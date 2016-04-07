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
package org.eclipse.risev2g.secc.backend;

import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;

import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;

public interface IBackendInterface {

	/**
	 * Provides a list of schedules coming from a secondary actor (SAScheduleList) with pMax values
	 * and optional tariff incentives which shall influence the charging behaviour of the EV.
	 * 
	 * @param maxEntriesSAScheduleTuple The maximum number of PMaxEntries and SalesTariff entries allowed by EVCC
	 * @param xmlSignatureRefElements Signature reference parameter provided to put sales tariff IDs and sales tariffs in
	 * @return An SASchedulesType element with a list of secondary actor schedules 
	 */
	public SAScheduleListType getSAScheduleList(int maxEntriesSAScheduleTuple, HashMap<String, byte[]> xmlSignatureRefElements);
	
	
	/**
	 * Provides a certificate chain coming from a secondary actor with the leaf certificate being 
	 * the contract certificate and possible intermediate certificates (sub CAs) included.
	 * @return Certificate chain for contract certificate
	 */
	public CertificateChainType getContractCertificateChain();
	
	
	/**
	 * Provides the private key belonging to the contract certificate.
	 * @return PrivateKey of the contract certificate
	 */
	public ECPrivateKey getContractCertificatePrivateKey();
	
	
	/**
	 * Provides a certificate chain coming from a secondary actor with the leaf certificate being 
	 * the provisioning certificate and possible intermediate certificates (sub CAs) included.
	 * @return Certificate chain for provisioning certificate
	 */
	public CertificateChainType getSAProvisioningCertificateChain();
	
	
	/**
	 * Provides the private key belonging to the SA provisioning certificate.
	 * @return PrivateKey of the SA provisioning certificate
	 */
	public ECPrivateKey getSAProvisioningCertificatePrivateKey();
}
