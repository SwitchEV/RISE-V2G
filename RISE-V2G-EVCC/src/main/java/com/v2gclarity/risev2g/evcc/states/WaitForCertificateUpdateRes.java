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
package com.v2gclarity.risev2g.evcc.states;

import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.PKI;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateUpdateResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCertificateUpdateRes extends ClientState {

	public WaitForCertificateUpdateRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CertificateUpdateResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			CertificateUpdateResType certificateUpdateRes = 
					(CertificateUpdateResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			if (!verifySignature(certificateUpdateRes, v2gMessageRes.getHeader().getSignature())) {
				return new TerminateSession("Signature verification failed");
			}
			
			// Check complete CPS certificate chain
			ResponseCodeType certChainResponseCode = SecurityUtils.verifyCertificateChain(
														certificateUpdateRes.getSAProvisioningCertificateChain(),
														GlobalValues.EVCC_TRUSTSTORE_FILEPATH.toString(),
														PKI.CPS);
			if (!certChainResponseCode.equals(ResponseCodeType.OK)) {
				return new TerminateSession("Provisioning certificate chain is not valid");
			}
			
			ECPrivateKey contractCertPrivateKey = SecurityUtils.getPrivateKey(
					SecurityUtils.getKeyStore(
							GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
							GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()),
					GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString());
			
			// Save contract certificate chain
			if (!SecurityUtils.saveContractCertificateChain(
					GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString(),
					certificateUpdateRes.getContractSignatureCertChain(),
					SecurityUtils.decryptContractCertPrivateKey(
							certificateUpdateRes.getDHpublickey().getValue(), 
							certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getValue(), 
							contractCertPrivateKey))) {
				return new TerminateSession("Contract certificate chain could not be saved");
			}
			
			return getSendMessage(getPaymentDetailsReq(), V2GMessages.PAYMENT_DETAILS_RES);
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
	
	
	private boolean verifySignature(CertificateUpdateResType certificateUpdateRes, SignatureType signature) {
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(
				certificateUpdateRes.getContractSignatureCertChain().getId(),
				SecurityUtils.generateDigest(
						certificateUpdateRes.getContractSignatureCertChain().getId(),
						getMessageHandler().getJaxbElement(certificateUpdateRes.getContractSignatureCertChain())));
		verifyXMLSigRefElements.put(
				certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getId(),
				SecurityUtils.generateDigest(
						certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getId(),
						getMessageHandler().getJaxbElement(certificateUpdateRes.getContractSignatureEncryptedPrivateKey())));
		verifyXMLSigRefElements.put(
				certificateUpdateRes.getDHpublickey().getId(),
				SecurityUtils.generateDigest(
						certificateUpdateRes.getDHpublickey().getId(),
						getMessageHandler().getJaxbElement(certificateUpdateRes.getDHpublickey())));
		verifyXMLSigRefElements.put(
				certificateUpdateRes.getEMAID().getId(),
				SecurityUtils.generateDigest(
						certificateUpdateRes.getEMAID().getId(),
						getMessageHandler().getJaxbElement(certificateUpdateRes.getEMAID())));
				
		if (!SecurityUtils.verifySignature(
				signature, 
				getMessageHandler().getJaxbElement(signature.getSignedInfo()),
				verifyXMLSigRefElements, 
				certificateUpdateRes.getSAProvisioningCertificateChain().getCertificate())) {
			return false;
		}
		
		return true;
	}
}
