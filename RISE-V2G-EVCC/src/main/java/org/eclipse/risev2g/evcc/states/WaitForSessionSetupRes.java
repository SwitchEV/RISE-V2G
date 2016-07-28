/*******************************************************************************
 *  Copyright (c) 2016 Dr.-Ing. Marc Mültin.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Dr.-Ing. Marc Mültin - initial API and implementation and initial documentation
 *******************************************************************************/
package org.eclipse.risev2g.evcc.states;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.ByteUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
				getCommSessionContext().setSessionID(receivedSessionID);
				getLogger().debug("Negotiated session ID is " + ByteUtils.toLongFromByteArray(receivedSessionID));
				getCommSessionContext().setOldSessionJoined(false);
				getCommSessionContext().setEvseID(sessionSetupRes.getEVSEID());
				// EVSETimeStamp is optional
				if (sessionSetupRes.getEVSETimeStamp() != null)	getCommSessionContext().setEvseTimeStamp(sessionSetupRes.getEVSETimeStamp());
			} else if (sessionSetupRes.getResponseCode().equals(ResponseCodeType.OK_OLD_SESSION_JOINED)) {
				getLogger().debug("Previous charging session joined (session ID = " + ByteUtils.toLongFromByteArray(receivedSessionID) + ")");
				
				/*
				 * Mark that the old session was joined in order to resend
				 * - SelectedPaymentOption and
				 * - RequestedEnergyTransferMode
				 * according to 8.4.2. Those values should be persisted in the properties file.
				 */
				getCommSessionContext().setOldSessionJoined(true);
				getCommSessionContext().setEvseID(sessionSetupRes.getEVSEID());
				getCommSessionContext().setEvseTimeStamp(sessionSetupRes.getEVSETimeStamp());
			} else {
				getCommSessionContext().setOldSessionJoined(false);
				getLogger().error("No negative response code received, but positive response code '" +
								  sessionSetupRes.getResponseCode().toString() + "' is " +
								  "neither OK_NEW_SESSION_ESTABLISHED nor OK_OLD_SESSION_JOINED");
				return new TerminateSession("Positive response code invalid in state WaitForSessionSetupRes");
			}
			
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
