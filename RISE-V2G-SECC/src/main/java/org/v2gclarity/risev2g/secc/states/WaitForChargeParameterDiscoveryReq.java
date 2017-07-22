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

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.v2gclarity.risev2g.secc.evseController.IACEVSEController;
import org.v2gclarity.risev2g.secc.evseController.IDCEVSEController;
import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import org.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVChargeParameterType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVChargeParameterType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
					
					getCommSessionContext().setSaSchedules(
							getCommSessionContext().getBackendInterface().getSAScheduleList(
									maxEntriesSAScheduleTuple, 
									getXMLSignatureRefElements())
							);
				}
				
				// Wait a bit and check if the schedule has already been provided
				// TODO is this the best way?
				try {
					Thread.sleep(TimeRestrictions.getV2G_EVCC_Msg_Timeout(V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES)-1000);
				} catch (InterruptedException e) {
					return new TerminateSession("InterruptedException while waiting for schedule");
				}
				
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
				getLogger().error("Response code '" + chargeParameterDiscoveryRes.getResponseCode() + "' will be sent");
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.NONE);
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
		
		if (chargeParameterDiscoveryReq.getEVChargeParameter() == null ||
				(chargeParameterDiscoveryReq.getEVChargeParameter().getValue() instanceof ACEVChargeParameterType && 
					(((ACEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getEAmount() == null ||
					 ((ACEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getEVMaxVoltage() == null ||
					 ((ACEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getEVMaxCurrent() == null ||
					 ((ACEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getEVMinCurrent() == null
					)
				) ||
				(chargeParameterDiscoveryReq.getEVChargeParameter().getValue() instanceof DCEVChargeParameterType && 
					(((DCEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getDCEVStatus() == null || 
					 ((DCEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getEVMaximumCurrentLimit() == null ||
					 ((DCEVChargeParameterType) chargeParameterDiscoveryReq.getEVChargeParameter().getValue()).getEVMaximumVoltageLimit() == null
					) 
				)
			) {
			chargeParameterDiscoveryRes.setResponseCode(ResponseCodeType.FAILED_WRONG_CHARGE_PARAMETER);
			return false;
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
	protected void setMandatoryFieldsForFailedRes() {
		chargeParameterDiscoveryRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
		chargeParameterDiscoveryRes.setEVSEChargeParameter(
					((IACEVSEController) getCommSessionContext().getACEvseController()).getACEVSEChargeParameter());
	}

}
