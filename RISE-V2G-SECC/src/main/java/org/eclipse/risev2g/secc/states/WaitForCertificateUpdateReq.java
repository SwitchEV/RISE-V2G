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

import java.security.KeyPair;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateUpdateReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateUpdateResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCertificateUpdateReq extends ServerState  {

	private CertificateUpdateResType certificateUpdateRes;
	
	public WaitForCertificateUpdateReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		certificateUpdateRes = new CertificateUpdateResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CertificateUpdateReqType.class, certificateUpdateRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			CertificateUpdateReqType certificateUpdateReq = 
					(CertificateUpdateReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			CertificateChainType contractCertificateChain = getCommSessionContext().getBackendInterface().getContractCertificateChain();
			
			if (isResponseCodeOK(
					certificateUpdateReq, 
					contractCertificateChain, 
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
								(ECPublicKey) SecurityUtils.getCertificate(certificateUpdateReq.getContractSignatureCertChain().getCertificate()).getPublicKey(),
								ecdhKeyPair,
								getCommSessionContext().getBackendInterface().getContractCertificatePrivateKey());
				
				certificateUpdateRes.setContractSignatureCertChain(contractCertificateChain);
				certificateUpdateRes.getContractSignatureCertChain().setId("contractSignatureCertChain");
				certificateUpdateRes.setContractSignatureEncryptedPrivateKey(encryptedContractCertPrivateKey);
				certificateUpdateRes.getContractSignatureEncryptedPrivateKey().setId("contractSignatureEncryptedPrivateKey");
				certificateUpdateRes.setDHpublickey(SecurityUtils.getDHPublicKey(ecdhKeyPair));
				certificateUpdateRes.getDHpublickey().setId("dhPublicKey");
				certificateUpdateRes.setEMAID(SecurityUtils.getEMAID(contractCertificateChain));
				certificateUpdateRes.getEMAID().setId("emaid");
				certificateUpdateRes.setSAProvisioningCertificateChain(getCommSessionContext().getBackendInterface().getSAProvisioningCertificateChain());
				
				// In case of negative response code, try at next charging (retryCounter = 0)
				if (!certificateUpdateRes.getResponseCode().toString().startsWith("OK"))
					certificateUpdateRes.setRetryCounter((short) 0);
				
				// Set xml reference elements
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getContractSignatureCertChain().getId(), 
						SecurityUtils.generateDigest(certificateUpdateRes.getContractSignatureCertChain(), false));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getId(),
						SecurityUtils.generateDigest(certificateUpdateRes.getContractSignatureEncryptedPrivateKey(), false));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getDHpublickey().getId(), 
						SecurityUtils.generateDigest(certificateUpdateRes.getDHpublickey(), false));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getEMAID().getId(), 
						SecurityUtils.generateDigest(certificateUpdateRes.getEMAID(), false));
				
				// Set signing private key
				setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getSAProvisioningCertificatePrivateKey());
			} else {
				getLogger().error("Response code '" + certificateUpdateRes.getResponseCode() + "' will be sent");
			}
		}
		
		return getSendMessage(certificateUpdateRes, 
	  			  			 (certificateUpdateRes.getResponseCode().toString().startsWith("OK") ? 
	  			  			 V2GMessages.PAYMENT_DETAILS_REQ : V2GMessages.NONE)
	 			 			 );
	}
	
	
	private boolean isResponseCodeOK(
			CertificateUpdateReqType certificateUpdateReq, 
			CertificateChainType saContractCertificateChain, 
			SignatureType signature) {
		// Check for FAILED_NoCertificateAvailable
		if (saContractCertificateChain == null || saContractCertificateChain.getCertificate() == null) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_NO_CERTIFICATE_AVAILABLE);
			return false;
		}
		
		/*
		 * Check for FAILED_CertificateExpired
		 * There is no negative response code for a certificate which is neither yet valid nor expired.
		 * It is thus implicitly expected that a secondary actor would only send already valid certificates.
		 */
		if (!SecurityUtils.isCertificateChainValid(certificateUpdateReq.getContractSignatureCertChain())) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_CERTIFICATE_EXPIRED);
			return false;
		}
		
		// Check for FAILED_CertChainError
		if (!SecurityUtils.isCertificateChainVerified(
				GlobalValues.SECC_TRUSTSTORE_FILEPATH.toString(), 
				certificateUpdateReq.getContractSignatureCertChain())) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_CERT_CHAIN_ERROR);
			return false;
		}
		
		// Check for FAILED_ContractCancelled
		// TODO how to check if the EMAID provided by EVCC is not accepted by secondary actor?
		
		// Check for FAILED_CertificateRevoked
		// TODO check for revocation with OCSP
		
		// Verify signature
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(certificateUpdateReq.getId(), SecurityUtils.generateDigest(certificateUpdateReq, false));
		ECPublicKey ecPublicKey = (ECPublicKey) SecurityUtils.getCertificate(
				certificateUpdateReq.getContractSignatureCertChain().getCertificate())
				.getPublicKey();
		if (!SecurityUtils.verifySignature(signature, verifyXMLSigRefElements, ecPublicKey)) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_SIGNATURE_ERROR);
			return false;
		}
		
		return true;
	}
}
