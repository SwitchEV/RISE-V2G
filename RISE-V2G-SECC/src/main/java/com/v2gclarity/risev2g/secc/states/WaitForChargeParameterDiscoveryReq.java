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

import com.v2gclarity.risev2g.secc.evseController.IACEVSEController;
import com.v2gclarity.risev2g.secc.evseController.IDCEVSEController;
import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForChargeParameterDiscoveryReq extends ServerState {

	private ChargeParameterDiscoveryResType chargeParameterDiscoveryRes;
	private boolean waitingForSchedule;
	
	public WaitForChargeParameterDiscoveryReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		chargeParameterDiscoveryRes = new ChargeParameterDiscoveryResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ChargeParameterDiscoveryReqType.class, chargeParameterDiscoveryRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq = 
					(ChargeParameterDiscoveryReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			if (isResponseCodeOK(chargeParameterDiscoveryReq)) {
				getCommSessionContext().setRequestedEnergyTransferMode(
						chargeParameterDiscoveryReq.getRequestedEnergyTransferMode());
				
				/*
				 * Request a new schedule in case of first ChargeParameterDiscoveryReq.
				 * If EVSEProcessingType.ONGOING was sent in previous ChargeParameterDiscoveryRes
				 * message, do not request again.
				 */
				if (!isWaitingForSchedule()) {
					// TODO we need a timeout mechanism here so that a response can be sent within 2s
					setWaitingForSchedule(true);
					
					// The max. number of PMaxScheduleEntries and SalesTariffEntries is 1024 if not provided otherwise by EVCC
					int maxEntriesSAScheduleTuple = (chargeParameterDiscoveryReq.getMaxEntriesSAScheduleTuple() != null) ? 
													chargeParameterDiscoveryReq.getMaxEntriesSAScheduleTuple() : 1024;
					
					Long departureTime = chargeParameterDiscoveryReq.getEVChargeParameter().getValue().getDepartureTime();
					
					getCommSessionContext().setSaSchedules(
							getCommSessionContext().getBackendInterface().getSAScheduleList(
									maxEntriesSAScheduleTuple, 
									(departureTime != null) ? departureTime.longValue() : 0,
									getXMLSignatureRefElements())
							);
				}
				
				// TODO An integration to a backend system which provides the SalesTariff would be needed here
				
				if (chargeParameterDiscoveryReq.getRequestedEnergyTransferMode().toString().startsWith("AC")) 
					chargeParameterDiscoveryRes.setEVSEChargeParameter(
							((IACEVSEController) getCommSessionContext().getACEvseController()).getACEVSEChargeParameter());
				else
					chargeParameterDiscoveryRes.setEVSEChargeParameter(
							((IDCEVSEController) getCommSessionContext().getDCEvseController()).getDCEVSEChargeParameter());
				
				/*
				 * TODO The next state depends as well on the EVSENotification:
				 * - NONE: PowerDeliveryReq (ChargeProgress of ChargeParameterDiscoveryReq = Start)
				 * - RENEGOTIATION: ChargeParameterDiscoveryReq
				 * - STOP: PowerDeliveryReq (ChargeProgress of ChargeParameterDiscoveryReq = Stop)
				 */
				
				if (getCommSessionContext().getSaSchedules() == null) {
					getLogger().debug("No SAScheduleList available yet, setting EVSEProcessingType to ONGOING");
					chargeParameterDiscoveryRes.setEVSEProcessing(EVSEProcessingType.ONGOING);
					return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.CHARGE_PARAMETER_DISCOVERY_REQ);
				} else {
					getLogger().debug("SAScheduleList has been provided");
					chargeParameterDiscoveryRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
					setWaitingForSchedule(false);
					chargeParameterDiscoveryRes.setSASchedules(
							getSASchedulesAsJAXBElement(getCommSessionContext().getSaSchedules()));
					
					/*
					 * Note 3 of [V2G2-905] states:
					 * "If the secondary actor is unaware of which authentication mode is used during EVCC-SECC 
					 * communication (EIM/ PnC), it can simply always sign the SalesTariff."
					 * 
					 * Therefore, we do not check here if PnC is used but just always sign the SalesTariff. 
					 * Without a real backend functionality, we must sign the SalesTariff by using the SecurityUtils
					 * class.
					 */
					//Set signing private key of Mobility Operator Sub-CA 2
					setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getMOSubCA2PrivateKey());
					
					if (chargeParameterDiscoveryReq.getRequestedEnergyTransferMode().toString().startsWith("AC")) 
						return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.POWER_DELIVERY_REQ);
					else 
						return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.CABLE_CHECK_REQ);
				}
			} else {
				setMandatoryFieldsForFailedRes(chargeParameterDiscoveryRes, chargeParameterDiscoveryRes.getResponseCode());
			}
		} else {
			if (chargeParameterDiscoveryRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new ChargeParameterDiscoveryResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, chargeParameterDiscoveryRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(chargeParameterDiscoveryRes, chargeParameterDiscoveryRes.getResponseCode());
			}
		}
		
		return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.NONE, chargeParameterDiscoveryRes.getResponseCode());
	}
	
	
	public boolean isResponseCodeOK(ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq) {
		// Check if the EV's requested EnergyTransferModeType is supported
		ArrayList<EnergyTransferModeType> evseSupported = getCommSessionContext().getSupportedEnergyTransferModes();
		EnergyTransferModeType evRequested = chargeParameterDiscoveryReq.getRequestedEnergyTransferMode();
		
		if (!evseSupported.contains(evRequested)) {
			chargeParameterDiscoveryRes.setResponseCode(ResponseCodeType.FAILED_WRONG_ENERGY_TRANSFER_MODE);
			return false;
		}
		
		// Check as well if evRequested does not fit to the content of attribute EVChargeParameter
		if ( (chargeParameterDiscoveryReq.getEVChargeParameter().getValue() instanceof ACEVChargeParameterType &&
			  evRequested.toString().startsWith("DC")) || 
			 (chargeParameterDiscoveryReq.getEVChargeParameter().getValue() instanceof DCEVChargeParameterType &&
			  evRequested.toString().startsWith("AC")) ) {
			getLogger().error(chargeParameterDiscoveryReq.getEVChargeParameter().getValue().getClass().getSimpleName() +
							  " does not fit to EnergyTransferMode '" + evRequested.toString() + "'");
			chargeParameterDiscoveryRes.setResponseCode(ResponseCodeType.FAILED_WRONG_ENERGY_TRANSFER_MODE);
			return false;
		}
		
		if (!verifyChargeParameter(chargeParameterDiscoveryReq)) {
			chargeParameterDiscoveryRes.setResponseCode(ResponseCodeType.FAILED_WRONG_CHARGE_PARAMETER);
			return false;
		}
		
		return true;
	}
	
	
	private boolean verifyChargeParameter(ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq) {
		if (chargeParameterDiscoveryReq.getEVChargeParameter() == null) {
			getLogger().error("EVChargeParameter is empty (null)");
			return false;
		}
		
		if (chargeParameterDiscoveryReq.getEVChargeParameter().getValue() instanceof ACEVChargeParameterType) {
			ACEVChargeParameterType acEVChargeParameter = (ACEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue();
			
			if ( // Check if mandatory charge parameters are null
				(acEVChargeParameter.getEAmount() == null || 
				 acEVChargeParameter.getEVMaxVoltage() == null ||
				 acEVChargeParameter.getEVMaxCurrent() == null ||
				 acEVChargeParameter.getEVMinCurrent() == null
				) ||
				// Check if charge parameters are out of range
				( acEVChargeParameter.getEAmount().getValue() < 0 ||
				  acEVChargeParameter.getEAmount().getValue() * Math.pow(10, acEVChargeParameter.getEAmount().getMultiplier()) > 200000 ||
				  acEVChargeParameter.getEVMaxVoltage().getValue() < 0 ||
				  acEVChargeParameter.getEVMaxVoltage().getValue() * Math.pow(10, acEVChargeParameter.getEVMaxVoltage().getMultiplier()) > 1000 ||
				  acEVChargeParameter.getEVMaxCurrent().getValue() < 0 ||
				  acEVChargeParameter.getEVMaxCurrent().getValue() * Math.pow(10, acEVChargeParameter.getEVMaxCurrent().getMultiplier()) > 400 ||
				  acEVChargeParameter.getEVMinCurrent().getValue() < 0 ||
				  acEVChargeParameter.getEVMinCurrent().getValue() * Math.pow(10, acEVChargeParameter.getEVMinCurrent().getMultiplier()) > 400
				)
			) {
				getLogger().error("One of the AC_EVChargeParameter elements is either null or out of range");
				return false;
			}
		}
		
		if (chargeParameterDiscoveryReq.getEVChargeParameter().getValue() instanceof DCEVChargeParameterType) {
			DCEVChargeParameterType dcEVChargeParameter = (DCEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue();
		
			if ( // Check if mandatory charge parameters are null
				(dcEVChargeParameter.getDCEVStatus() == null || 
				  dcEVChargeParameter.getEVMaximumCurrentLimit() == null ||
				  dcEVChargeParameter.getEVMaximumVoltageLimit() == null
				) ||
				// Check if charge parameters are out of range
				( dcEVChargeParameter.getDCEVStatus().getEVRESSSOC() < 0 ||
				  dcEVChargeParameter.getDCEVStatus().getEVRESSSOC() > 100 ||
				  dcEVChargeParameter.getEVMaximumCurrentLimit().getValue() < 0 ||
				  dcEVChargeParameter.getEVMaximumCurrentLimit().getValue() * Math.pow(10, dcEVChargeParameter.getEVMaximumCurrentLimit().getMultiplier()) > 400 ||
				  dcEVChargeParameter.getEVMaximumVoltageLimit().getValue() < 0 ||
				  dcEVChargeParameter.getEVMaximumVoltageLimit().getValue() * Math.pow(10, dcEVChargeParameter.getEVMaximumVoltageLimit().getMultiplier()) > 1000 ||
				  ( // EVMaximumPowerLimit is optional
				    dcEVChargeParameter.getEVMaximumPowerLimit() != null && (
				      dcEVChargeParameter.getEVMaximumPowerLimit().getValue() < 0 ||
				      dcEVChargeParameter.getEVMaximumPowerLimit().getValue() * Math.pow(10, dcEVChargeParameter.getEVMaximumPowerLimit().getMultiplier()) > 200000
				    )
				  ) ||
				  ( // EVEnergyCapacity is optional
				    dcEVChargeParameter.getEVEnergyCapacity() != null && (
				      dcEVChargeParameter.getEVEnergyCapacity().getValue() < 0 ||
				      dcEVChargeParameter.getEVEnergyCapacity().getValue() * Math.pow(10, dcEVChargeParameter.getEVEnergyCapacity().getMultiplier()) > 200000
				    )
				  ) ||
				  ( // EVEnergyRequest is optional
				    dcEVChargeParameter.getEVEnergyRequest() != null && (
				      dcEVChargeParameter.getEVEnergyRequest().getValue() < 0 ||
				      dcEVChargeParameter.getEVEnergyRequest().getValue() * Math.pow(10, dcEVChargeParameter.getEVEnergyRequest().getMultiplier()) > 200000
				    )
				  ) ||
				  ( // FullSOC is optional
				    dcEVChargeParameter.getFullSOC() != null && (
				      dcEVChargeParameter.getFullSOC() < 0 ||
				      dcEVChargeParameter.getFullSOC() > 100
				    )
				  ) ||
				  ( // BulkSOC is optional
				    dcEVChargeParameter.getBulkSOC() != null && (
				      dcEVChargeParameter.getBulkSOC() < 0 ||
				      dcEVChargeParameter.getBulkSOC() > 100
				    )
				  )
				)
			) {
				getLogger().error("One of the DC_EVChargeParameter elements is either null or out of range");
				return false;
			}
		}
		
		return true;
	}
	
	
	private JAXBElement<SAScheduleListType> getSASchedulesAsJAXBElement(SAScheduleListType saScheduleList) {
		return new JAXBElement<SAScheduleListType>(
				new QName("urn:iso:15118:2:2013:MsgDataTypes", "SAScheduleList"),
				SAScheduleListType.class, 
				saScheduleList);
	}

	public boolean isWaitingForSchedule() {
		return waitingForSchedule;
	}

	private void setWaitingForSchedule(boolean waitingForSchedule) {
		this.waitingForSchedule = waitingForSchedule;
	}

	@Override
	public BodyBaseType getResponseMessage() {
		return chargeParameterDiscoveryRes;
	}

}
