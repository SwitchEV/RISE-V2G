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
package org.eclipse.risev2g.evcc.states;

import java.security.KeyStore;
import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.utils.SecurityUtils.ContractCertificateStatus;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateInstallationReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateUpdateReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionResType;

public class WaitForPaymentServiceSelectionRes extends ClientState {
	
	public WaitForPaymentServiceSelectionRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PaymentServiceSelectionResType.class)) {
			if (getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.CONTRACT)) {
				
				if (getCommSessionContext().getContractCertStatus().equals(ContractCertificateStatus.UNKNOWN)) {
					getCommSessionContext().setContractCertStatus(SecurityUtils.getContractCertificateStatus());
				}
				
				// 1. Check if certificate installation is needed
				if (getCommSessionContext().getContractCertStatus().equals(ContractCertificateStatus.INSTALLATION_NEEDED)) {
					if (getCommSessionContext().isCertificateServiceAvailable((short) 1)) {
						getLogger().info("Trying to install new contract certificate");
						return getSendMessage(getCertificateInstallationReq(), V2GMessages.CERTIFICATE_INSTALLATION_RES);
					} else return new TerminateSession("Certificate installation needed but service is not available");
				}
				
				// 2. Check if certificate update is needed (means: certificate is available but expires soon)
				if (getCommSessionContext().getContractCertStatus().equals(ContractCertificateStatus.UPDATE_NEEDED)) {
					if (getCommSessionContext().isCertificateServiceAvailable((short) 2)) {
						getLogger().info("Trying to update contract certificate");
						return getSendMessage(getCertificateUpdateReq(), V2GMessages.CERTIFICATE_UPDATE_RES);
					} else return new TerminateSession("Certificate update needed but service is not available");
				}

				return getSendMessage(getPaymentDetailsReq(), V2GMessages.PAYMENT_DETAILS_RES);
			} else if (getCommSessionContext().getSelectedPaymentOption().equals(PaymentOptionType.EXTERNAL_PAYMENT)) {
				return getSendMessage(getAuthorizationReq(null), V2GMessages.AUTHORIZATION_RES);
			} else {
				return new TerminateSession("No valid PaymentOptionType available");
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
	
	
	private CertificateInstallationReqType getCertificateInstallationReq() {
		KeyStore evccKeyStore = SecurityUtils.getKeyStore(
				GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		
		CertificateInstallationReqType certInstallationReq = new CertificateInstallationReqType();
		/*
		 * Experience from the test symposium in San Diego (April 2016):
		 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
		 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
		 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
		 */
		certInstallationReq.setId("ID1");
		certInstallationReq.setListOfRootCertificateIDs(
				SecurityUtils.getListOfRootCertificateIDs(
						GlobalValues.EVCC_TRUSTSTORE_FILEPATH.toString(),
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()));
		certInstallationReq.setOEMProvisioningCert(
				SecurityUtils.getCertificateChain(
						evccKeyStore, GlobalValues.ALIAS_OEM_PROV_CERTIFICATE.toString()).getCertificate());
		
		// Set xml reference element
		getXMLSignatureRefElements().put(certInstallationReq.getId(), SecurityUtils.generateDigest(certInstallationReq));
		
		// Set signing private key
		setSignaturePrivateKey(SecurityUtils.getPrivateKey(
				SecurityUtils.getKeyStore(
						GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()), 
				GlobalValues.ALIAS_OEM_PROV_CERTIFICATE.toString())
		);
		
		return certInstallationReq;
	}
	
	
	private CertificateUpdateReqType getCertificateUpdateReq() {
		CertificateUpdateReqType certificateUpdateReq = new CertificateUpdateReqType();
		certificateUpdateReq.setContractSignatureCertChain(
				SecurityUtils.getCertificateChain(
						SecurityUtils.getKeyStore(
								GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
								GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()), 
						GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString()));	
		certificateUpdateReq.setEMAID(SecurityUtils.getEMAID(GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()).getValue());
		/*
		 * Experience from the test symposium in San Diego (April 2016):
		 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
		 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
		 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
		 */
		certificateUpdateReq.setId("ID1");
		certificateUpdateReq.setListOfRootCertificateIDs(
				SecurityUtils.getListOfRootCertificateIDs(
						GlobalValues.EVCC_TRUSTSTORE_FILEPATH.toString(),
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()));
		
		// Set xml reference element
		getXMLSignatureRefElements().put(
				certificateUpdateReq.getId(), 
				SecurityUtils.generateDigest(certificateUpdateReq));
		
		// Set signing private key
		setSignaturePrivateKey(SecurityUtils.getPrivateKey(
				SecurityUtils.getKeyStore(
						GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()), 
				GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString())
		);
		
		return certificateUpdateReq;
	}
}
