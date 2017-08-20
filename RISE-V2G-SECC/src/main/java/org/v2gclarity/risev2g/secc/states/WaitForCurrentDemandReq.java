/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-207  V2G Clarity (Dr.-Ing. Marc Mültin) 
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
package org.v2gclarity.risev2g.secc.states;

import org.v2gclarity.risev2g.secc.evseController.IDCEVSEController;
import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CurrentDemandReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CurrentDemandResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCurrentDemandReq extends ServerState {

	private CurrentDemandResType currentDemandRes;
	
	public WaitForCurrentDemandReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		currentDemandRes = new CurrentDemandResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CurrentDemandReqType.class, currentDemandRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			CurrentDemandReqType currentDemandReq = 
					(CurrentDemandReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			IDCEVSEController evseController = (IDCEVSEController) getCommSessionContext().getDCEvseController();
			
			evseController.setEVMaximumCurrentLimit(currentDemandReq.getEVMaximumCurrentLimit());
			evseController.setEVMaximumVoltageLimit(currentDemandReq.getEVMaximumVoltageLimit());
			evseController.setEVMaximumPowerLimit(currentDemandReq.getEVMaximumPowerLimit());
			evseController.setTargetCurrent(currentDemandReq.getEVTargetCurrent());
			evseController.setTargetVoltage(currentDemandReq.getEVTargetVoltage());
			
			// TODO how to deal with the remaining parameters of currentDemandReq?
			
			/*
			 * TODO check if a renegotiation is wanted or not
			 * Change EVSENotificationType to NONE if you want more than one charge loop iteration, 
			 * but then make sure the EV is stopping the charge loop
			 */
			currentDemandRes.setDCEVSEStatus(evseController.getDCEVSEStatus(EVSENotificationType.NONE));
			
			currentDemandRes.setEVSECurrentLimitAchieved(evseController.isEVSECurrentLimitAchieved());
			currentDemandRes.setEVSEVoltageLimitAchieved(evseController.isEVSEVoltageLimitAchieved());
			currentDemandRes.setEVSEPowerLimitAchieved(evseController.isEVSEPowerLimitAchieved());
			currentDemandRes.setEVSEID(evseController.getEvseID());
			currentDemandRes.setEVSEMaximumCurrentLimit(evseController.getEVSEMaximumCurrentLimit());
			currentDemandRes.setEVSEMaximumVoltageLimit(evseController.getEVSEMaximumVoltageLimit());
			currentDemandRes.setEVSEMaximumPowerLimit(evseController.getEVSEMaximumPowerLimit());
			currentDemandRes.setEVSEPresentCurrent(evseController.getPresentCurrent());
			currentDemandRes.setEVSEPresentVoltage(evseController.getPresentVoltage());
			currentDemandRes.setMeterInfo(evseController.getMeterInfo());
			getCommSessionContext().setSentMeterInfo(evseController.getMeterInfo());
			currentDemandRes.setSAScheduleTupleID(getCommSessionContext().getChosenSAScheduleTuple());
			
			// TODO how to determine if a receipt is required or not?
			currentDemandRes.setReceiptRequired(true);
			
			if (currentDemandRes.isReceiptRequired()) {
				return getSendMessage(currentDemandRes, V2GMessages.METERING_RECEIPT_REQ);
			} else {
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
				.getAllowedRequests().add(V2GMessages.CURRENT_DEMAND_REQ);
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
				.getAllowedRequests().add(V2GMessages.POWER_DELIVERY_REQ);
				
				return getSendMessage(currentDemandRes, V2GMessages.FORK);
			}
		} else {
			if (currentDemandRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new CurrentDemandResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, currentDemandRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(currentDemandRes, currentDemandRes.getResponseCode());
			}
		}
		
		return getSendMessage(currentDemandRes, V2GMessages.NONE, currentDemandRes.getResponseCode());
	}

	@Override
	public BodyBaseType getResponseMessage() {
		return currentDemandRes;
	}

}
