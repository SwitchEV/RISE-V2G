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
