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
package org.eclipse.risev2g.evcc.states;

import org.eclipse.risev2g.evcc.evController.IACEVController;
import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingStatusResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeteringReceiptReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForChargingStatusRes extends ClientState {

	public WaitForChargingStatusRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ChargingStatusResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			ChargingStatusResType chargingStatusRes = 
					(ChargingStatusResType) v2gMessageRes.getBody().getBodyElement().getValue();
		
			/*
			 * ReceiptRequired has higher priority than a possible EVSENotification=Renegotiate
			 * 
			 * Check if communication is secured with TLS before reacting upon a possible request from the SECC to send
			 * a MeteringReceiptRequest. If no TLS is used, a MeteringReceiptRequest may not be sent because
			 * a signature cannot be applied without private key of the contract certificate.
			 */
			if (chargingStatusRes.isReceiptRequired() && getCommSessionContext().isTlsConnection()) {
				MeteringReceiptReqType meteringReceiptReq = new MeteringReceiptReqType();
				/*
				 * Experience from the test symposium in San Diego (April 2016):
				 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
				 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
				 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
				 */
				meteringReceiptReq.setId("id1");
				meteringReceiptReq.setMeterInfo(chargingStatusRes.getMeterInfo());
				meteringReceiptReq.setSAScheduleTupleID(chargingStatusRes.getSAScheduleTupleID());
				meteringReceiptReq.setSessionID(getCommSessionContext().getSessionID());
				
				// Set xml reference element
				getXMLSignatureRefElements().put(
						meteringReceiptReq.getId(), 
						SecurityUtils.generateDigest(meteringReceiptReq));
				
				// Set signing private key
				setSignaturePrivateKey(SecurityUtils.getPrivateKey(
						SecurityUtils.getKeyStore(
								GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
								GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()), 
						GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString())
				);
				
				return getSendMessage(meteringReceiptReq, V2GMessages.METERING_RECEIPT_RES);
			}
				
			// Check for EVSEMaxCurrent and tell the EV
			if (chargingStatusRes.getEVSEMaxCurrent() != null)
				((IACEVController) getCommSessionContext().getEvController())
					.adjustMaxCurrent(chargingStatusRes.getEVSEMaxCurrent());
			
			switch (chargingStatusRes.getACEVSEStatus().getEVSENotification()) {
				case STOP_CHARGING:
					getCommSessionContext().setStopChargingRequested(true);
					return getSendMessage(getPowerDeliveryReq(ChargeProgressType.STOP), 
										  V2GMessages.POWER_DELIVERY_RES,
										  " (ChargeProgress = STOP_CHARGING)");
				case RE_NEGOTIATION:
					return getSendMessage(getPowerDeliveryReq(ChargeProgressType.RENEGOTIATE), 
										  V2GMessages.POWER_DELIVERY_RES,
							  			  " (ChargeProgress = RE_NEGOTIATION)");
				default:
					// TODO regard [V2G2-305] (new SalesTariff if EAmount not yet met and tariff finished)
					
					// TODO check somehow if charging is stopped by EV, otherwise send new ChargingStatusReq
					getCommSessionContext().setStopChargingRequested(true);
					return getSendMessage(getPowerDeliveryReq(ChargeProgressType.STOP), 
										  V2GMessages.POWER_DELIVERY_RES,
										  " (ChargeProgress = STOP_CHARGING)");
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
