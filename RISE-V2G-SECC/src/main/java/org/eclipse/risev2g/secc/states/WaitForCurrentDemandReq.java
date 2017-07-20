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
package org.eclipse.risev2g.secc.states;

import org.eclipse.risev2g.secc.evseController.IDCEVSEController;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CurrentDemandReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CurrentDemandResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(currentDemandRes, V2GMessages.NONE);
	}

	@Override
	protected void setMandatoryFieldsForFailedRes() {
		IDCEVSEController evseController = (IDCEVSEController) getCommSessionContext().getDCEvseController();
		
		PhysicalValueType physicalValueType = new PhysicalValueType();
		physicalValueType.setMultiplier(new Byte("0"));
		physicalValueType.setUnit(UnitSymbolType.V);  // does not matter which unit symbol if FAILED response is sent
		physicalValueType.setValue((short) 1);
		
		currentDemandRes.setDCEVSEStatus(evseController.getDCEVSEStatus(EVSENotificationType.NONE));
		currentDemandRes.setEVSEPresentVoltage(physicalValueType);
		currentDemandRes.setEVSEPresentCurrent(physicalValueType);
		currentDemandRes.setEVSECurrentLimitAchieved(false);
		currentDemandRes.setEVSEVoltageLimitAchieved(false);
		currentDemandRes.setEVSEPowerLimitAchieved(false);
		currentDemandRes.setEVSEID(evseController.getEvseID());
		currentDemandRes.setSAScheduleTupleID((short) 1); 
	}

}
