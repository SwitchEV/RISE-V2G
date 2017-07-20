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
package org.eclipse.risev2g.evcc.states;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForAuthorizationRes extends ClientState {

	public WaitForAuthorizationRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, AuthorizationResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			AuthorizationResType authorizationRes = 
					(AuthorizationResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			if (authorizationRes.getEVSEProcessing().equals(EVSEProcessingType.FINISHED)) {
				ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq = getChargeParameterDiscoveryReq();
			
				/*
				 * Save this request in case the ChargeParameterDiscoveryRes indicates that the EVSE is 
				 * still processing. Then this request can just be resent instead of asking the EV again.
				 */
				getCommSessionContext().setChargeParameterDiscoveryReq(chargeParameterDiscoveryReq);
				
				return getSendMessage(chargeParameterDiscoveryReq, V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES);
			} else {
				// Set xml reference element
				AuthorizationReqType authorizationReq = getAuthorizationReq(null);
				getXMLSignatureRefElements().put(
						authorizationReq.getId(), 
						SecurityUtils.generateDigest(authorizationReq));
				
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
