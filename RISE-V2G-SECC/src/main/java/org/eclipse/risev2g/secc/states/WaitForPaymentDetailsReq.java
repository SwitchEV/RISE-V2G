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
package org.eclipse.risev2g.secc.states;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentDetailsReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForPaymentDetailsReq extends ServerState {
	
	private PaymentDetailsResType paymentDetailsRes; 
	
	public WaitForPaymentDetailsReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		paymentDetailsRes = new PaymentDetailsResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PaymentDetailsReqType.class, paymentDetailsRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			PaymentDetailsReqType paymentDetailsReq = 
					(PaymentDetailsReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			if (isResponseCodeOK(paymentDetailsReq)) {
				// Save contract certificate chain for certificate and signature verification/validation
				getCommSessionContext().setContractSignatureCertChain(paymentDetailsReq.getContractSignatureCertChain());
				
				paymentDetailsRes.setEVSETimeStamp(System.currentTimeMillis());
				byte[] genChallenge = SecurityUtils.generateRandomNumber(16);
				getCommSessionContext().setGenChallenge(genChallenge);
				paymentDetailsRes.setGenChallenge(genChallenge);
			} else {
				getLogger().error("Response code '" + paymentDetailsRes.getResponseCode() + "' will be sent");
			}
		} 
		
		return getSendMessage(paymentDetailsRes, 
				  			  (paymentDetailsRes.getResponseCode().toString().startsWith("OK") ? 
				  			  V2GMessages.AUTHORIZATION_REQ : V2GMessages.NONE)
				 			 );
	}
	
	
	public boolean isResponseCodeOK(PaymentDetailsReqType paymentDetailsReq) {
		// TODO is Check for FAILED_NoCertificateAvailable and FAILED_CertificateRevoked necessary here?
		
		if (paymentDetailsReq.getContractSignatureCertChain() == null) {
			getLogger().error("Certificate chain is NULL");
			paymentDetailsRes.setResponseCode(ResponseCodeType.FAILED_CERT_CHAIN_ERROR);
			return false;
		}
		
		if (!SecurityUtils.isCertificateChainValid(paymentDetailsReq.getContractSignatureCertChain())) {
			getLogger().error("Contract certificate chain is not valid");
			paymentDetailsRes.setResponseCode(ResponseCodeType.FAILED_CERTIFICATE_EXPIRED);
			return false;
		}
		
		if (!SecurityUtils.isCertificateChainVerified(
				GlobalValues.SECC_TRUSTSTORE_FILEPATH.toString(),
				paymentDetailsReq.getContractSignatureCertChain())) {
			getLogger().error("Contract certificate chain could not be verified");
			paymentDetailsRes.setResponseCode(ResponseCodeType.FAILED_CERT_CHAIN_ERROR);
			return false;
		}
		
		// Check if certificate expires soon (in 21 days or fewer) according to V2G2-690 
		// A check for general validity has already been done above and does not need to be checked again here
		if (SecurityUtils.getValidityPeriod(
					SecurityUtils.getCertificate(paymentDetailsReq.getContractSignatureCertChain().getCertificate())
				) <= GlobalValues.CERTIFICATE_EXPIRES_SOON_PERIOD.getShortValue()) {
			paymentDetailsRes.setResponseCode(ResponseCodeType.OK_CERTIFICATE_EXPIRES_SOON);
		}
		
		return true;
	}
}
