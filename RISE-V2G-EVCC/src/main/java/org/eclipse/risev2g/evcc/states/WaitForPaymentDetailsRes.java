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

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForPaymentDetailsRes extends ClientState {
	
	public WaitForPaymentDetailsRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PaymentDetailsResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			PaymentDetailsResType paymentDetailsRes = 
					(PaymentDetailsResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			/*
			 * A reaction on the response code OK_CERTIFICATE_EXPIRES_SOON is not needed as this check 
			 * is already done by EVCC itself before deciding to send CertificateUpdateReq/CertificateInstallationReq
			 */
			
			if (paymentDetailsRes.getGenChallenge() == null) {
				return new TerminateSession("GenChallenge not provided in PaymentDetailsRes");
			} else {
				// Set xml reference element
				AuthorizationReqType authorizationReq = getAuthorizationReq(paymentDetailsRes.getGenChallenge());
				
				getXMLSignatureRefElements().put(
						authorizationReq.getId(), 
						SecurityUtils.generateDigest(authorizationReq, false));
				
				// Set signing private key
				setSignaturePrivateKey(SecurityUtils.getPrivateKey(
						SecurityUtils.getKeyStore(
								GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
								GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()), 
						GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString())
				);
				
				return getSendMessage(authorizationReq, V2GMessages.AUTHORIZATION_RES);
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}

}
