/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017  V2G Clarity (Dr.-Ing. Marc Mültin)
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
package com.v2gclarity.risev2g.secc.states;

import java.util.Arrays;
import java.util.HashMap;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForAuthorizationReq extends ServerState {

	private AuthorizationResType authorizationRes;
	private boolean authorizationFinished;
	
	public WaitForAuthorizationReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		authorizationRes = new AuthorizationResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, AuthorizationReqType.class, authorizationRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			AuthorizationReqType authorizationReq = 
					(AuthorizationReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			if (isResponseCodeOK(authorizationReq, v2gMessageReq.getHeader().getSignature())) {
				/*
				 * TODO start a Thread which authenticates the EVCC and sets the class-variable
				 * authenticationFinished (and remove setAuthorizationFinished(true) here!)
				 */
				setAuthorizationFinished(true);
				
				if (isAuthorizationFinished()) {
					authorizationRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
					return getSendMessage(authorizationRes, V2GMessages.CHARGE_PARAMETER_DISCOVERY_REQ);
				} else {
					authorizationRes.setEVSEProcessing(EVSEProcessingType.ONGOING);
					return getSendMessage(authorizationRes, V2GMessages.AUTHORIZATION_REQ);
				}
			} else {
				setMandatoryFieldsForFailedRes(authorizationRes, authorizationRes.getResponseCode());
			}
		} else {
			if (authorizationRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new AuthorizationReqType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, authorizationRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(authorizationRes, authorizationRes.getResponseCode());
			}
		}
		
		return getSendMessage(authorizationRes, V2GMessages.NONE, authorizationRes.getResponseCode());
	}
	
	
	public boolean isResponseCodeOK(AuthorizationReqType authorizationReq, SignatureType signature) {
		if (getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.EXTERNAL_PAYMENT)) {
			if (authorizationReq.getGenChallenge() != null) 
				getLogger().warn("EVCC sent a challenge parameter but " + PaymentOptionType.EXTERNAL_PAYMENT + 
					 " has been chosen. The challenge parameter should not be present and will be ignored.");
	
			return true;
		}
		
		if (!Arrays.equals(authorizationReq.getGenChallenge(), getCommSessionContext().getGenChallenge())) {
			authorizationRes.setResponseCode(ResponseCodeType.FAILED_CHALLENGE_INVALID);
			return false;
		}
		
		/*
		 * Only try to verify the signature in case we use a TLS connection and 'Contract' has been chosen as payment 
		 * method. If EIM has been chosen, then no contract certificate chain and not challenge will be sent by the EV, 
		 * but TLS is possible with both EIM and PnC.
		 */
		if (getCommSessionContext().isTlsConnection() && 
			getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.CONTRACT)) {
			// Verify signature
			HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
			verifyXMLSigRefElements.put(
					authorizationReq.getId(), 
					SecurityUtils.generateDigest(
							authorizationReq.getId(),
							getMessageHandler().getJaxbElement(authorizationReq)));
			
			if (!SecurityUtils.verifySignature(
					signature, 
					getMessageHandler().getJaxbElement(signature.getSignedInfo()),
					verifyXMLSigRefElements, 
					getCommSessionContext().getContractSignatureCertChain().getCertificate())) {
				authorizationRes.setResponseCode(ResponseCodeType.FAILED_SIGNATURE_ERROR);
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isAuthorizationFinished() {
		return authorizationFinished;
	}

	public void setAuthorizationFinished(boolean authorizationFinished) {
		this.authorizationFinished = authorizationFinished;
	}
	

	@Override
	public BodyBaseType getResponseMessage() {
		return authorizationRes;
	}

}
