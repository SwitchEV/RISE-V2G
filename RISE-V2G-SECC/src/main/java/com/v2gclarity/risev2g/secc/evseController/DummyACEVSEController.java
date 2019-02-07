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
import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVSEChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyACEVSEController implements IACEVSEController {

	@SuppressWarnings("unused")
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
