/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017  V2G Clarity (Dr.-Ing. Marc Mültin)
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
package com.v2gclarity.risev2g.secc.states;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingStatusReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingStatusResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;

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
					getCommSessionContext().getACEvseController().getACEVSEStatus(EVSENotificationType.NONE)
			);
			
			// Optionally indicate that the EVCC is required to send a MeteringReceiptReq message 
			if (getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.EXTERNAL_PAYMENT)) {
				// In EIM, there is never a MeteringReceiptReq/-Res message pair, therefore it is set to false here
				chargingStatusRes.setReceiptRequired(false);
			} else {
				// Only in PnC mode according to [V2G2-691]
				chargingStatusRes.setReceiptRequired(true);
			}
			
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
		} else {
			if (chargingStatusRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new ChargingStatusResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, chargingStatusRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(chargingStatusRes, chargingStatusRes.getResponseCode());
			}
		}
		
		return getSendMessage(chargingStatusRes, V2GMessages.NONE, chargingStatusRes.getResponseCode());
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return chargingStatusRes;
	}

}
