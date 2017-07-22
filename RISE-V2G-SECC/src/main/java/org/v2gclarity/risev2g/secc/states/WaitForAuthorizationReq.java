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

import java.util.Arrays;
import java.util.HashMap;

import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.utils.SecurityUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
				getLogger().error("Response code '" + authorizationRes.getResponseCode() + "' will be sent");
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(authorizationRes, V2GMessages.NONE);
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
			verifyXMLSigRefElements.put(authorizationReq.getId(), SecurityUtils.generateDigest(authorizationReq));
			
			if (!SecurityUtils.verifySignature(
					signature, 
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
	protected void setMandatoryFieldsForFailedRes() {
		authorizationRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
	}

}
