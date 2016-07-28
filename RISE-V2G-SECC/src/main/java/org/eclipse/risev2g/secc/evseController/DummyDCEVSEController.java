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
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyDCEVSEController implements IDCEVSEController {

	private V2GCommunicationSessionSECC commSessionContext;
	private PhysicalValueType targetCurrent;
	private PhysicalValueType targetVoltage;
	private PhysicalValueType maximumEVVoltageLimit;
	private PhysicalValueType maximumEVCurrentLimit;
	private PhysicalValueType maximumEVPowerLimit;
	
	public DummyDCEVSEController(V2GCommunicationSessionSECC commSessionContext) {
		setCommSessionContext(commSessionContext);
	}
	
	@Override
	public String getEvseID() {
		return "EVSEID-0";
	}
	
	
	@Override
	public JAXBElement<DCEVSEChargeParameterType> getDCEVSEChargeParameter() {
		DCEVSEChargeParameterType dcEVSEChargeParameter = new DCEVSEChargeParameterType();
		
		dcEVSEChargeParameter.setDCEVSEStatus(getDCEVSEStatus(EVSENotificationType.NONE));
		dcEVSEChargeParameter.setEVSEMaximumCurrentLimit(getEVSEMaximumCurrentLimit());
		dcEVSEChargeParameter.setEVSEMaximumPowerLimit(getEVSEMaximumPowerLimit());
		dcEVSEChargeParameter.setEVSEMaximumVoltageLimit(getEVSEMaximumVoltageLimit());
		dcEVSEChargeParameter.setEVSEMinimumCurrentLimit(getEVSEMinimumCurrentLimit());
		dcEVSEChargeParameter.setEVSEMinimumVoltageLimit(getEVSEMinimumVoltageLimit());
		dcEVSEChargeParameter.setEVSEPeakCurrentRipple(getEVSEPeakCurrentRipple());
		
		return new JAXBElement<DCEVSEChargeParameterType>(
				new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVSEChargeParameter"),
				DCEVSEChargeParameterType.class, 
				dcEVSEChargeParameter);
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
	public DCEVSEStatusType getDCEVSEStatus(EVSENotificationType notification) {
		DCEVSEStatusType dcEvseStatus = new DCEVSEStatusType();
		dcEvseStatus.setNotificationMaxDelay(0);
		dcEvseStatus.setEVSENotification((notification != null) ? notification : EVSENotificationType.NONE);
		dcEvseStatus.setEVSEStatusCode(DCEVSEStatusCodeType.EVSE_READY);
//		dcEvseStatus.setEVSEIsolationStatus(IsolationLevelType.INVALID);
		
		return dcEvseStatus;
	}

	@Override
	public void setTargetVoltage(PhysicalValueType targetVoltage) {
		this.targetVoltage = targetVoltage;
	}

	@Override
	public void setTargetCurrent(PhysicalValueType targetCurrent) {
		this.targetCurrent = targetCurrent;
	}

	@Override
	public PhysicalValueType getPresentVoltage() {
		return this.targetVoltage;
	}
	
	
	@Override
	public PhysicalValueType getPresentCurrent() {
		return this.targetCurrent;
	}

	@Override
	public void setEVMaximumVoltageLimit(PhysicalValueType maximumVoltageLimit) {
		this.maximumEVVoltageLimit = maximumVoltageLimit;
	}
	
	
	@Override
	public void setEVMaximumCurrentLimit(PhysicalValueType maximumCurrentLimit) {
		this.maximumEVCurrentLimit = maximumCurrentLimit;
	}

	@Override
	public void setEVMaximumPowerLimit(PhysicalValueType maximumPowerLimit) {
		this.maximumEVPowerLimit = maximumPowerLimit;
	}


	@Override
	public PhysicalValueType getEVSEMaximumVoltageLimit() {
		PhysicalValueType evseMaxVoltageLimit = new PhysicalValueType();
		
		evseMaxVoltageLimit.setMultiplier(new Byte("0"));
		evseMaxVoltageLimit.setUnit(UnitSymbolType.V);
		evseMaxVoltageLimit.setValue((short) 400);
		
		return evseMaxVoltageLimit;
	}
	
	
	@Override
	public PhysicalValueType getEVSEMinimumVoltageLimit() {
		PhysicalValueType evseMinVoltageLimit = new PhysicalValueType();
		
		evseMinVoltageLimit.setMultiplier(new Byte("0"));
		evseMinVoltageLimit.setUnit(UnitSymbolType.V);
		evseMinVoltageLimit.setValue((short) 230);
		
		return evseMinVoltageLimit;
	}
	

	@Override
	public PhysicalValueType getEVSEMaximumCurrentLimit() {
		PhysicalValueType evseMaxCurrentLimit = new PhysicalValueType();
		
		evseMaxCurrentLimit.setMultiplier(new Byte("0"));
		evseMaxCurrentLimit.setUnit(UnitSymbolType.A);
		evseMaxCurrentLimit.setValue((short) 32);
		
		return evseMaxCurrentLimit;
	}
	
	
	@Override
	public PhysicalValueType getEVSEMinimumCurrentLimit() {
		PhysicalValueType evseMinCurrentLimit = new PhysicalValueType();
		
		evseMinCurrentLimit.setMultiplier(new Byte("0"));
		evseMinCurrentLimit.setUnit(UnitSymbolType.A);
		evseMinCurrentLimit.setValue((short) 16);
		
		return evseMinCurrentLimit;
	}

	@Override
	public PhysicalValueType getEVSEMaximumPowerLimit() {
		PhysicalValueType evseMaxPowerLimit = new PhysicalValueType();
		
		evseMaxPowerLimit.setMultiplier(new Byte("3"));
		evseMaxPowerLimit.setUnit(UnitSymbolType.W);
		evseMaxPowerLimit.setValue((short) 63);
		
		return evseMaxPowerLimit;
	}

	@Override
	public boolean isEVSECurrentLimitAchieved() {
		return false;
	}

	@Override
	public boolean isEVSEVoltageLimitAchieved() {
		return false;
	}

	@Override
	public boolean isEVSEPowerLimitAchieved() {
		return false;
	}

	@Override
	public MeterInfoType getMeterInfo() {
		MeterInfoType meterInfo = new MeterInfoType();
		meterInfo.setMeterID("1");
		meterInfo.setMeterReading(BigInteger.valueOf(32000));
		meterInfo.setTMeter(System.currentTimeMillis() / 1000);
		
		return meterInfo;
	}

	@Override
	public PhysicalValueType getEVSEPeakCurrentRipple() {
		PhysicalValueType peakCurrentRipple = new PhysicalValueType();
		
		peakCurrentRipple.setMultiplier(new Byte("0"));
		peakCurrentRipple.setUnit(UnitSymbolType.A);
		peakCurrentRipple.setValue((short) 0);  // what is a peak-to-peak current ripple??
		
		return peakCurrentRipple;
	}
}
