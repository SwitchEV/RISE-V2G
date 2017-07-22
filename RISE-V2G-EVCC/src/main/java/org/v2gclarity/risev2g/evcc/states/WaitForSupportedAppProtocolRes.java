/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright 2017 Dr.-Ing. Marc Mültin (V2G Clarity)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.v2gclarity.risev2g.evcc.states;

import org.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import org.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import org.v2gclarity.risev2g.shared.utils.MiscUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.AppProtocolType;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionSetupReqType;

public class WaitForSupportedAppProtocolRes extends ClientState {

	public WaitForSupportedAppProtocolRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (message instanceof SupportedAppProtocolRes) {
			getLogger().debug("SupportedAppProtocolRes received");
			SupportedAppProtocolRes supportedAppProtocolRes = (SupportedAppProtocolRes) message;
			SessionSetupReqType sessionSetupReq = new SessionSetupReqType();
			boolean match = false;
			
			if (supportedAppProtocolRes.getResponseCode().equals(ResponseCodeType.OK_SUCCESSFUL_NEGOTIATION) ||
				supportedAppProtocolRes.getResponseCode().equals(ResponseCodeType.OK_SUCCESSFUL_NEGOTIATION_WITH_MINOR_DEVIATION)) {
				// Check which schemaID is to be chosen
				for (AppProtocolType evccAppProtocol : getCommSessionContext().getSupportedAppProtocols()) {
					if (evccAppProtocol.getSchemaID() == supportedAppProtocolRes.getSchemaID()) {
						/*
						 * If the EVCC supports more than one appProtocol or even minor deviations, 
						 * then the EVCC must in some way be able to react accordingly to those different  
						 * versions. Currently, only IS version of April 2014 is supported (see [V2G2-098]).
						 */
						
						getCommSessionContext().setChosenAppProtocol(evccAppProtocol);
						match = true;
						break;
					}
				}
			} else {
				return new TerminateSession("No supported appProtocol found (negative response code)");
			}
			
			// Double check if - despite an OK_ response code - a valid schemaID has been sent
			if (match) {
				sessionSetupReq.setEVCCID(MiscUtils.getMacAddress());
				
				/*
				 * The session ID is taken from the properties file. If a previous charging session has been
				 * paused, then the previously valid session ID has been written to the properties file
				 * in order persist the value when the ISO/IEC 15118 controller is shut down for energy
				 * saving reasons.
				 * The initial value for a completely new charging session must be 0.
				 */
				long sessionID = (long) MiscUtils.getPropertyValue("SessionID");
				getCommSessionContext().setSessionID(
						getCommSessionContext().generateSessionIDFromValue(sessionID)
				);
			} else {
				return new TerminateSession("No supported appProtocol found (positive response code received, " + 
											"but no valid schemaID. Received schema ID is: " + 
											supportedAppProtocolRes.getSchemaID());
			}
			
			return getSendMessage(sessionSetupReq, V2GMessages.SESSION_SETUP_RES, (int) Math.min(
					TimeRestrictions.getV2G_EVCC_Msg_Timeout(V2GMessages.SESSION_SETUP_RES), 
					TimeRestrictions.V2G_EVCC_COMMUNICATION_SETUP_TIMEOUT - (System.currentTimeMillis() - getCommSessionContext().getV2gEVCCCommunicationSetupTimer())
					));
		} else {
			return new TerminateSession("Invalid message (" + message.getClass().getSimpleName() + 
	  		  		  					") at this state (" + this.getClass().getSimpleName() + ")");
		}
	}
}
