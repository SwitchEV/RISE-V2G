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
package com.v2gclarity.risev2g.secc.evseController;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.IsolationLevelType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyDCEVSEController implements IDCEVSEController {

	private V2GCommunicationSessionSECC commSessionContext;
	private PhysicalValueType targetCurrent;
	private PhysicalValueType targetVoltage;
	@SuppressWarnings("unused")
	private PhysicalValueType maximumEVVoltageLimit;
	@SuppressWarnings("unused")
	private PhysicalValueType maximumEVCurrentLimit;
	@SuppressWarnings("unused")
	private PhysicalValueType maximumEVPowerLimit;
	private IsolationLevelType isolationLevel;
	
	public DummyDCEVSEController() {
		setIsolationLevel(IsolationLevelType.INVALID);
	}
	
	@Override
	public String getEvseID() {
		return "DE*V2G*E12345";
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

	@Override
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
		dcEvseStatus.setEVSEIsolationStatus(getIsolationLevel());
		
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

	@Override
	public IsolationLevelType getIsolationLevel() {
		return isolationLevel;
	}

	@Override
	public void setIsolationLevel(IsolationLevelType isolationLevel) {
		this.isolationLevel = isolationLevel;
	}
}
