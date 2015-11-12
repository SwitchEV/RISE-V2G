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
package org.eclipse.risev2g.secc.states;

import org.eclipse.risev2g.secc.evseController.IACEVSEController;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingStatusReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingStatusResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeterInfoType;

public class WaitForChargingStatusReq extends ServerState {

	private ChargingStatusResType chargingStatusRes;
	
	public WaitForChargingStatusReq(
			V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		chargingStatusRes = new ChargingStatusResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ChargingStatusReqType.class, chargingStatusRes)) {
			chargingStatusRes.setEVSEID(getCommSessionContext().getACEvseController().getEvseID());
			chargingStatusRes.setSAScheduleTupleID(getCommSessionContext().getChosenSAScheduleTuple());
			
			/*
			 * TODO check if a renegotiation is wanted or not
			 * Change EVSENotificationType to NONE if you want more than one charge loop iteration, 
			 * but then make sure the EV is stopping the charge loop
			 */
			chargingStatusRes.setACEVSEStatus(
					((IACEVSEController) getCommSessionContext().getACEvseController())
					.getACEVSEStatus(EVSENotificationType.STOP_CHARGING)  
					);
			
			// Optionally indicate that the EVCC is required to send a MeteringReceiptReq message 
			// (only in PnC mode according to [V2G2-691])
			chargingStatusRes.setReceiptRequired(false);
			
			// Optionally set EVSEMaxCurrent (if NOT in AC PnC mode) -> check with AC station
			
			MeterInfoType meterInfo = getCommSessionContext().getACEvseController().getMeterInfo();
			chargingStatusRes.setMeterInfo(meterInfo);
			getCommSessionContext().setSentMeterInfo(meterInfo);
						
			/*
			 * TODO it is unclear how the EV should react if an EVSENotification = Renegotiate/Stop
			 * is sent as well as ReceiptRequired = true: is a PowerDeliveryReq oder a MeteringReceiptReq
			 * expected then?
			 */
			if (chargingStatusRes.isReceiptRequired()) {
				return getSendMessage(chargingStatusRes, V2GMessages.METERING_RECEIPT_REQ);
			} else {
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.CHARGING_STATUS_REQ);
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.POWER_DELIVERY_REQ);
				
				return getSendMessage(chargingStatusRes, V2GMessages.FORK);
			}
		} 
		
		return getSendMessage(chargingStatusRes, V2GMessages.NONE);
	}

}
