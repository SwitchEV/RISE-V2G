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
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DiffieHellmanPublickeyType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EMAIDType;
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
				KeyPair ecdhKeyPair = SecurityUtils.getECKeyPair();
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
				certificateUpdateRes.setEMAID(SecurityUtils.getEMAID(contractCertificateChain));
				certificateUpdateRes.getEMAID().setId("id4"); // emaid
				certificateUpdateRes.setSAProvisioningCertificateChain(getCommSessionContext().getBackendInterface().getCPSCertificateChain());
				
				// In case of negative response code, try at next charging (retryCounter = 0)
				if (!certificateUpdateRes.getResponseCode().toString().startsWith("OK"))
					certificateUpdateRes.setRetryCounter((short) 0);
				
				// Set xml reference elements
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getContractSignatureCertChain().getId(), 
						SecurityUtils.generateDigest(certificateUpdateRes.getContractSignatureCertChain()));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getContractSignatureEncryptedPrivateKey().getId(),
						SecurityUtils.generateDigest(certificateUpdateRes.getContractSignatureEncryptedPrivateKey()));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getDHpublickey().getId(), 
						SecurityUtils.generateDigest(certificateUpdateRes.getDHpublickey()));
				getXMLSignatureRefElements().put(
						certificateUpdateRes.getEMAID().getId(), 
						SecurityUtils.generateDigest(certificateUpdateRes.getEMAID()));
				
				// Set signing private key
				setSignaturePrivateKey(getCommSessionContext().getBackendInterface().getCPSLeafPrivateKey());
			} else {
				getLogger().error("Response code '" + certificateUpdateRes.getResponseCode() + "' will be sent");
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
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
		verifyXMLSigRefElements.put(certificateUpdateReq.getId(), SecurityUtils.generateDigest(certificateUpdateReq));

		if (!SecurityUtils.verifySignature(
				signature, 
				verifyXMLSigRefElements, 
				certificateUpdateReq.getContractSignatureCertChain().getCertificate())) {
			certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_SIGNATURE_ERROR);
			return false;
		}
		
		return true;
	}
	
	
	@Override
	protected void setMandatoryFieldsForFailedRes() {
		CertificateChainType saProvisioningCertificateChain = new CertificateChainType();
		saProvisioningCertificateChain.setCertificate(new byte[1]);
		certificateUpdateRes.setSAProvisioningCertificateChain(saProvisioningCertificateChain);
		
		CertificateChainType contractSignatureCertChain = new CertificateChainType();
		contractSignatureCertChain.setCertificate(new byte[1]);
		contractSignatureCertChain.setId("ID1");
		certificateUpdateRes.setContractSignatureCertChain(contractSignatureCertChain);
		
		ContractSignatureEncryptedPrivateKeyType contractSignatureEncryptedPrivateKey = new ContractSignatureEncryptedPrivateKeyType();
		contractSignatureEncryptedPrivateKey.setValue(new byte[1]);
		contractSignatureEncryptedPrivateKey.setId("ID2");
		certificateUpdateRes.setContractSignatureEncryptedPrivateKey(contractSignatureEncryptedPrivateKey);
		
		DiffieHellmanPublickeyType dhPublicKeyType = new DiffieHellmanPublickeyType();
		dhPublicKeyType.setValue(new byte[1]);
		dhPublicKeyType.setId("ID3");
		certificateUpdateRes.setDHpublickey(dhPublicKeyType);
		
		EMAIDType emaid = new EMAIDType();
		emaid.setValue("DEV2G1234512345");
		emaid.setId("ID4");
		certificateUpdateRes.setEMAID(emaid);
	}
}
