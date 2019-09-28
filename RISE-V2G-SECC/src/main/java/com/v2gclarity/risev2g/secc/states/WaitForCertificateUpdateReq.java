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
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.HashMap;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.PKI;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateUpdateReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateUpdateResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			
			if (isResponseCodeOK(
					certificateUpdateReq, 
					v2gMessageReq.getHeader().getSignature())) {
				// The ECDH (elliptic curve Diffie Hellman) key pair is also needed for the generation of the shared secret
				KeyPair ecdhKeyPair = SecurityUtils.getECKeyPair();
				if (ecdhKeyPair == null) {
					getLogger().error("ECDH keypair could not be generated");
					return null;
				}
				
				// Encrypt private key
				ContractSignatureEncryptedPrivateKeyType encryptedContractCertPrivateKey = 
						SecurityUtils.encryptContractCertPrivateKey(
								(ECPublicKey) SecurityUtils.getCertificate(certificateUpdateReq.getContractSignatureCertChain().getCertificate()).getPublicKey(),
								(ECPrivateKey) ecdhKeyPair.getPrivate(),
								getCommSessionContext().getBackendInterface().getContractCertificatePrivateKey());
				
				/*
				 * Experience from the test symposium in San Diego (April 2016):
				 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
				 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
				 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
				 */
				certificateUpdateRes.getContractSignatureCertChain().setId("id1"); // contractSignatureCertChain
				certificateUpdateRes.setContractSignatureEncryptedPrivateKey(encryptedContractCertPrivateKey);
				certificateUpdateRes.getContractSignatureEncryptedPrivateKey().setId("id2"); // contractSignatureEncryptedPrivateKey
				certificateUpdateRes.setDHpublickey(SecurityUtils.getDHPublicKey(ecdhKeyPair));
				certificateUpdateRes.getDHpublickey().setId("id3"); // dhPublicKey
				certificateUpdateRes.setEMAID(SecurityUtils.getEMAID(certificateUpdateReq.getContractSignatureCertChain()));
				certificateUpdateRes.getEMAID().setId("id4"); // emaid
				certificateUpdateRes.setSAProvisioningCertificateChain(getCommSessionContext().getBackendInterface().getCPSCertificateChain());
				
				// In case of negative response code, try at next charging (retryCounter = 0)
				if (!certificateUpdateRes.getResponseCode().toString().startsWith("OK"))
					certificateUpdateRes.setRetryCounter((short) 0);
				
				// Set xml reference elements
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getContractSignatureCertChain().getId(), 
						SecurityUtils.generateDigest(
								certificateUpdateRes.getContractSignatureCertChain().getId(),
								getMessageHandler().getJaxbElement(certificateUpdateRes.getContractSignatureCertChain())));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getId(),
						SecurityUtils.generateDigest(
								certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getId(),
								getMessageHandler().getJaxbElement(certificateUpdateRes.getContractSignatureEncryptedPrivateKey())));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getDHpublickey().getId(), 
						SecurityUtils.generateDigest(
								certificateUpdateRes.getDHpublickey().getId(),
								getMessageHandler().getJaxbElement(certificateUpdateRes.getDHpublickey())));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getEMAID().getId(), 
						SecurityUtils.generateDigest(
								certificateUpdateRes.getEMAID().getId(),
								getMessageHandler().getJaxbElement(certificateUpdateRes.getEMAID())));
				
				// Set signing private key
				setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getCPSLeafPrivateKey());
			} else {
				setMandatoryFieldsForFailedRes(certificateUpdateRes, certificateUpdateRes.getResponseCode());
			}
		} else {
			if (certificateUpdateRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new CertificateUpdateResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, certificateUpdateRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(certificateUpdateRes, certificateUpdateRes.getResponseCode());
			}
		}
		
		return getSendMessage(certificateUpdateRes, 
	  			  			 (certificateUpdateRes.getResponseCode().toString().startsWith("OK") ? 
	  			  			 V2GMessages.PAYMENT_DETAILS_REQ : V2GMessages.NONE),
	  			  			 certificateUpdateRes.getResponseCode()
	 			 			 );
	}
	
	
	private boolean isResponseCodeOK(
			CertificateUpdateReqType certificateUpdateReq, 
			SignatureType signature) {
		// Check for FAILED_NoCertificateAvailable
		CertificateChainType saContractCertificateChain = 
				getCommSessionContext().getBackendInterface().getContractCertificateChain(
						certificateUpdateReq.getContractSignatureCertChain()
				);
		if (saContractCertificateChain == null || saContractCertificateChain.getCertificate() == null) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_NO_CERTIFICATE_AVAILABLE);
			return false;
		} else {
			certificateUpdateRes.setContractSignatureCertChain(saContractCertificateChain);
		}
		
		// Check complete contract certificate chain
		ResponseCodeType certChainResponseCode = SecurityUtils.verifyCertificateChain(
													certificateUpdateReq.getContractSignatureCertChain(),
													GlobalValues.SECC_TRUSTSTORE_FILEPATH.toString(),
													PKI.MO);
		if (!certChainResponseCode.equals(ResponseCodeType.OK)) {
			certificateUpdateRes.setResponseCode(certChainResponseCode);
			return false;
		}
		
		// Check for FAILED_CertificateRevoked
		// TODO check for revocation with OCSP
		
		// Verify signature
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(
				certificateUpdateReq.getId(), 
				SecurityUtils.generateDigest(
						certificateUpdateReq.getId(),
						getMessageHandler().getJaxbElement(certificateUpdateReq)));

		if (!SecurityUtils.verifySignature(
				signature, 
				getMessageHandler().getJaxbElement(signature.getSignedInfo()),
				verifyXMLSigRefElements, 
				certificateUpdateReq.getContractSignatureCertChain().getCertificate())) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_SIGNATURE_ERROR);
			return false;
		}
		
		return true;
	}
	
	@Override
	public BodyBaseType getResponseMessage() {
		return certificateUpdateRes;
	}
}
