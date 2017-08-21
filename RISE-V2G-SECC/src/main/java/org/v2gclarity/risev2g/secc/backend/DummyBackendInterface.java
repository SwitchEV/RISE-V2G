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
package org.v2gclarity.risev2g.secc.backend;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.utils.SecurityUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EMAIDType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PMaxScheduleEntryType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PMaxScheduleType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.RelativeTimeIntervalType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SalesTariffEntryType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SalesTariffType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyBackendInterface implements IBackendInterface {

	private V2GCommunicationSessionSECC commSessionContext;
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	
	public DummyBackendInterface(V2GCommunicationSessionSECC commSessionContext) {
		setCommSessionContext(commSessionContext);
	}
	
	@Override
	public SAScheduleListType getSAScheduleList(
			int maxEntriesSAScheduleTuple, 
			long departureTime,
			HashMap<String, byte[]> xmlSignatureRefElements) {
		/*
		 * Some important requirements:
		 * 
		 * 1. The sum of the individual time intervals described in the PMaxSchedule and
		 * SalesTariff provided in the ChargeParameterDiscoveryRes message shall match
		 * the period of time indicated by the EVCC in the message element DepartureTime of the
		 * ChargeParameterDiscoveryReq message.
		 * 
		 * 2. If the EVCC did not provide a DepartureTime Target Setting in the ChargeParameterDiscoveryReq 
		 * message, the sum of the individual time intervals described in the PMaxSchedule and SalesTariff 
		 * provided in the ChargeParameterDiscoveryRes message, shall be greater or equal to 24 hours.
		 * 
		 * 3. If the number of SalesTariffEntry elements in the SalesTariff or the number of
		 * PMaxScheduleEntry elements in the PMaxSchedule provided by the secondary actor(s) are not
		 * covering the entire period of time until DepartureTime, the Target Setting EAmount has not 
		 * been met and the communication session has not been finished, it is the responsibility of 
		 * the EVCC to request a new element of type SAScheduleListType as soon as the last 
		 * SalesTariffEntry element or the last PMaxScheduleEntry element becomes active by sending 
		 * a new ChargeParameterDiscoveryReq message.
		 * 
		 * 4. In case of PnC, and if a Tariff Table is used by the secondary actor, the secondary actor SHALL
		 * sign the field SalesTariff of type SalesTariffType. In case of EIM, the secondary actor MAY sign
		 * this field.
		 * 
		 * 5. The SECC shall 'copy' (not change!) the signature value received from the SA and transmit this value in the
		 * header of the ChargeParameterDiscoveryRes message.
		 * 
		 * 6. 
		 * If the element SalesTariff is signed, it shall be signed by the same private key that was used to
		 * issue the leaf contract certificate that the EVCC used during this connection for contract
		 * authentication (PnC).
		 * 
		 * 7. An EVCC shall support 12 entries for PMaxScheduleEntry and SalesTariffEntry elements inside
		 * one SAScheduleTuple if MaxEntriesSAScheduleTuple is not transmitted in ChargeParameterDiscoveryReq.
		 * 
		 * 8. The valid range for the value of EPriceLevel element shall be defined as being between 0 and
		 * the value of NumEPriceLevels element including the boundary values.
		 */
		
		// PMaxSchedule
		// IMPORTANT: check that you do not add more pMax entries than parameter maxEntriesSAScheduleTuple
		PMaxScheduleType pMaxSchedule = new PMaxScheduleType();
		
		if (departureTime != 0)
			pMaxSchedule.getPMaxScheduleEntry().add(createPMaxScheduleEntry("3", (short) 11, 0, departureTime));
		else
			pMaxSchedule.getPMaxScheduleEntry().add(createPMaxScheduleEntry("3", (short) 11, 0, 86400L));
		
		/*
		 * SalesTariff (add some meaningful things)
		 * But: If it is instantiated, it must be filled with meaningful data, otherwise there will
		 * occur an error with the EXIDecoder (at least at Vector)
		 * 
		 * IMPORTANT: check that you do not add more sales tariff entries than parameter maxEntriesSAScheduleTuple
		 */
		SalesTariffType salesTariff = new SalesTariffType();
		/*
		 * Experience from the test symposium in San Diego (April 2016):
		 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
		 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
		 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
		 */
		salesTariff.setId("ID1"); 
		salesTariff.setSalesTariffID((short) 1);
		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(0L, (short) 1));
//		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(1800L, (short) 4));
//		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(3600L, (short) 2));
//		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(5400L, (short) 3));
		
		// Put 'em all together
		SAScheduleTupleType saScheduleTuple = new SAScheduleTupleType();
		saScheduleTuple.setSAScheduleTupleID((short) 1); 
		saScheduleTuple.setPMaxSchedule(pMaxSchedule);
		saScheduleTuple.setSalesTariff(salesTariff);
		
		SAScheduleListType saScheduleList = new SAScheduleListType();
		saScheduleList.getSAScheduleTuple().add(saScheduleTuple);
		
		// Set XML reference elements for SalesTariff elements (repeat this for every sales tariff) if they are sent
		if (saScheduleTuple.getSalesTariff() != null) {
			xmlSignatureRefElements.put(
					salesTariff.getId(), 
					SecurityUtils.generateDigest(getCommSessionContext().getMessageHandler().getJaxbElement(salesTariff)));
		}
	
		return saScheduleList;
	}
	
	private SalesTariffEntryType createSalesTariffEntry(long start, short ePriceLevel) {
		RelativeTimeIntervalType salesTariffTimeInterval = new RelativeTimeIntervalType();
		salesTariffTimeInterval.setStart(start);
		
		SalesTariffEntryType salesTariffEntry = new SalesTariffEntryType();
		salesTariffEntry.setTimeInterval(new JAXBElement<RelativeTimeIntervalType>(
				new QName("urn:iso:15118:2:2013:MsgDataTypes", "RelativeTimeInterval"),
				RelativeTimeIntervalType.class, 
				salesTariffTimeInterval));
		salesTariffEntry.setEPriceLevel(ePriceLevel);
		
		return salesTariffEntry;
	}
	
	private PMaxScheduleEntryType createPMaxScheduleEntry(String multiplier, short pMax, long start) {
		PhysicalValueType pMaxValue = new PhysicalValueType();
		pMaxValue.setMultiplier(new Byte(multiplier));
		pMaxValue.setUnit(UnitSymbolType.W);
		pMaxValue.setValue(pMax);
		
		RelativeTimeIntervalType pMaxTimeInterval = new RelativeTimeIntervalType();
		pMaxTimeInterval.setStart(start);
		
		PMaxScheduleEntryType pMaxScheduleEntry = new PMaxScheduleEntryType();
		pMaxScheduleEntry.setTimeInterval(new JAXBElement<RelativeTimeIntervalType>(
				new QName("urn:iso:15118:2:2013:MsgDataTypes", "RelativeTimeInterval"),
				RelativeTimeIntervalType.class, 
				pMaxTimeInterval));
		pMaxScheduleEntry.setPMax(pMaxValue);
		
		return pMaxScheduleEntry;
	}
	
	private PMaxScheduleEntryType createPMaxScheduleEntry(String multiplier, short pMax, long start, long duration) {
		PMaxScheduleEntryType pMaxScheduleEntry = createPMaxScheduleEntry(multiplier, pMax, start);
		((RelativeTimeIntervalType) pMaxScheduleEntry.getTimeInterval().getValue()).setDuration(duration);
		
		return pMaxScheduleEntry;
	}


	@Override
	public CertificateChainType getContractCertificateChain(X509Certificate oemProvisioningCert) {
		/*
		 * Normally, a backend protocol such as OCPP would be used to retrieve the contract certificate chain
		 * based on the OEM provisioning certificate
		 */
		return SecurityUtils.getCertificateChain("./moCertChain.p12");
	}
	
	@Override
	public CertificateChainType getContractCertificateChain(CertificateChainType oldContractCertChain) {
		/*
		 * Normally, a backend protocol such as OCPP would be used to retrieve the new contract certificate chain
		 * based on the to-be-updated old contract certificate chain
		 */
		EMAIDType providedEMAID = SecurityUtils.getEMAID(oldContractCertChain);
		
		/*
		 * NOTE 1: You need to agree with your test partner on valid, authorized EMAIDs that you put into this list.
		 * 
		 * NOTE 2: Not the EMAID given as a parameter of CertificateUpdateReq is checked (error prone), but the EMAID
		 * provided in the common name field of the to-be-updated contract certificate
		 */
		ArrayList<EMAIDType> authorizedEMAIDs = new ArrayList<EMAIDType>();
		
		EMAIDType authorizedEMAID1 = new EMAIDType();
		authorizedEMAID1.setId("id1");
		authorizedEMAID1.setValue("DE1ABCD2EF357A");
		
		EMAIDType authorizedEMAID2 = new EMAIDType();
		authorizedEMAID2.setId("id2");
		authorizedEMAID2.setValue("DE1ABCD2EF357C");
		
		authorizedEMAIDs.add(authorizedEMAID1);
		authorizedEMAIDs.add(authorizedEMAID2);
		
		boolean emaidFound = false;
		
		for (EMAIDType emaid : authorizedEMAIDs) {
			if (emaid.getValue().equals(providedEMAID.getValue()))
				emaidFound = true;
		}
		
		if (emaidFound)
			return SecurityUtils.getCertificateChain("./moCertChain.p12");
		else {
			getLogger().warn("EMAID '" + providedEMAID.getValue() + "' (read from common name field of contract "
						   + "certificate) is not authorized");
			return null;
		}
			
	}
	
	
	@Override
	public ECPrivateKey getContractCertificatePrivateKey() {
		KeyStore keyStore = SecurityUtils.getPKCS12KeyStore(
				"./moCertChain.p12", 
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		ECPrivateKey privateKey = SecurityUtils.getPrivateKey(keyStore);
		
		if (privateKey == null) 
			getLogger().error("No private key available from contract certificate keystore");
		
		return privateKey;
	}
	
	
	@Override
	public ECPrivateKey getCPSLeafPrivateKey() {
		KeyStore keyStore = SecurityUtils.getPKCS12KeyStore(
				"./cpsCertChain.p12", 
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		ECPrivateKey privateKey = SecurityUtils.getPrivateKey(keyStore);
		
		if (privateKey == null) 
			getLogger().error("No private key available from Certificate Provisioning Service keystore");
		
		return privateKey;
	}
	
	@Override
	public ECPrivateKey getMOSubCA2PrivateKey() {
		ECPrivateKey privateKey = SecurityUtils.getPrivateKey("./moSubCA2.pkcs8.der");
		
		if (privateKey == null) 
			getLogger().error("No private key available from MO Sub-CA 2 PKCS#8 file");
		
		return privateKey;
	}
	
	
	@Override
	public CertificateChainType getCPSCertificateChain() {
		return SecurityUtils.getCertificateChain("./cpsCertChain.p12");
	}
	
	
	public V2GCommunicationSessionSECC getCommSessionContext() {
		return commSessionContext;
	}

	public void setCommSessionContext(V2GCommunicationSessionSECC commSessionContext) {
		this.commSessionContext = commSessionContext;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
