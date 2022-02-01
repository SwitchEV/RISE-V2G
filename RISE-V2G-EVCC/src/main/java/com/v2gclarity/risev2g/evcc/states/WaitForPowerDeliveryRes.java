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

import com.v2gclarity.risev2g.evcc.evController.IDCEVController;
import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.CPStates;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingSessionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingStatusReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.WeldingDetectionReqType;

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
				// In DC charging, we need to switch to state B during renegotiation because we need to go through CableCheckReq and PreChargeReq again for which state B is required
				if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
					getCommSessionContext().setChangeToState(CPStates.STATE_B);
				}
				ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq = getChargeParameterDiscoveryReq();

				/*
				 * Save this request in case the ChargeParameterDiscoveryRes indicates that the EVSE is
				 * still processing. Then this request can just be resent instead of asking the EV again.
				 */
				getCommSessionContext().setChargeParameterDiscoveryReq(chargeParameterDiscoveryReq);
				
				return getSendMessage(chargeParameterDiscoveryReq, V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES);
			} else if (getCommSessionContext().getChargingSession() != null && 
					   getCommSessionContext().getChargingSession() == ChargingSessionType.TERMINATE) {
				return getSendMessage(ChargingSessionType.TERMINATE);
			} else if (getCommSessionContext().getChargingSession() != null && 
					   getCommSessionContext().getChargingSession() == ChargingSessionType.PAUSE) {
				return getSendMessage(ChargingSessionType.PAUSE);
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
	
	
	private ReactionToIncomingMessage getSendMessage(ChargingSessionType chargingSessionType) {
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
			// CP state B signaling BEFORE sending WeldingDetectionReq message in DC
			if (getCommSessionContext().getEvController().setCPState(CPStates.STATE_B)) {
				WeldingDetectionReqType weldingDetectionReq = new WeldingDetectionReqType();
				DCEVStatusType dcEVStatus = ((IDCEVController) getCommSessionContext().getEvController()).getDCEVStatus();
				weldingDetectionReq.setDCEVStatus(dcEVStatus);
				
				return getSendMessage(weldingDetectionReq, V2GMessages.WELDING_DETECTION_RES);
			} else {
				return new TerminateSession("CP state B not ready (current state = " + 
						getCommSessionContext().getEvController().getCPState() +
						")");
			}
		} else {	
			return getSendMessage(getSessionStopReq(chargingSessionType), 
								  V2GMessages.SESSION_STOP_RES, "(ChargingSession = " + 
								  chargingSessionType.toString() + ")");
		}
	}
}
