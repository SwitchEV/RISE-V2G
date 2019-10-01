/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
package com.v2gclarity.risev2g.evcc.evController;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.CPStates;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ACEVChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingSessionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVErrorCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVPowerDeliveryParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PMaxScheduleEntryType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PMaxScheduleType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ProfileEntryType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.RelativeTimeIntervalType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleTupleType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;

public class DummyEVController implements IACEVController, IDCEVController {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName()); 
	private V2GCommunicationSessionEVCC commSessionContext;
	private CPStates cpState;
	private int chargingLoopCounter;
	
	public DummyEVController() {
		setCPState(CPStates.STATE_B); // should be signaled before ISO/IEC 15118 stack initializes
		setChargingLoopCounter((short) 0);
	}
	
	@Override
	public PaymentOptionType getPaymentOption() {
		/*
		 * The payment options offered by the SECC should probably be displayed on a HMI in the EV.
		 * A request to the EVController should then be initiated here in order to let the user
		 * choose which offered payment option to use.
		 * 
		 * TODO check [V2G2-828] (selecting payment option related to state B, C)
		 */
		
		// Set default to Plug & Charge
		return PaymentOptionType.CONTRACT;
	}


	@Override
	public EnergyTransferModeType getRequestedEnergyTransferMode() {
		// Set default to AC_THREE_PHASE_CORE. Should normally depend on type of cable plugged into the vehicle inlet
		EnergyTransferModeType requestedEnergyTransferMode = (EnergyTransferModeType) MiscUtils.getPropertyValue("energy.transfermode.requested");
		 
		if (requestedEnergyTransferMode == null)
			return EnergyTransferModeType.AC_THREE_PHASE_CORE;
		else
			return requestedEnergyTransferMode;
	}
	

	@Override
	public JAXBElement<ACEVChargeParameterType> getACEVChargeParamter() {
		ACEVChargeParameterType acEVChargeParameter = new ACEVChargeParameterType();
		acEVChargeParameter.setDepartureTime((long) 7200);  // offset in seconds from sending request
		
		PhysicalValueType eAmount = new PhysicalValueType();
		eAmount.setMultiplier(new Byte("0"));
		eAmount.setUnit(UnitSymbolType.WH);
		eAmount.setValue((short) 5000);
		acEVChargeParameter.setEAmount(eAmount);
		
		PhysicalValueType evMaxVoltage = new PhysicalValueType();
		evMaxVoltage.setMultiplier(new Byte("0"));
		evMaxVoltage.setUnit(UnitSymbolType.V);
		evMaxVoltage.setValue((short) 400);
		acEVChargeParameter.setEVMaxVoltage(evMaxVoltage);
		
		PhysicalValueType evMaxCurrent = new PhysicalValueType();
		evMaxCurrent.setMultiplier(new Byte("0"));
		evMaxCurrent.setUnit(UnitSymbolType.A);
		evMaxCurrent.setValue((short) 32);  
		acEVChargeParameter.setEVMaxCurrent(evMaxCurrent);
		
		PhysicalValueType evMinCurrent = new PhysicalValueType();
		evMinCurrent.setMultiplier(new Byte("0"));
		evMinCurrent.setUnit(UnitSymbolType.A);
		evMinCurrent.setValue((short) 5);  
		acEVChargeParameter.setEVMinCurrent(evMinCurrent);
		
		return new JAXBElement<ACEVChargeParameterType>(new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVChargeParameter"), 
				ACEVChargeParameterType.class, 
				acEVChargeParameter);
	}
	
	
	@Override
	public JAXBElement<DCEVChargeParameterType> getDCEVChargeParamter() {
		PhysicalValueType evMaxCurrent = new PhysicalValueType();
		evMaxCurrent.setMultiplier(new Byte("0"));
		evMaxCurrent.setUnit(UnitSymbolType.A);
		evMaxCurrent.setValue((short) 200);  
		
		PhysicalValueType evMaxVoltage = new PhysicalValueType();
		evMaxVoltage.setMultiplier(new Byte("0"));
		evMaxVoltage.setUnit(UnitSymbolType.V);
		evMaxVoltage.setValue((short) 400);
		
		PhysicalValueType eAmount = new PhysicalValueType();
		eAmount.setMultiplier(new Byte("0"));
		eAmount.setUnit(UnitSymbolType.WH);
		eAmount.setValue((short) 5000);
		
		DCEVChargeParameterType dcEVChargeParameter = new DCEVChargeParameterType();
		dcEVChargeParameter.setDCEVStatus(getDCEVStatus());
		dcEVChargeParameter.setEVMaximumCurrentLimit(evMaxCurrent);
		dcEVChargeParameter.setEVMaximumVoltageLimit(evMaxVoltage);
		dcEVChargeParameter.setEVEnergyRequest(eAmount);

		return new JAXBElement<DCEVChargeParameterType>(new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVChargeParameter"), 
				DCEVChargeParameterType.class, 
				dcEVChargeParameter);
	}

	@Override
	public ChargingProfileType getChargingProfile() {
		ChargingProfileType chargingProfile = new ChargingProfileType();
		
		SAScheduleListType saScheduleList = (SAScheduleListType) getCommSessionContext().getSaSchedules();
		
		// Simply use the first scheduleTuple
		SAScheduleTupleType saScheduleTuple = saScheduleList.getSAScheduleTuple().get(0);
		
		// Just follow the PMaxSchedule
		PMaxScheduleType pMaxSchedule = (PMaxScheduleType) saScheduleTuple.getPMaxSchedule();
		
		List<PMaxScheduleEntryType> pMaxScheduleEntries = pMaxSchedule.getPMaxScheduleEntry();
		
		// Just copy the provided PMaxSchedule
		for (PMaxScheduleEntryType pMaxScheduleEntry : pMaxScheduleEntries) {
			ProfileEntryType chargingProfileEntry = new ProfileEntryType();
			
			PhysicalValueType maxPower = new PhysicalValueType();
			maxPower.setMultiplier(pMaxScheduleEntry.getPMax().getMultiplier());
			maxPower.setUnit(UnitSymbolType.W);
			maxPower.setValue(pMaxScheduleEntry.getPMax().getValue());

			chargingProfileEntry.setChargingProfileEntryMaxPower(maxPower);
			chargingProfileEntry.setChargingProfileEntryMaxNumberOfPhasesInUse(new Byte("3"));
			chargingProfileEntry.setChargingProfileEntryStart(
					((RelativeTimeIntervalType) pMaxScheduleEntry.getTimeInterval().getValue()).getStart()
					);
			
			chargingProfile.getProfileEntry().add(chargingProfileEntry);
		}
		
		return chargingProfile;
	}

	@Override
	public short getChosenSAScheduleTupleID() {
		return getCommSessionContext().getSaSchedules().getSAScheduleTuple().get(0).getSAScheduleTupleID();
	}

	public V2GCommunicationSessionEVCC getCommSessionContext() {
		return commSessionContext;
	}

	@Override
	public void setCommSessionContext(V2GCommunicationSessionEVCC commSessionContext) {
		this.commSessionContext = commSessionContext;
	}

	@Override
	public boolean setCPState(CPStates state) {
		getLogger().debug("Changing to state " + state.toString());
		this.cpState = state;
		return true;
	}

	@Override
	public CPStates getCPState() {
		return cpState;
	}

	@Override
	public DCEVStatusType getDCEVStatus() {
		DCEVStatusType dcEvStatus = new DCEVStatusType();
		dcEvStatus.setEVErrorCode(DCEVErrorCodeType.NO_ERROR);
		dcEvStatus.setEVReady(true);
		dcEvStatus.setEVRESSSOC(new Byte("50"));
		
		return dcEvStatus;
	}

	@Override
	public PhysicalValueType getTargetVoltage() {
		PhysicalValueType targetVoltage = new PhysicalValueType();
		targetVoltage.setMultiplier(new Byte("0"));
		targetVoltage.setUnit(UnitSymbolType.V);
		targetVoltage.setValue((short) 400); 
		
		return targetVoltage;
	}

	@Override
	public PhysicalValueType getTargetCurrent() {
		PhysicalValueType targetCurrent = new PhysicalValueType();
		targetCurrent.setMultiplier(new Byte("0"));
		targetCurrent.setUnit(UnitSymbolType.A);
		targetCurrent.setValue((short) 2); // according to IEC 61851-23, this value should be limited to 2A as it seems (see https://github.com/V2GClarity/RISE-V2G/issues/20)
		
		return targetCurrent;
	}

	@Override
	public boolean isBulkChargingComplete() {
		return false;
	}

	@Override
	public boolean isChargingComplete() {
		return false;
	}

	@Override
	public PhysicalValueType getMaximumVoltageLimit() {
		PhysicalValueType maxVoltageLimit = new PhysicalValueType();
		maxVoltageLimit.setMultiplier(new Byte("0"));
		maxVoltageLimit.setUnit(UnitSymbolType.V);
		maxVoltageLimit.setValue((short) 400); 
		
		return maxVoltageLimit;
	}

	@Override
	public PhysicalValueType getMaximumCurrentLimit() {
		PhysicalValueType maxCurrentLimit = new PhysicalValueType();
		maxCurrentLimit.setMultiplier(new Byte("0"));
		maxCurrentLimit.setUnit(UnitSymbolType.A);
		maxCurrentLimit.setValue((short) 32); 
		
		return maxCurrentLimit;
	}

	@Override
	public PhysicalValueType getMaximumPowerLimit() {
		PhysicalValueType maxPowerLimit = new PhysicalValueType();
		maxPowerLimit.setMultiplier(new Byte("3"));
		maxPowerLimit.setUnit(UnitSymbolType.W);
		maxPowerLimit.setValue((short) 63); 
		
		return maxPowerLimit;
	}

	@Override
	public PhysicalValueType getRemainingTimeToFullSOC() {
		PhysicalValueType remainingTimeToFullSOC = new PhysicalValueType();
		remainingTimeToFullSOC.setMultiplier(new Byte("0"));
		remainingTimeToFullSOC.setUnit(UnitSymbolType.S);
		remainingTimeToFullSOC.setValue((short) 1800);
		
		return remainingTimeToFullSOC;
	}

	@Override
	public PhysicalValueType getRemainingTimeToBulkSOC() {
		PhysicalValueType remainingTimeToBulkSOC = new PhysicalValueType();
		remainingTimeToBulkSOC.setMultiplier(new Byte("0"));
		remainingTimeToBulkSOC.setUnit(UnitSymbolType.S);
		remainingTimeToBulkSOC.setValue((short) 900);
		
		return remainingTimeToBulkSOC;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public void adjustMaxCurrent(PhysicalValueType evseMaxCurrent) {
		short multiplier = (short) (evseMaxCurrent.getMultiplier() & 0xFF);
		getLogger().info("Adjusting max current to " + evseMaxCurrent.getValue() * Math.pow(10, multiplier) + " A");
	}

	@Override
	public boolean isChargingLoopActive() {
		// Keep charging until 100 charging loops are finished
		if (getChargingLoopCounter() < 100) {
			setChargingLoopCounter(getChargingLoopCounter() + 1);
			
			/*
			 * OPTIONAL:
			 * Trigger a renegotiation after 50 charging loops (for testing purposes); you can comment this out if you do not want to test an EV-triggered renegotiation
			 */
			if (getChargingLoopCounter() == 50) {
				getCommSessionContext().setRenegotiationRequested(true);
				getLogger().debug("EV triggered a renegotiation (for testing purposes)");
			}
			
			return true;
		} else 
			
			/*
			 * OPTIONAL:
			 * If you want to trigger a pause of the charging session, then uncomment this line
			 */
			//getCommSessionContext().setChargingSession(ChargingSessionType.PAUSE);
			
			return false;
	}

	public int getChargingLoopCounter() {
		return chargingLoopCounter;
	}

	public void setChargingLoopCounter(int chargingLoopCounter) {
		this.chargingLoopCounter = chargingLoopCounter;
	}

	@Override
	public DCEVPowerDeliveryParameterType getEVPowerDeliveryParameter() {
		DCEVPowerDeliveryParameterType dcEvPowerDeliveryParameter = new DCEVPowerDeliveryParameterType();
		dcEvPowerDeliveryParameter.setBulkChargingComplete(false);
		dcEvPowerDeliveryParameter.setChargingComplete(false);
		dcEvPowerDeliveryParameter.setDCEVStatus(getDCEVStatus());
		
		return dcEvPowerDeliveryParameter;
	}
}
