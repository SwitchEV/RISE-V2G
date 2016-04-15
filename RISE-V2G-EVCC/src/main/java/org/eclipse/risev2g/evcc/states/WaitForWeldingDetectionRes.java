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
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingSessionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.WeldingDetectionResType;

public class WaitForWeldingDetectionRes extends ClientState {

	public WaitForWeldingDetectionRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, WeldingDetectionResType.class)) {
			WeldingDetectionResType weldingDetectionRes = 
					(WeldingDetectionResType) ((V2GMessage) message).getBody().getBodyElement().getValue();
			
			/*
			 * [V2G2-620] states that the EVCC shall send a WeldingDetectionReq again if the
			 * Welding Detection function has not finished on EV side. But how is this checked?
			 * 
			 * How to react on DCEVSEStatus values?
			 */
			
			if (getCommSessionContext().isPausingV2GCommSession()) {
				getCommSessionContext().setPausingV2GCommSession(false);
				
				return getSendMessage(getSessionStopReq(ChargingSessionType.PAUSE), 
						  V2GMessages.SESSION_STOP_RES, "(ChargingSession = " + 
						  ChargingSessionType.PAUSE.toString() + ")");
			} else {
				getCommSessionContext().setStopChargingRequested(false);
				
				return getSendMessage(getSessionStopReq(ChargingSessionType.TERMINATE), 
						  V2GMessages.SESSION_STOP_RES, "(ChargingSession = " + 
						  ChargingSessionType.TERMINATE.toString() + ")");
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}

}
