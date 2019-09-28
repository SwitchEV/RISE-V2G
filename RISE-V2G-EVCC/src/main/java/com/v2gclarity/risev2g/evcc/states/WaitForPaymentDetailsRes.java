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

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
				// Save the sent genChallenge for the state WaitForAuthorizationRes if EVSEProcessing is set to ONGOING
				getCommSessionContext().setSentGenChallenge(paymentDetailsRes.getGenChallenge());
				
				AuthorizationReqType authorizationReq = getAuthorizationReq(paymentDetailsRes.getGenChallenge());
				
				// Set xml reference element
				getXMLSignatureRefElements().put(
						authorizationReq.getId(), 
						SecurityUtils.generateDigest(
								authorizationReq.getId(),
								getMessageHandler().getJaxbElement(authorizationReq)));
				
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
