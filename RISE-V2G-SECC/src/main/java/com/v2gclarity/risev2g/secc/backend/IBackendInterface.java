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
package com.v2gclarity.risev2g.secc.backend;

import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;

public interface IBackendInterface {

	/**
	 * Provides a reference to the current communication session for
	 * this backend interface.
	 * @param commSessionContext The active communication session
	 */
	public void setCommSessionContext(V2GCommunicationSessionSECC commSessionContext);
	
	/**
	 * Provides a list of schedules coming from a secondary actor (SAScheduleList) with pMax values
	 * and optional tariff incentives which shall influence the charging behaviour of the EV.
	 * 
	 * @param maxEntriesSAScheduleTuple The maximum number of PMaxEntries and SalesTariff entries allowed by EVCC
	 * @param departureTime The departure time provided by the EV
	 * @param xmlSignatureRefElements Signature reference parameter provided to put sales tariff IDs and sales tariffs in
	 * @return An SASchedulesType element with a list of secondary actor schedules 
	 */
	public SAScheduleListType getSAScheduleList(
			int maxEntriesSAScheduleTuple, 
			long departureTime,
			HashMap<String, byte[]> xmlSignatureRefElements);
	
	
	/**
	 * Provides a certificate chain coming from a secondary actor with the leaf certificate being 
	 * the contract certificate and possible intermediate certificates (Sub-CAs) included.
	 * 
	 * This interface is to be used for the CertificateUpdate
	 * 
	 * @param oldContractCertificateChain The to-be-updated contract certificate chain
	 * @return Certificate chain for contract certificate
	 */
	public CertificateChainType getContractCertificateChain(CertificateChainType oldContractCertChain);
	
	
	/**
	 * Provides a certificate chain coming from a secondary actor with the leaf certificate being 
	 * the contract certificate and possible intermediate certificates (Sub-CAs) included.
	 * 
	 * This interface is to be used for the CertificateInstallation
	 * 
	 * @param oemProvisioningCert The OEM provisioning certificate
	 * @return Certificate chain for contract certificate
	 */
	public CertificateChainType getContractCertificateChain(X509Certificate oemProvisioningCert);
	
	
	/**
	 * Provides the private key belonging to the contract certificate.
	 * 
	 * @return PrivateKey of the contract certificate
	 */
	public ECPrivateKey getContractCertificatePrivateKey();
	
	
	/**
	 * Provides a certificate chain coming from a secondary actor with the leaf certificate being 
	 * the provisioning certificate and possible intermediate certificates (sub CAs) included.
	 * 
	 * @return Certificate chain for provisioning certificate
	 */
	public CertificateChainType getCPSCertificateChain();
	
	
	/**
	 * Provides the private key belonging to the SA provisioning certificate.
	 * 
	 * @return PrivateKey of the SA provisioning certificate
	 */
	public ECPrivateKey getCPSLeafPrivateKey();
	
	
	/**
	 * Provides the private key belonging to the MO Sub-CA 2 certificate (signature of SalesTariff).
	 * 
	 * @return PrivateKey of the MO Sub-CA 2 certificate
	 */
	public ECPrivateKey getMOSubCA2PrivateKey();
}
