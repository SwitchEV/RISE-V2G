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
package org.v2gclarity.risev2g.evcc.states;

import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;

import org.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import org.v2gclarity.risev2g.shared.utils.SecurityUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCertificateInstallationRes extends ClientState {

	public WaitForCertificateInstallationRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CertificateInstallationResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			CertificateInstallationResType certificateInstallationRes = 
					(CertificateInstallationResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			if (!verifySignature(certificateInstallationRes, v2gMessageRes.getHeader().getSignature())) {
				return new TerminateSession("Signature verification failed");
			}
			
			/**
			 * Check
			 * - validity of each certificate in the chain
			 * - that the signer certificate has a DC (Domain Component) field with the content "CPS" set
			 */
			if (!SecurityUtils.isCertificateChainValid(certificateInstallationRes.getSAProvisioningCertificateChain(), "CPS")) {
				return new TerminateSession("Provisioning certificate chain is not valid");
			}
			
			ECPrivateKey oemProvCertPrivateKey = SecurityUtils.getPrivateKey(
					SecurityUtils.getKeyStore(
							GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
							GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()),
					GlobalValues.ALIAS_OEM_PROV_CERTIFICATE.toString());
			
			// Save contract certificate chain
			if (!SecurityUtils.saveContractCertificateChain(
					GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString(),
					certificateInstallationRes.getContractSignatureCertChain(),
					SecurityUtils.decryptContractCertPrivateKey(
							certificateInstallationRes.getDHpublickey().getValue(), 
							certificateInstallationRes.getContractSignatureEncryptedPrivateKey().getValue(), 
							oemProvCertPrivateKey))) {
				return new TerminateSession("Contract certificate chain could not be saved");
			} 
			
			return getSendMessage(getPaymentDetailsReq(), V2GMessages.PAYMENT_DETAILS_RES);
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
	
	
	private boolean verifySignature(CertificateInstallationResType certificateInstallationRes, SignatureType signature) {
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getContractSignatureCertChain().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getContractSignatureCertChain()));
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getContractSignatureEncryptedPrivateKey().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getContractSignatureEncryptedPrivateKey()));
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getDHpublickey().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getDHpublickey()));
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getEMAID().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getEMAID()));
				
		if (!SecurityUtils.verifySignature(
				signature, 
				verifyXMLSigRefElements, 
				certificateInstallationRes.getSAProvisioningCertificateChain().getCertificate())) {
			return false;
		}
		
		return true;
	}

}
