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

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.risev2g.secc.evseController.IACEVSEController;
import org.eclipse.risev2g.secc.evseController.IDCEVSEController;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.misc.TimeRestrictions;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
					
					// Set signing private key
					setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getSAProvisioningCertificatePrivateKey());
					
					if (chargeParameterDiscoveryReq.getRequestedEnergyTransferMode().toString().startsWith("AC")) 
						return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.POWER_DELIVERY_REQ);
					else 
						return getSendMessage(chargeParameterDiscoveryRes, V2GMessages.CABLE_CHECK_REQ);
				}
			} else {
				getLogger().error("Response code '" + chargeParameterDiscoveryRes.getResponseCode() + "' will be sent");
			}
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

}
