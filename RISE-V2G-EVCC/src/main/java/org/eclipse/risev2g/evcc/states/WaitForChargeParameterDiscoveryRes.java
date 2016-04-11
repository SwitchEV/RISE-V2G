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
package org.eclipse.risev2g.evcc.states;

import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;
import java.util.List;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.CPStates;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			
			if (chargeParameterDiscoveryRes.getEVSEProcessing().equals(EVSEProcessingType.ONGOING)) {
				getLogger().debug("EVSEProcessing was set to ONGOING");
				return getSendMessage(getCommSessionContext().getChargeParameterDiscoveryReq(), V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES);
			} else {
				// Check for the EVSENotification
				EVSENotificationType evseNotification = null;
				if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC"))
					evseNotification = ((ACEVSEChargeParameterType) chargeParameterDiscoveryRes
											.getEVSEChargeParameter().getValue())
											.getACEVSEStatus().getEVSENotification();
				else
					evseNotification = ((DCEVSEChargeParameterType) chargeParameterDiscoveryRes
											.getEVSEChargeParameter().getValue())
											.getDCEVSEStatus().getEVSENotification();
				
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
					
					SAScheduleListType saSchedules = (SAScheduleListType) chargeParameterDiscoveryRes.getSASchedules().getValue();
					
					if (saSchedules != null && getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.CONTRACT))
						verifySalesTariffs(saSchedules, v2gMessageRes.getHeader().getSignature());
					
					// Save the list of SASchedules (saves the time of reception as well)
					getCommSessionContext().setSaSchedules(saSchedules);
					
					if (getCommSessionContext().getEvController().getCPState().equals(CPStates.STATE_B)) {
						if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
							return getSendMessage(getPowerDeliveryReq(ChargeProgressType.START), V2GMessages.POWER_DELIVERY_RES);
						} else if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
							// CP state C signaling BEFORE sending CableCheckReq message in DC
							if (getCommSessionContext().getEvController().setCPState(CPStates.STATE_C))
								return getSendMessage(getCableCheckReq(), V2GMessages.CABLE_CHECK_RES);
							else
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
		 * 1. In case of PnC, and if a Tariff Table is used by the secondary actor, the secondary actor SHALL
		 * sign the field SalesTariff of type SalesTariffType. In case of EIM, the secondary actor MAY sign
		 * this field.
		 * 
		 * 2. If the EVCC treats the SalesTariff as invalid, it shall ignore the SalesTariff table, i.e. the
		 * behaviour of the EVCC shall be the same as if no tariff tables were received. Furthermore, the
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
					SecurityUtils.generateDigest(saScheduleTuple.getSalesTariff(), false));
		}
		
		if (salesTariffCounter > 0) {
			X509Certificate moSub2Certificate = SecurityUtils.getMOSub2Certificate(
													GlobalValues.EVCC_KEYSTORE_FILEPATH.toString());
			if (moSub2Certificate == null) {
				getLogger().error("No MOSub2Certificate found, signature of sales tariff could therefore not be verified");
				return false;
			} else {
				ECPublicKey ecPublicKey = (ECPublicKey) moSub2Certificate.getPublicKey();
				if (!SecurityUtils.verifySignature(signature, verifyXMLSigRefElements, ecPublicKey)) {
					return false;
				}
			}
		}
		
		if (ignoredSalesTariffs > 0) {
			getLogger().info("Sales tariffs could not be verified because of missing signature and will therefore be ignored");
			return false;
		}
		
		return true;
	}
}
