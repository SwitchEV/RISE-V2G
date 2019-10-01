/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
package com.v2gclarity.risev2g.evcc.states;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForSessionSetupRes extends ClientState {

	public WaitForSessionSetupRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, SessionSetupResType.class)) {
			byte[] receivedSessionID = ((V2GMessage) message).getHeader().getSessionID();
			SessionSetupResType sessionSetupRes = 
					(SessionSetupResType) ((V2GMessage) message).getBody().getBodyElement().getValue();
					
			if (sessionSetupRes.getResponseCode().equals(ResponseCodeType.OK_NEW_SESSION_ESTABLISHED)) {
				getLogger().debug("Negotiated session ID is " + ByteUtils.toHexString(receivedSessionID));
				getCommSessionContext().setOldSessionJoined(false);
			} else if (sessionSetupRes.getResponseCode().equals(ResponseCodeType.OK_OLD_SESSION_JOINED)) {
				getLogger().debug("Previous charging session joined (session ID = " + ByteUtils.toHexString(receivedSessionID) + ")");
				
				/*
				 * Mark that the old session was joined in order to resend
				 * - SelectedPaymentOption and
				 * - RequestedEnergyTransferMode
				 * according to 8.4.2. Those values should be persisted in the properties file.
				 */
				getCommSessionContext().setOldSessionJoined(true);
			} else {
				getCommSessionContext().setOldSessionJoined(false);
				getLogger().error("No negative response code received, but positive response code '" +
								  sessionSetupRes.getResponseCode().toString() + "' is " +
								  "neither OK_NEW_SESSION_ESTABLISHED nor OK_OLD_SESSION_JOINED");
				return new TerminateSession("Positive response code invalid in state WaitForSessionSetupRes");
			}
			
			getCommSessionContext().setSessionID(receivedSessionID);
			getCommSessionContext().setEvseID(sessionSetupRes.getEVSEID());
			// EVSETimeStamp is optional
			if (sessionSetupRes.getEVSETimeStamp() != null) 
				getCommSessionContext().setEvseTimeStamp(sessionSetupRes.getEVSETimeStamp());
			
			ServiceDiscoveryReqType serviceDiscoveryReq = new ServiceDiscoveryReqType();
			
			/*
			 * If it is desired to restrict the services to a certain scope and/or category (optional),
			 * then this is the place to do it.
			 */
			
			return getSendMessage(serviceDiscoveryReq, V2GMessages.SERVICE_DISCOVERY_RES);
		} else {
			getCommSessionContext().setOldSessionJoined(false);
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
