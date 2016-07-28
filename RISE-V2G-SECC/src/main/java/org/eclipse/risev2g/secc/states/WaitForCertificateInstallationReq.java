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
package org.eclipse.risev2g.secc.states;

import java.security.KeyPair;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateInstallationReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCertificateInstallationReq extends ServerState  {

	private CertificateInstallationResType certificateInstallationRes;
	
	public WaitForCertificateInstallationReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		certificateInstallationRes = new CertificateInstallationResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CertificateInstallationReqType.class, certificateInstallationRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			CertificateInstallationReqType certificateInstallationReq = (CertificateInstallationReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			CertificateChainType saContractCertificateChain = getCommSessionContext().getBackendInterface().getContractCertificateChain();
			
			if (isResponseCodeOK(
					certificateInstallationReq,
					saContractCertificateChain,
					v2gMessageReq.getHeader().getSignature())) {
				// The ECDH (elliptic curve Diffie Hellman) key pair is also needed for the generation of the shared secret
				KeyPair ecdhKeyPair = SecurityUtils.getECDHKeyPair();
				if (ecdhKeyPair == null) {
					getLogger().error("ECDH keypair could not be generated");
					return null;
				}
				
				// Encrypt private key
				ContractSignatureEncryptedPrivateKeyType encryptedContractCertPrivateKey = 
						SecurityUtils.encryptContractCertPrivateKey(
								(ECPublicKey) SecurityUtils.getCertificate(certificateInstallationReq.getOEMProvisioningCert()).getPublicKey(),
								ecdhKeyPair,
								getCommSessionContext().getBackendInterface().getContractCertificatePrivateKey());
				
				certificateInstallationRes.setContractSignatureCertChain(saContractCertificateChain);
				certificateInstallationRes.getContractSignatureCertChain().setId("contractSignatureCertChain");
				certificateInstallationRes.setContractSignatureEncryptedPrivateKey(encryptedContractCertPrivateKey);
				certificateInstallationRes.getContractSignatureEncryptedPrivateKey().setId("contractSignatureEncryptedPrivateKey");
				certificateInstallationRes.setDHpublickey(SecurityUtils.getDHPublicKey(ecdhKeyPair));
				certificateInstallationRes.getDHpublickey().setId("dhPublicKey");
				certificateInstallationRes.setEMAID(SecurityUtils.getEMAID(saContractCertificateChain));
				certificateInstallationRes.getEMAID().setId("emaid");
				certificateInstallationRes.setSAProvisioningCertificateChain(
						getCommSessionContext().getBackendInterface().getSAProvisioningCertificateChain());
				
				// Set xml reference elements
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getContractSignatureCertChain().getId(), 
						SecurityUtils.generateDigest(certificateInstallationRes.getContractSignatureCertChain(), false));
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getContractSignatureEncryptedPrivateKey().getId(),
						SecurityUtils.generateDigest(certificateInstallationRes.getContractSignatureEncryptedPrivateKey(), false));
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getDHpublickey().getId(), 
						SecurityUtils.generateDigest(certificateInstallationRes.getDHpublickey(), false));
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getEMAID().getId(), 
						SecurityUtils.generateDigest(certificateInstallationRes.getEMAID(), false));
				
				// Set signing private key
				setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getSAProvisioningCertificatePrivateKey());
			
			} else {
				getLogger().error("Response code '" + certificateInstallationRes.getResponseCode() + "' will be sent");
			}
		}
		
		return getSendMessage(certificateInstallationRes, V2GMessages.PAYMENT_DETAILS_REQ);
	}
	
	private boolean isResponseCodeOK(
			CertificateInstallationReqType certificateInstallationReq,
			CertificateChainType saContractCertificateChain, 
			SignatureType signature) {

		// Check for FAILED_NoCertificateAvailable
		if (saContractCertificateChain == null || saContractCertificateChain.getCertificate() == null) {
			certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED_NO_CERTIFICATE_AVAILABLE);
			return false;
		}
		
		/*
		 * Check for FAILED_CertificateExpired
		 * There is no negative response code for a certificate which is neither yet valid nor expired.
		 * It is thus implicitly expected that a secondary actor would only send already valid certificates.
		 */
		if (!SecurityUtils.isCertificateChainValid(saContractCertificateChain)) {
			certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED_CERTIFICATE_EXPIRED);
			return false;
		}
		
		// Check for FAILED_CertificateRevoked
		// TODO check for revocation with OCSP
		
		// Verify signature
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(certificateInstallationReq.getId(), SecurityUtils.generateDigest(certificateInstallationReq, false));
		ECPublicKey ecPublicKey = (ECPublicKey) SecurityUtils.getCertificate(
				certificateInstallationReq.getOEMProvisioningCert())
				.getPublicKey();
		if (!SecurityUtils.verifySignature(signature, verifyXMLSigRefElements, ecPublicKey)) {
			certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED_SIGNATURE_ERROR);
			return false;
		}
		
		return true;
	}
}
