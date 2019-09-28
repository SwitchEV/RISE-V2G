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
package com.v2gclarity.risev2g.secc.states;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.PKI;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateInstallationReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			CertificateChainType saContractCertificateChain = 
					getCommSessionContext().getBackendInterface().getContractCertificateChain(
							SecurityUtils.getCertificate(certificateInstallationReq.getOEMProvisioningCert())
					);
			
			if (isResponseCodeOK(
					certificateInstallationReq,
					saContractCertificateChain,
					v2gMessageReq.getHeader().getSignature())) {
				// The EC key pair is also needed for the generation of the shared secret
				KeyPair ecKeyPair = SecurityUtils.getECKeyPair();
				if (ecKeyPair == null) {
					getLogger().error("EC keypair could not be generated");
					return null;
				}
				
				// Encrypt private key
				ContractSignatureEncryptedPrivateKeyType encryptedContractCertPrivateKey = 
						SecurityUtils.encryptContractCertPrivateKey(
								(ECPublicKey) SecurityUtils.getCertificate(certificateInstallationReq.getOEMProvisioningCert()).getPublicKey(),
								(ECPrivateKey) ecKeyPair.getPrivate(),
								getCommSessionContext().getBackendInterface().getContractCertificatePrivateKey());
				
				certificateInstallationRes.setContractSignatureCertChain(saContractCertificateChain);
				/*
				 * Experience from the test symposium in San Diego (April 2016):
				 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
				 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
				 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
				 */
				certificateInstallationRes.getContractSignatureCertChain().setId("id1"); // contractSignatureCertChain
				certificateInstallationRes.setContractSignatureEncryptedPrivateKey(encryptedContractCertPrivateKey);
				certificateInstallationRes.getContractSignatureEncryptedPrivateKey().setId("id2"); // contractSignatureEncryptedPrivateKey
				certificateInstallationRes.setDHpublickey(SecurityUtils.getDHPublicKey(ecKeyPair));
				certificateInstallationRes.getDHpublickey().setId("id3"); // dhPublicKey
				certificateInstallationRes.setEMAID(SecurityUtils.getEMAID(saContractCertificateChain));
				certificateInstallationRes.getEMAID().setId("id4"); // emaid
				certificateInstallationRes.setSAProvisioningCertificateChain(
						getCommSessionContext().getBackendInterface().getCPSCertificateChain());
				
				// Set xml reference elements
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getContractSignatureCertChain().getId(), 
						SecurityUtils.generateDigest(
								certificateInstallationRes.getContractSignatureCertChain().getId(),
								getMessageHandler().getJaxbElement(certificateInstallationRes.getContractSignatureCertChain())));
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getContractSignatureEncryptedPrivateKey().getId(),
						SecurityUtils.generateDigest(
								certificateInstallationRes.getContractSignatureEncryptedPrivateKey().getId(),
								getMessageHandler().getJaxbElement(certificateInstallationRes.getContractSignatureEncryptedPrivateKey())));
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getDHpublickey().getId(), 
						SecurityUtils.generateDigest(
								certificateInstallationRes.getDHpublickey().getId(),
								getMessageHandler().getJaxbElement(certificateInstallationRes.getDHpublickey())));
				getXMLSignatureRefElements().put(
						certificateInstallationRes.getEMAID().getId(), 
						SecurityUtils.generateDigest(
								certificateInstallationRes.getEMAID().getId(),
								getMessageHandler().getJaxbElement(certificateInstallationRes.getEMAID())));
				
				// Set signing private key
				setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getCPSLeafPrivateKey());
			
			} else {
				setMandatoryFieldsForFailedRes(certificateInstallationRes, certificateInstallationRes.getResponseCode());
			}
		} else {
			if (certificateInstallationRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new CertificateInstallationResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, certificateInstallationRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(certificateInstallationRes, certificateInstallationRes.getResponseCode());
			}
		}
		
		return getSendMessage(certificateInstallationRes, V2GMessages.PAYMENT_DETAILS_REQ, certificateInstallationRes.getResponseCode());
	}
	
	private boolean isResponseCodeOK(
			CertificateInstallationReqType certificateInstallationReq,
			CertificateChainType saContractCertificateChain, 
			SignatureType signature) {
		ResponseCodeType responseCode = null;
		X509Certificate oemProvCert = SecurityUtils.getCertificate(
														certificateInstallationReq.getOEMProvisioningCert()
													 );
		
		// Check for FAILED_NoCertificateAvailable
		if (saContractCertificateChain == null || saContractCertificateChain.getCertificate() == null) {
			certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED_NO_CERTIFICATE_AVAILABLE);
			return false;
		}
		
		// Check for FAILED_CertificateExpired
		responseCode = SecurityUtils.verifyValidityPeriod(oemProvCert); 
		if (!responseCode.equals(ResponseCodeType.OK)) {
			certificateInstallationRes.setResponseCode(responseCode);
			return false;
		}
		
		// Check for correct attributes (e.g. correct domain component)
		responseCode = SecurityUtils.verifyLeafCertificateAttributes(oemProvCert, PKI.OEM); 
		if (!responseCode.equals(ResponseCodeType.OK)) {
			// FAILED_CertChainError is not defined for CertificateInstallationRes, that's why I set it to FAILED 
			certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED);
			return false;
		}
		
		// Check for FAILED_CertificateRevoked
		// TODO check for revocation with OCSP
		
		// Verify signature
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(
				certificateInstallationReq.getId(), 
				SecurityUtils.generateDigest(
						certificateInstallationReq.getId(),
						getMessageHandler().getJaxbElement(certificateInstallationReq)));
		
		if (!SecurityUtils.verifySignature(
				signature, 
				getMessageHandler().getJaxbElement(signature.getSignedInfo()),
				verifyXMLSigRefElements, 
				certificateInstallationReq.getOEMProvisioningCert())) {
			certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED_SIGNATURE_ERROR);
			return false;
		}
		
		return true;
	}

	@Override
	public BodyBaseType getResponseMessage() {
		return certificateInstallationRes;
	}
}
