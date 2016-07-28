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
package org.eclipse.risev2g.secc.backend;

import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PMaxScheduleEntryType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PMaxScheduleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.RelativeTimeIntervalType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyBackendInterface implements IBackendInterface {

	private V2GCommunicationSessionSECC commSessionContext;
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	
	public DummyBackendInterface(V2GCommunicationSessionSECC commSessionContext) {
		setCommSessionContext(commSessionContext);
	}
	
	@Override
	public SAScheduleListType getSAScheduleList() {
		/*
		 * PMaxSchedule
		 */
		PhysicalValueType pMaxValue = new PhysicalValueType();
		pMaxValue.setMultiplier(new Byte("3"));
		pMaxValue.setUnit(UnitSymbolType.W);
		pMaxValue.setValue((short) 11);
		
		RelativeTimeIntervalType timeInterval = new RelativeTimeIntervalType();
		timeInterval.setStart(0);
		timeInterval.setDuration(3600L);
		
		PMaxScheduleEntryType pMaxScheduleEntry = new PMaxScheduleEntryType();
		pMaxScheduleEntry.setTimeInterval(new JAXBElement<RelativeTimeIntervalType>(
				new QName("urn:iso:15118:2:2013:MsgDataTypes", "RelativeTimeInterval"),
				RelativeTimeIntervalType.class, 
				timeInterval));
		pMaxScheduleEntry.setPMax(pMaxValue);
		
		PMaxScheduleType pMaxSchedule = new PMaxScheduleType();
		pMaxSchedule.getPMaxScheduleEntry().add(pMaxScheduleEntry);
		
		
		/*
		 * SalesTariff (add some meaningful things)
		 * But: If it is instantiated, it must be filled with meaningful data, otherwise there will
		 * occur an error with the EXIDecoder (at least at Vector)
		 */
		
		
		/*
		 * Put 'em all together
		 */
		SAScheduleTupleType saScheduleTuple = new SAScheduleTupleType();
		saScheduleTuple.setSAScheduleTupleID((short) 1); 
		saScheduleTuple.setPMaxSchedule(pMaxSchedule);
//		saScheduleTuple.setSalesTariff(salesTariff);
		
		SAScheduleListType saScheduleList = new SAScheduleListType();
		saScheduleList.getSAScheduleTuple().add(saScheduleTuple);

		return saScheduleList;
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
