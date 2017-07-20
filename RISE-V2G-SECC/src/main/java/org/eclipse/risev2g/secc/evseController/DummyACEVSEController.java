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
package org.eclipse.risev2g.secc.evseController;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.utils.ByteUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyACEVSEController implements IACEVSEController {

	private V2GCommunicationSessionSECC commSessionContext;
	
	public DummyACEVSEController(V2GCommunicationSessionSECC commSessionContext) {
		setCommSessionContext(commSessionContext);
	}
	
	@Override
	public String getEvseID() {
		return "DE*V2G*E12345";
	}

	@Override
	public JAXBElement<ACEVSEChargeParameterType> getACEVSEChargeParameter() {
		ACEVSEChargeParameterType acEVSEChargeParameter = new ACEVSEChargeParameterType();
		
		PhysicalValueType evseNominalVoltage = new PhysicalValueType();
		evseNominalVoltage.setMultiplier((byte) 0);
		evseNominalVoltage.setUnit(UnitSymbolType.V);
		evseNominalVoltage.setValue((short) 230);
		acEVSEChargeParameter.setEVSENominalVoltage(evseNominalVoltage);
		
		PhysicalValueType evseMaxCurrent = new PhysicalValueType();
		evseMaxCurrent.setMultiplier(ByteUtils.toByteFromHexString("00"));
		evseMaxCurrent.setUnit(UnitSymbolType.A);
		evseMaxCurrent.setValue((short) 32);
		acEVSEChargeParameter.setEVSEMaxCurrent(evseMaxCurrent);
		
		acEVSEChargeParameter.setACEVSEStatus(getACEVSEStatus(EVSENotificationType.NONE));
		
		return new JAXBElement<ACEVSEChargeParameterType>(
				new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVSEChargeParameter"),
				ACEVSEChargeParameterType.class, 
				acEVSEChargeParameter);
	}
	
	
	@Override
	public ACEVSEStatusType getACEVSEStatus(EVSENotificationType notification) {
		ACEVSEStatusType acEVSEStatus = new ACEVSEStatusType();
		acEVSEStatus.setEVSENotification((notification != null) ? notification : EVSENotificationType.NONE);
		acEVSEStatus.setNotificationMaxDelay(0);
		acEVSEStatus.setRCD(false);
		
		return acEVSEStatus;
	}

	
	public V2GCommunicationSessionSECC getCommSessionContext() {
		return commSessionContext;
	}

	
	public void setCommSessionContext(V2GCommunicationSessionSECC commSessionContext) {
		this.commSessionContext = commSessionContext;
	}

	
	@Override
	public boolean closeContactor() {
		// A check for CP state B would be necessary
		return true;
	}

	
	@Override
	public boolean openContactor() {
		return true;
	}

	
	@Override
	public MeterInfoType getMeterInfo() {
		MeterInfoType meterInfo = new MeterInfoType();
		meterInfo.setMeterID("1");
		meterInfo.setMeterReading(BigInteger.valueOf(32000));
		meterInfo.setTMeter(System.currentTimeMillis() / 1000);
		
		return meterInfo;
	}
}
