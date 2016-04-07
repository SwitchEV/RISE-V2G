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

import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.secc.states.ServerState;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PMaxScheduleEntryType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PMaxScheduleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.RelativeTimeIntervalType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SalesTariffEntryType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SalesTariffType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyBackendInterface implements IBackendInterface {

	private V2GCommunicationSessionSECC commSessionContext;
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	
	public DummyBackendInterface(V2GCommunicationSessionSECC commSessionContext) {
		setCommSessionContext(commSessionContext);
	}
	
	@Override
	public SAScheduleListType getSAScheduleList(int maxEntriesSAScheduleTuple, HashMap<String, byte[]> xmlSignatureRefElements) {
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
		pMaxSchedule.getPMaxScheduleEntry().add(createPMaxScheduleEntry("3", (short) 11, 0, 7200L));
		
		/*
		 * SalesTariff (add some meaningful things)
		 * But: If it is instantiated, it must be filled with meaningful data, otherwise there will
		 * occur an error with the EXIDecoder (at least at Vector)
		 * 
		 * IMPORTANT: check that you do not add more sales tariff entries than parameter maxEntriesSAScheduleTuple
		 */
		SalesTariffType salesTariff = new SalesTariffType();
		salesTariff.setId("salesTariff");
		salesTariff.setSalesTariffID((short) 1);
		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(0L, (short) 1));
		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(1800L, (short) 4));
		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(3600L, (short) 2));
		salesTariff.getSalesTariffEntry().add(createSalesTariffEntry(5400L, (short) 3));
		
		// Put 'em all together
		SAScheduleTupleType saScheduleTuple = new SAScheduleTupleType();
		saScheduleTuple.setSAScheduleTupleID((short) 1); 
		saScheduleTuple.setPMaxSchedule(pMaxSchedule);
		saScheduleTuple.setSalesTariff(salesTariff);
		
		SAScheduleListType saScheduleList = new SAScheduleListType();
		saScheduleList.getSAScheduleTuple().add(saScheduleTuple);
		
		// Set xml reference elements (repeat this for every sales tariff)
		xmlSignatureRefElements.put(
				salesTariff.getId(), 
				SecurityUtils.generateDigest(salesTariff, false));
	
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
		pMaxTimeInterval.setStart(0);
		pMaxTimeInterval.setDuration(7200L); // 2 hours
		
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
	public CertificateChainType getContractCertificateChain() {
		return SecurityUtils.getCertificateChain("./contractCert.p12");
	}
	
	@Override
	public ECPrivateKey getContractCertificatePrivateKey() {
		KeyStore keyStore = SecurityUtils.getPKCS12KeyStore(
				"./contractCert.p12", 
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		return SecurityUtils.getPrivateKey(keyStore);
	}
	
	
	@Override
	public ECPrivateKey getSAProvisioningCertificatePrivateKey() {
		KeyStore keyStore = SecurityUtils.getPKCS12KeyStore(
				"./provServiceCert.p12", 
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		return SecurityUtils.getPrivateKey(keyStore);
	}
	
	@Override
	public CertificateChainType getSAProvisioningCertificateChain() {
		return SecurityUtils.getCertificateChain("./provServiceCert.p12");
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
