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

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
				SecurityUtils.generateDigest(certificateInstallationRes.getContractSignatureCertChain(), false));
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getContractSignatureEncryptedPrivateKey().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getContractSignatureEncryptedPrivateKey(), false));
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getDHpublickey().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getDHpublickey(), false));
		verifyXMLSigRefElements.put(
				certificateInstallationRes.getEMAID().getId(),
				SecurityUtils.generateDigest(certificateInstallationRes.getEMAID(), false));
				
		ECPublicKey ecPublicKey = (ECPublicKey) SecurityUtils.getCertificate(
				certificateInstallationRes.getSAProvisioningCertificateChain().getCertificate())
				.getPublicKey();
		if (!SecurityUtils.verifySignature(signature, verifyXMLSigRefElements, ecPublicKey)) {
			return false;
		}
		
		return true;
	}

}
