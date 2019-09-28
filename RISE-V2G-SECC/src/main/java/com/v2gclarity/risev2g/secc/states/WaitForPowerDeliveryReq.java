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
package com.v2gclarity.risev2g.secc.states;

import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PMaxScheduleEntryType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PowerDeliveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ProfileEntryType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.RelativeTimeIntervalType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
					getCommSessionContext().setChargeProgressStarted(true); // see [V2G2-812]
					
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
				setMandatoryFieldsForFailedRes(powerDeliveryRes, powerDeliveryRes.getResponseCode());
			}
		} else {
			if (powerDeliveryRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new PowerDeliveryResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, powerDeliveryRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(powerDeliveryRes, powerDeliveryRes.getResponseCode());
			}
		}
		
		return getSendMessage(powerDeliveryRes, V2GMessages.NONE, powerDeliveryRes.getResponseCode());
	}
	
	
	public boolean isResponseCodeOK(PowerDeliveryReqType powerDeliveryReq) {
		SAScheduleTupleType chosenSASchedule = getChosenSASCheduleTuple(powerDeliveryReq.getSAScheduleTupleID());
		
		// This debug message is helpful to determine why the EV might not send a ChargingProfile (parameter is optional and should only be left out if ChargeProgress is set to Stop)
		getLogger().debug("ChargeProgress is set to " + powerDeliveryReq.getChargeProgress());
		
		if (powerDeliveryReq.getChargeProgress().equals(ChargeProgressType.RENEGOTIATE) && 
				!getCommSessionContext().isChargeProgressStarted()) {
				getLogger().error("EVCC wants to renegotiate, but charge progress has not started yet (no "
								+ "PowerDeliveryReq with ChargeProgress=START has been received before)");
				powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED);
				return false;
		}
		
		if (chosenSASchedule == null) {
			getLogger().warn("Chosen SAScheduleTupleID in PowerDeliveryReq is null, but parameter is mandatory");
			powerDeliveryRes.setResponseCode(ResponseCodeType.FAILED_TARIFF_SELECTION_INVALID);
			return false;
		}
		
		// Important to call this AFTER checking for valid tariff selection because of possible null-value!
		// Check ChargingProfile only if EV wants to start (not stop or renegotiate) the charging process
		if (powerDeliveryReq.getChargeProgress().equals(ChargeProgressType.START) && 
			!isChargingProfileValid(chosenSASchedule, powerDeliveryReq.getChargingProfile())) {
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
	
	
	protected void setEVSEStatus(PowerDeliveryResType powerDeliveryRes) {
		// In case the SECC received a PowerDeliveryReq before a PaymentServiceSelectionReq, the field requestedEnergyTransferMode will be null. So we need to check for it.
		if (getCommSessionContext().getRequestedEnergyTransferMode() != null && getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
			/*
			 * The MiscUtils method getJAXBElement() cannot be used here because of the difference in the
			 * class name (ACEVSEStatus) and the name in the XSD (AC_EVSEStatus)
			 */
			JAXBElement<ACEVSEStatusType> jaxbEVSEStatus = new JAXBElement<>(new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVSEStatus"), 
					ACEVSEStatusType.class, 
					getCommSessionContext().getACEvseController().getACEVSEStatus(EVSENotificationType.NONE));
			powerDeliveryRes.setEVSEStatus(jaxbEVSEStatus);
		} else if (getCommSessionContext().getRequestedEnergyTransferMode() != null && getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
			/*
			 * The MiscUtils method getJAXBElement() cannot be used here because of the difference in the
			 * class name (DCEVSEStatus) and the name in the XSD (DC_EVSEStatus)
			 */
			JAXBElement<DCEVSEStatusType> jaxbACEVSEStatus = new JAXBElement<>(new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVSEStatus"), 
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
		long profileEntryStart = 0;
		double profileEntryPower = 0;
		long pMaxScheduleIntervalStart = 0;
		long pMaxScheduleIntervalEnd = 0;
		double pMaxScheduleIntervalPMax = 0;
		ArrayList<PMaxScheduleEntryType> limit = (ArrayList<PMaxScheduleEntryType>) chosenSAScheduleTuple.getPMaxSchedule().getPMaxScheduleEntry();
		
		if (chargingProfile == null) {
			getLogger().error("ChargingProfile is empty (null)");
			return false;
		}
		
		for (ProfileEntryType profileEntry : chargingProfile.getProfileEntry()) {
			if (profileEntry.getChargingProfileEntryMaxNumberOfPhasesInUse() != null && profileEntry.getChargingProfileEntryMaxNumberOfPhasesInUse() == 2) {
				getLogger().error("Parameter MaxNumberOfPhasesInUse of one ChargingProfile entry element is 2 which is not allowed. Only 1 or 3 are valid values.");
				return false;
			}
				
			profileEntryStart = profileEntry.getChargingProfileEntryStart();
			profileEntryPower = profileEntry.getChargingProfileEntryMaxPower().getValue() * 
							    Math.pow(10, profileEntry.getChargingProfileEntryMaxPower().getMultiplier());
			
			for (int i=0; i < limit.size(); i++) {
				pMaxScheduleIntervalStart = ((RelativeTimeIntervalType) limit.get(i).getTimeInterval().getValue()).getStart();
				
				try {
					pMaxScheduleIntervalEnd = ((RelativeTimeIntervalType) limit.get(i+1).getTimeInterval().getValue()).getStart();
				} catch (IndexOutOfBoundsException e) {
					if ( ((RelativeTimeIntervalType) limit.get(i).getTimeInterval().getValue()).getDuration() != 0)
						pMaxScheduleIntervalEnd = pMaxScheduleIntervalStart + ((RelativeTimeIntervalType) limit.get(i).getTimeInterval().getValue()).getDuration();
					else
						pMaxScheduleIntervalEnd = Long.MAX_VALUE;
				}
				
				pMaxScheduleIntervalPMax = limit.get(i).getPMax().getValue() * Math.pow(10, limit.get(i).getPMax().getMultiplier());
				
				// TODO Find out how to deal with grace time period defined by [V2G2-833] and [V2G2-834] that contradicts [V2G2-777]
				if (profileEntryStart >= pMaxScheduleIntervalStart && profileEntryStart < pMaxScheduleIntervalEnd) {
					if (profileEntryPower > pMaxScheduleIntervalPMax) {
						getLogger().error("ChargingProfile entry element starting at " + profileEntryStart + 
										  "s exceeds power limit. Limit is " + pMaxScheduleIntervalPMax + 
										  " W, ChargingProfile entry's max power value is " + profileEntryPower + " W" );
						return false;
					} else
						break;
				} else
					continue;
			}
		}
		
		return true;
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return powerDeliveryRes;
	}
}
