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
package com.v2gclarity.risev2g.evcc.states;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.CPStates;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVSEChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForChargeParameterDiscoveryRes extends ClientState {

	public WaitForChargeParameterDiscoveryRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ChargeParameterDiscoveryResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			ChargeParameterDiscoveryResType chargeParameterDiscoveryRes = 
					(ChargeParameterDiscoveryResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			if (chargeParameterDiscoveryRes.getEVSEProcessing() == null)
				return new TerminateSession("EVSEProcessing field of ChargeParameterDiscoveryRes is null. This field is mandatory.");
			
			if (chargeParameterDiscoveryRes.getEVSEProcessing().equals(EVSEProcessingType.ONGOING)) {
				getLogger().debug("EVSEProcessing was set to ONGOING");
				
				long elapsedTimeInMs = 0;
				
				if (getCommSessionContext().isOngoingTimerActive()) {
					long elapsedTime = System.nanoTime() - getCommSessionContext().getOngoingTimer();
					elapsedTimeInMs = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
					
					if (elapsedTimeInMs > TimeRestrictions.V2G_EVCC_ONGOING_TIMEOUT) 
						return new TerminateSession("Ongoing timer timed out for ChargeParameterDiscoveryReq");
				} else {
					getCommSessionContext().setOngoingTimer(System.nanoTime());
					getCommSessionContext().setOngoingTimerActive(true);
				}
				
				return getSendMessage(getCommSessionContext().getChargeParameterDiscoveryReq(), V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES, Math.min((TimeRestrictions.V2G_EVCC_ONGOING_TIMEOUT - (int) elapsedTimeInMs), TimeRestrictions.getV2gEvccMsgTimeout(V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES)));
			} else {	
				getLogger().debug("EVSEProcessing was set to FINISHED");
				
				getCommSessionContext().setOngoingTimer(0L);
				getCommSessionContext().setOngoingTimerActive(false);
				
				// Check for the EVSENotification
				EVSENotificationType evseNotification = null;
				
				try {
					if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC"))
					evseNotification = ((ACEVSEChargeParameterType) chargeParameterDiscoveryRes
											.getEVSEChargeParameter().getValue())
											.getACEVSEStatus().getEVSENotification();
					else
						evseNotification = ((DCEVSEChargeParameterType) chargeParameterDiscoveryRes
												.getEVSEChargeParameter().getValue())
												.getDCEVSEStatus().getEVSENotification();
				} catch (ClassCastException e) {
					return new TerminateSession("Sent EVSEChargeParameter do not match requested energy transfer mode " + 
												getCommSessionContext().getRequestedEnergyTransferMode().toString());
				}
				
				if (evseNotification.equals(EVSENotificationType.STOP_CHARGING)) {
					getLogger().debug("The EVSE requested to stop the charging process");
					getCommSessionContext().setStopChargingRequested(true);
					
					return getSendMessage(getPowerDeliveryReq(ChargeProgressType.STOP), V2GMessages.POWER_DELIVERY_RES);
				} else {
					/*
					 * The case RE_NEGOTIATION is to be ignored according to [V2G2-841] and [V2G2-680].
					 * An SECC triggered renegotiation is only to be reacted on in the messages
					 * - ChargingStatusRes
					 * - MeteringReceiptRes
					 * - CurrentDemandRes
					 */
					
					if (chargeParameterDiscoveryRes.getSASchedules() == null) 
						return new TerminateSession("SASchedules field of ChargeParameterDiscoveryRes is null although EVSEProcessing is set to FINISHED. SASchedules is mandatory in this case.");
					
					SAScheduleListType saSchedules = (SAScheduleListType) chargeParameterDiscoveryRes.getSASchedules().getValue();
					
					// If TLS is used, verify each sales tariff (if present) with the mobility operator sub 2 certificate
					if (getCommSessionContext().isTlsConnection() && saSchedules != null) {
						if (!verifySalesTariffs(saSchedules, v2gMessageRes.getHeader().getSignature())) {
							getLogger().warn("The SalesTariff will be ignored for the charge process due to "
										   + "failed signature verification during TLS communication.");
							deleteUnverifiedSalesTariffs(saSchedules);
						}
					}
					
					// Save the list of SASchedules (saves the time of reception as well)
					getCommSessionContext().setSaSchedules(saSchedules);
					
					if (getCommSessionContext().getEvController().getCPState().equals(CPStates.STATE_B)) {
						if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
							return getSendMessage(getPowerDeliveryReq(ChargeProgressType.START), V2GMessages.POWER_DELIVERY_RES);
						} else if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
							// CP state C signaling BEFORE sending CableCheckReq message in DC
							if (getCommSessionContext().getEvController().setCPState(CPStates.STATE_C)) {
								// Set timer for CableCheck
								getCommSessionContext().setOngoingTimer(System.nanoTime());
								getCommSessionContext().setOngoingTimerActive(true);
							
								return getSendMessage(getCableCheckReq(), V2GMessages.CABLE_CHECK_RES);
							} else
								return new TerminateSession("CP state C not ready (current state = " + 
										getCommSessionContext().getEvController().getCPState() +
										")");
						} else {
							return new TerminateSession("RequestedEnergyTransferMode '" + getCommSessionContext().getRequestedEnergyTransferMode().toString() + 
														"is neither of type AC nor DC");
						}
					} else {
						return new TerminateSession("CP state B not ready (current state = " + 
													getCommSessionContext().getEvController().getCPState() +
													")");
					}
				}
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
	
	/**
	 * Verifies each sales tariff given with the ChargeParameterDiscoveryRes message with the 
	 * mobility operator sub 2 certificate.
	 * 
	 * @param saSchedules The SASchedule list which holds all PMaxSchedules and SalesTariffs
	 * @param signature The signature for the sales tariffs
	 * @return True, if the verification of the sales tariffs was successful, false otherwise
	 */
	private boolean verifySalesTariffs(SAScheduleListType saSchedules, SignatureType signature) {
		 /* 
		 * Some important requirements: 
		 * 
		 * 1. In case of PnC, and if a SalesTariff is used by the secondary actor, the secondary actor SHALL
		 * sign the field SalesTariff of type SalesTariffType. In case of EIM, the secondary actor MAY sign
		 * this field.
		 * 
		 * 2. If the EVCC treats the SalesTariff as invalid, it shall ignore the SalesTariff, i.e. the
		 * behavior of the EVCC shall be the same as if no SalesTariff was received. Furthermore, the
		 * EVCC MAY close the connection. It then may reopen the connection again.
		 */
		
		boolean salesTariffSignatureAvailable = (signature == null) ? false : true;
		boolean ignoreSalesTariffs = (getCommSessionContext().isTlsConnection() && !salesTariffSignatureAvailable) ? true : false;
		short ignoredSalesTariffs = 0;
		
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		List<SAScheduleTupleType> saScheduleTuples = saSchedules.getSAScheduleTuple();
		int salesTariffCounter = 0;
		
		for (SAScheduleTupleType saScheduleTuple : saScheduleTuples) {
			// verification regards only sales tariffs, not PMaxSchedules
			if (saScheduleTuple.getSalesTariff() == null) continue;
			
			// Check if signature is given during TLS communication. If no signature is given, delete SalesTariff
			if (ignoreSalesTariffs) {
				ignoredSalesTariffs++;
				saScheduleTuple.setSalesTariff(null);
				continue;
			}
			
			salesTariffCounter++;
			
			verifyXMLSigRefElements.put(
					saScheduleTuple.getSalesTariff().getId(),
					SecurityUtils.generateDigest(
							saScheduleTuple.getSalesTariff().getId(),
							getMessageHandler().getJaxbElement(saScheduleTuple.getSalesTariff())));
		}
		
		if (salesTariffCounter > 0) {
			X509Certificate moSubCA2Certificate = SecurityUtils.getMOSubCA2Certificate(
													GlobalValues.EVCC_KEYSTORE_FILEPATH.toString());
			if (moSubCA2Certificate == null) {
				getLogger().error("No MOSubCA2 certificate found, signature of SalesTariff could therefore not be verified");
				return false;
			} else {
				if (!SecurityUtils.verifySignature(
						signature, 
						getMessageHandler().getJaxbElement(signature.getSignedInfo()),
						verifyXMLSigRefElements, 
						moSubCA2Certificate)) {
					getLogger().warn("Verification of SalesTariff failed using certificate with distinguished name '" + 
									 moSubCA2Certificate.getSubjectX500Principal().getName() + "'"); 
					return false;
				}
			}
		}
		
		if (ignoredSalesTariffs > 0) {
			getLogger().info("SalesTariffs could not be verified because of missing signature and will therefore be ignored");
			return false;
		}
		
		return true;
	}
	
	/**
	 * If the signature of one ore more sales tariffs cannot be verified, then the sales tariffs should be ignored
	 * rather than terminating the charge process. The charge process can then proceed based solely on the 
	 * PMaxSchedule
	 * 
	 * @param saSchedules The schedule(s) from the secondary actor including PMaxSchedule and potential SalesTariff
	 * 					  elements.
	 */
	private void deleteUnverifiedSalesTariffs(SAScheduleListType saSchedules) {
		List<SAScheduleTupleType> saScheduleTuples = saSchedules.getSAScheduleTuple();
		
		for (SAScheduleTupleType saScheduleTuple : saScheduleTuples) {
			saScheduleTuple.setSalesTariff(null);
		}
	}
}
