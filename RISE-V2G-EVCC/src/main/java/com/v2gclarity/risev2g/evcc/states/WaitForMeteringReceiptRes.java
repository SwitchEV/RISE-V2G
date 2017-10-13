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
package com.v2gclarity.risev2g.evcc.states;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingStatusReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeteringReceiptResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForMeteringReceiptRes extends ClientState {

	private boolean acCharging;
	
	public WaitForMeteringReceiptRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, MeteringReceiptResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			MeteringReceiptResType meteringReceiptRes = 
					(MeteringReceiptResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			EVSENotificationType evseNotification = null;
			
			if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
				setAcCharging(true);
				evseNotification = ((ACEVSEStatusType) meteringReceiptRes.getEVSEStatus().getValue()).getEVSENotification();
			} else if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
				setAcCharging(false);
				evseNotification = ((DCEVSEStatusType) meteringReceiptRes.getEVSEStatus().getValue()).getEVSENotification();
			} else {
				return new TerminateSession("RequestedEnergyTransferMode '" + getCommSessionContext().getRequestedEnergyTransferMode().toString() + 
											"is neither of type AC nor DC");
			}
			
			switch (evseNotification) {
			case STOP_CHARGING:
				getCommSessionContext().setStopChargingRequested(true);
				return getSendMessage(getPowerDeliveryReq(ChargeProgressType.STOP), 
									  V2GMessages.POWER_DELIVERY_RES,
									  " (ChargeProgress = STOP_CHARGING)");
			case RE_NEGOTIATION:
				getCommSessionContext().setRenegotiationRequested(true);
				return getSendMessage(getPowerDeliveryReq(ChargeProgressType.RENEGOTIATE), 
									  V2GMessages.POWER_DELIVERY_RES,
						  			  " (ChargeProgress = RE_NEGOTIATION)");
			default:
				// TODO regard [V2G2-305] (new SalesTariff if EAmount not yet met and tariff finished)
				
				// TODO check somehow if charging is stopped by EV, otherwise send new ChargingStatusReq/CurrentDemandReq
				
				if (isAcCharging()) {
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
	
	
	private boolean isAcCharging() {
		return acCharging;
	}
	

	private void setAcCharging(boolean acCharging) {
		this.acCharging = acCharging;
	}
}
