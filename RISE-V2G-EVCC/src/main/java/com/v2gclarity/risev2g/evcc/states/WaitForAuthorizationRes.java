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

import java.util.concurrent.TimeUnit;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			
			if (authorizationRes.getEVSEProcessing() == null)
				return new TerminateSession("EVSEProcessing parameter of AuthorizationRes is null. Parameter is mandatory.");
			
			if (authorizationRes.getEVSEProcessing().equals(EVSEProcessingType.FINISHED)) {
				getLogger().debug("EVSEProcessing was set to FINISHED");
				
				getCommSessionContext().setOngoingTimer(0L);
				getCommSessionContext().setOngoingTimerActive(false);
				
				ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq = getChargeParameterDiscoveryReq();
			
				/*
				 * Save this request in case the ChargeParameterDiscoveryRes indicates that the EVSE is 
				 * still processing. Then this request can just be resent instead of asking the EV again.
				 */
				getCommSessionContext().setChargeParameterDiscoveryReq(chargeParameterDiscoveryReq);
				
				return getSendMessage(chargeParameterDiscoveryReq, V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES);
			} else {
				getLogger().debug("EVSEProcessing was set to ONGOING");
				
				long elapsedTimeInMs = 0;
				
				if (getCommSessionContext().isOngoingTimerActive()) {
					long elapsedTime = System.nanoTime() - getCommSessionContext().getOngoingTimer();
					elapsedTimeInMs = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
					
					if (elapsedTimeInMs > TimeRestrictions.V2G_EVCC_ONGOING_TIMEOUT) 
						return new TerminateSession("Ongoing timer timed out for AuthorizationReq");
				} else {
					getCommSessionContext().setOngoingTimer(System.nanoTime());
					getCommSessionContext().setOngoingTimerActive(true);
				}
					
				AuthorizationReqType authorizationReq = null;
				
				if (getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.CONTRACT) && 
					getCommSessionContext().isTlsConnection()) {
					authorizationReq = getAuthorizationReq(getCommSessionContext().getSentGenChallenge());
					
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
				} else {
					authorizationReq = getAuthorizationReq(null);
				}
				
				return getSendMessage(authorizationReq, V2GMessages.AUTHORIZATION_RES, Math.min((TimeRestrictions.V2G_EVCC_ONGOING_TIMEOUT - (int) elapsedTimeInMs), TimeRestrictions.getV2gEvccMsgTimeout(V2GMessages.AUTHORIZATION_RES)));
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
