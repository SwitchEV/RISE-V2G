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

import java.security.interfaces.ECPublicKey;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			}
		} 
		
		return getSendMessage(authorizationRes, V2GMessages.NONE);
	}
	
	
	public boolean isResponseCodeOK(AuthorizationReqType authorizationReq, SignatureType signature) {
		if (!Arrays.equals(authorizationReq.getGenChallenge(), getCommSessionContext().getGenChallenge())) {
			authorizationRes.setResponseCode(ResponseCodeType.FAILED_CHALLENGE_INVALID);
			return false;
		}
		
		// Only try to verify the signature in case we use a TLS connection
		if (getCommSessionContext().isTlsConnection()) {
			// Verify signature
			HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
			verifyXMLSigRefElements.put(authorizationReq.getId(), SecurityUtils.generateDigest(authorizationReq, false));
			
			ECPublicKey ecPublicKey = (ECPublicKey) SecurityUtils.getCertificate(
					getCommSessionContext().getContractSignatureCertChain().getCertificate())
					.getPublicKey();
			
			if (!SecurityUtils.verifySignature(signature, verifyXMLSigRefElements, ecPublicKey)) {
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

}
