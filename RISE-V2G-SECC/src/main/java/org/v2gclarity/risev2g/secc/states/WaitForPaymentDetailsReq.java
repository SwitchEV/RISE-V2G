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

import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.enumerations.PKI;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.utils.SecurityUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentDetailsReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
				
				paymentDetailsRes.setEVSETimeStamp(System.currentTimeMillis() / 1000L);
				byte[] genChallenge = SecurityUtils.generateRandomNumber(16);
				getCommSessionContext().setGenChallenge(genChallenge);
				paymentDetailsRes.setGenChallenge(genChallenge);
			} else {
				setMandatoryFieldsForFailedRes(paymentDetailsRes, paymentDetailsRes.getResponseCode());
			}
		} else {
			if (paymentDetailsRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new PaymentDetailsResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, paymentDetailsRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(paymentDetailsRes, paymentDetailsRes.getResponseCode());
			}
		}
		
		return getSendMessage(paymentDetailsRes, 
				  			  (paymentDetailsRes.getResponseCode().toString().startsWith("OK") ? 
				  			  V2GMessages.AUTHORIZATION_REQ : V2GMessages.NONE),
				  			  paymentDetailsRes.getResponseCode()
				 			 );
	}
	
	
	public boolean isResponseCodeOK(PaymentDetailsReqType paymentDetailsReq) {
		// TODO is Check for FAILED_NoCertificateAvailable and FAILED_CertificateRevoked necessary here?
		
		if (paymentDetailsReq.getContractSignatureCertChain() == null) {
			getLogger().error("Certificate chain is NULL");
			paymentDetailsRes.setResponseCode(ResponseCodeType.FAILED_CERT_CHAIN_ERROR);
			return false;
		}
		
		// Check complete contract certificate chain
		ResponseCodeType certChainResponseCode = SecurityUtils.verifyCertificateChain(
													paymentDetailsReq.getContractSignatureCertChain(),
													GlobalValues.SECC_TRUSTSTORE_FILEPATH.toString(),
													PKI.MO);
		if (!certChainResponseCode.equals(ResponseCodeType.OK)) {
			paymentDetailsRes.setResponseCode(certChainResponseCode);
			return false;
		}
		
		// Check if certificate expires soon (in 21 days or fewer) according to V2G2-690 
		// A check for general validity has already been done above and does not need to be checked again here
		if (SecurityUtils.getValidityPeriod(
					SecurityUtils.getCertificate(paymentDetailsReq.getContractSignatureCertChain().getCertificate())
				) <= GlobalValues.CERTIFICATE_EXPIRES_SOON_PERIOD.getShortValue()) {
			paymentDetailsRes.setResponseCode(ResponseCodeType.OK_CERTIFICATE_EXPIRES_SOON);
		}
		
		// Check for FAILED_ContractCancelled
		// TODO how to check if the EMAID provided by EVCC is not accepted by secondary actor?
		if (!SecurityUtils.isEMAIDSynstaxValid(
				SecurityUtils.getCertificate(
						paymentDetailsReq.getContractSignatureCertChain().getCertificate())
						)
				) {
			// There is no good FAILED response code for this situation, but ContractCanceled is still better than FAILED
			paymentDetailsRes.setResponseCode(ResponseCodeType.FAILED_CONTRACT_CANCELED);
			return false;
		}
		
		return true;
	}
	
	@Override
	public BodyBaseType getResponseMessage() {
		return paymentDetailsRes;
	}
}
