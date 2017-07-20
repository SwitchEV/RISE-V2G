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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PowerDeliveryReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForPowerDeliveryReq extends ServerState {

	private PowerDeliveryResType powerDeliveryRes;
	
	public WaitForPowerDeliveryReq(
			V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		powerDeliveryRes = new PowerDeliveryResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PowerDeliveryReqType.class, powerDeliveryRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			PowerDeliveryReqType powerDeliveryReq = (PowerDeliveryReqType) v2gMessageReq.getBody().getBodyElement().getValue();

			if (isResponseCodeOK(powerDeliveryReq)) {
				getCommSessionContext().setChosenSAScheduleTuple(powerDeliveryReq.getSAScheduleTupleID());
				
				// For debugging purposes, log the ChargeProgress value
				getLogger().debug("ChargeProgress of PowerDeliveryReq set to '" + 
								  powerDeliveryReq.getChargeProgress().toString() + "'");
				
				// TODO regard [V2G2-866]
				
				setEVSEStatus(powerDeliveryRes);
				
				if (powerDeliveryReq.getChargeProgress().equals(ChargeProgressType.START)) {
					if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC"))
						return getSendMessage(powerDeliveryRes, V2GMessages.CHARGING_STATUS_REQ);
					else
						return getSendMessage(powerDeliveryRes, V2GMessages.CURRENT_DEMAND_REQ);
				} else if (powerDeliveryReq.getChargeProgress().equals(ChargeProgressType.STOP)) {
					if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
						return getSendMessage(powerDeliveryRes, V2GMessages.SESSION_STOP_REQ);
					} else {
						((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
						.getAllowedRequests().add(V2GMessages.WELDING_DETECTION_REQ);
						((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
						.getAllowedRequests().add(V2GMessages.SESSION_STOP_REQ);
						
						return getSendMessage(powerDeliveryRes, V2GMessages.FORK);
					}
				} else {
					return getSendMessage(powerDeliveryRes, V2GMessages.CHARGE_PARAMETER_DISCOVERY_REQ);
				}
			} else {
				getLogger().error("Response code '" + powerDeliveryRes.getResponseCode() + "' will be sent");
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(powerDeliveryRes, V2GMessages.NONE);
	}
	
	
	public boolean isResponseCodeOK(PowerDeliveryReqType powerDeliveryReq) {
		SAScheduleTupleType chosenSASchedule = getChosenSASCheduleTuple(powerDeliveryReq.getSAScheduleTupleID());
		
		if (chosenSASchedule == null) {
			getLogger().warn("Chosen SAScheduleTupleID in PowerDeliveryReq is null, but parameter is mandatory");
			powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED_TARIFF_SELECTION_INVALID);
			return false;
		}
		
		// Important to call this AFTER checking for valid tariff selection because of possible null-value!
		if (!isChargingProfileValid(chosenSASchedule, powerDeliveryReq.getChargingProfile())) {
			powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED_CHARGING_PROFILE_INVALID);
			return false;
		}
		
		// Not sure if these values are the ones to monitor when checking for FAILED_POWER_DELIVERY_NOT_APPLIED 
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
			if (getCommSessionContext().getACEvseController().getACEVSEStatus(null).isRCD()) {
				getLogger().error("RCD has detected an error");
				powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED_POWER_DELIVERY_NOT_APPLIED);
				return false;
			}
		} else {
			DCEVSEStatusCodeType dcEVSEStatusCode = 
					getCommSessionContext().getDCEvseController().getDCEVSEStatus(null).getEVSEStatusCode();
			
			if (dcEVSEStatusCode.equals(DCEVSEStatusCodeType.EVSE_NOT_READY) ||
				dcEVSEStatusCode.equals(DCEVSEStatusCodeType.EVSE_SHUTDOWN) ||
				dcEVSEStatusCode.equals(DCEVSEStatusCodeType.EVSE_EMERGENCY_SHUTDOWN) || 
				dcEVSEStatusCode.equals(DCEVSEStatusCodeType.EVSE_MALFUNCTION)) {
				getLogger().error("EVSE status code is '" + dcEVSEStatusCode.toString() + "'");
				powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED_POWER_DELIVERY_NOT_APPLIED);
				return false;
			}
					
		}
		
		if ((powerDeliveryReq.getChargeProgress().equals(ChargeProgressType.START) &&
			 !getCommSessionContext().getEvseController().closeContactor()) ||
			(powerDeliveryReq.getChargeProgress().equals(ChargeProgressType.STOP) &&
			 !getCommSessionContext().getEvseController().openContactor())) {
			powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED_CONTACTOR_ERROR);
			return false;
		}
		
		return true;
	}
	
	
	private void setEVSEStatus(PowerDeliveryResType powerDeliveryRes) {
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
			/*
			 * The MiscUtils method getJAXBElement() cannot be used here because of the difference in the
			 * class name (ACEVSEStatus) and the name in the XSD (AC_EVSEStatus)
			 */
			JAXBElement jaxbEVSEStatus = new JAXBElement(new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVSEStatus"), 
					ACEVSEStatusType.class, 
					getCommSessionContext().getACEvseController().getACEVSEStatus(EVSENotificationType.NONE));
			powerDeliveryRes.setEVSEStatus(jaxbEVSEStatus);
		} else if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
			/*
			 * The MiscUtils method getJAXBElement() cannot be used here because of the difference in the
			 * class name (DCEVSEStatus) and the name in the XSD (DC_EVSEStatus)
			 */
			JAXBElement jaxbACEVSEStatus = new JAXBElement(new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVSEStatus"), 
					DCEVSEStatusType.class, 
					getCommSessionContext().getDCEvseController().getDCEVSEStatus(EVSENotificationType.NONE));
			powerDeliveryRes.setEVSEStatus(jaxbACEVSEStatus);
		} else {
			getLogger().warn("RequestedEnergyTransferMode '" + getCommSessionContext().getRequestedEnergyTransferMode().toString() + 
										"is neither of type AC nor DC");
		}
	}
	
	
	private SAScheduleTupleType getChosenSASCheduleTuple(short chosenSAScheduleTupleID) {
		for (SAScheduleTupleType saSchedule : getCommSessionContext().getSaSchedules().getSAScheduleTuple()) {
			if (saSchedule.getSAScheduleTupleID() == chosenSAScheduleTupleID) return saSchedule;
		}
		return null;
	}
	
	
	private boolean isChargingProfileValid(
			SAScheduleTupleType chosenSAScheduleTuple, 
			ChargingProfileType chargingProfile) {
		// TODO check for validity of charging profile
		
		return true;
	}
	

	@Override
	protected void setMandatoryFieldsForFailedRes() {
		setEVSEStatus(powerDeliveryRes);
	}
}
