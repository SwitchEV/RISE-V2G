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

import org.eclipse.risev2g.evcc.evController.IDCEVController;
import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.CPStates;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingSessionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingStatusReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.WeldingDetectionReqType;

public class WaitForPowerDeliveryRes extends ClientState {

	public WaitForPowerDeliveryRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PowerDeliveryResType.class)) {
			PowerDeliveryResType powerDeliveryRes = 
					(PowerDeliveryResType) ((V2GMessage) message).getBody().getBodyElement().getValue();
			
			/*
			 * TODO clarification needed on how to react on EVSENotification
			 * 
			 * EVSENotification=RE_NEGOATION is ignored, because an SECC triggered renegotiation is only
			 * to be reacted on in the messages
			 * - ChargingStatusRes
			 * - MeteringReceiptRes
			 * - CurrentDemandRes
			 * 
			 * But how to react on EVSENotification=STOP?
			 */
			
			if (getCommSessionContext().isRenegotiationRequested()) {
				getCommSessionContext().setRenegotiationRequested(false);
				return getSendMessage(getChargeParameterDiscoveryReq(), V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES);
			} else if (getCommSessionContext().isStopChargingRequested()) {
				return getSendMessage(ChargingSessionType.TERMINATE, true);
			} else if (getCommSessionContext().isPausingV2GCommSession()) {
				return getSendMessage(ChargingSessionType.PAUSE, false);
			} else {
				if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
					ChargingStatusReqType chargingStatusReq = new ChargingStatusReqType();
					return getSendMessage(chargingStatusReq, V2GMessages.CHARGING_STATUS_RES);
				} else {
					return getSendMessage(getCurrentDemandReq(), V2GMessages.CURRENT_DEMAND_RES);
				}
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
	
	
	private ReactionToIncomingMessage getSendMessage(ChargingSessionType chargingSessionType, boolean stopChargingRequested) {
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
			// CP state C signaling BEFORE sending WeldingDetectionReq message in DC
			if (getCommSessionContext().getEvController().setCPState(CPStates.STATE_C)) {
				WeldingDetectionReqType weldingDetectionReq = new WeldingDetectionReqType();
				DCEVStatusType dcEVStatus = ((IDCEVController) getCommSessionContext().getEvController()).getDCEVStatus();
				weldingDetectionReq.setDCEVStatus(dcEVStatus);
				
				return getSendMessage(weldingDetectionReq, V2GMessages.WELDING_DETECTION_RES);
			} else {
				return new TerminateSession("CP state C not ready (current state = " + 
						getCommSessionContext().getEvController().getCPState() +
						")");
			}
		} else {
			if (stopChargingRequested) getCommSessionContext().setStopChargingRequested(false);
			else getCommSessionContext().setPausingV2GCommSession(false);
			
			return getSendMessage(getSessionStopReq(chargingSessionType), 
								  V2GMessages.SESSION_STOP_RES, "(ChargingSession = " + 
								  chargingSessionType.toString() + ")");
		}
	}
}
