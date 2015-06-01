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
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingStatusReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeteringReceiptResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
