/*******************************************************************************
 *  Copyright (c) 2015 Marc Mültin (Chargepartner GmbH).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Dr.-Ing. Marc Mültin (Chargepartner GmbH) - initial API and implementation and initial documentation
 *******************************************************************************/
package org.eclipse.risev2g.evcc.states;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.misc.TimeRestrictions;
import org.eclipse.risev2g.shared.utils.MiscUtils;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.AppProtocolType;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionSetupReqType;

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
				return new TerminateSession("No supported appProtocol found (positive response code received, but no valid schemaID)");
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
