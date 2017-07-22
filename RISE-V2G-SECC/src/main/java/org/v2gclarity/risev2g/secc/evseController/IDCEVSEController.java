/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright 2017 Dr.-Ing. Marc Mültin (V2G Clarity)
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
package org.v2gclarity.risev2g.secc.evseController;

import javax.xml.bind.JAXBElement;

import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEChargeParameterType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;

public interface IDCEVSEController extends IEVSEController {

	
	/**
	 * Returns the charge parameter for DC charging 
	 * @return The EVSE specific charge parameter for the current charging session
	 */
	public JAXBElement<DCEVSEChargeParameterType> getDCEVSEChargeParameter();
	
	
	/**
	 * Sets the target voltage communicated by the EV for the DC charging process
	 * @param targetVoltage The target voltage encapsulated in a PhysicalValueType
	 */
	public void setTargetVoltage(PhysicalValueType targetVoltage);
	
	
	/**
	 * Sets the target voltage communicated by the EV for the DC charging process
	 * @param targetVoltage The target voltage encapsulated in a PhysicalValueType
	 */
	public void setTargetCurrent(PhysicalValueType targetCurrent);
	
	
	/**
	 * Sets the maximum voltage communicated by the EV for the DC charging process
	 * @param maximumVoltage The maximum voltage encapsulated in a PhysicalValueType
	 */
	public void setEVMaximumVoltageLimit(PhysicalValueType maximumVoltage);
	
	
	/**
	 * Sets the maximum current communicated by the EV for the DC charging process
	 * @param maximumCurrent The maximum current encapsulated in a PhysicalValueType
	 */
	public void setEVMaximumCurrentLimit(PhysicalValueType maximumCurrent);
	
	
	/**
	 * Sets the maximum power communicated by the EV for the DC charging process
	 * @param maximumPower The maximum power encapsulated in a PhysicalValueType
	 */
	public void setEVMaximumPowerLimit(PhysicalValueType maximumPower);
	
	
	/**
	 * Returns the present voltage at the EVSE
	 * @return Present voltage given as a PhyiscalValueType
	 */
	public PhysicalValueType getPresentVoltage();
	
	
	/**
	 * Returns the present current at the EVSE
	 * @return Present current given as a PhyiscalValueType
	 */
	public PhysicalValueType getPresentCurrent();
	
	
	/**
	 * Returns the maximum voltage limit of the EVSE for DC charging
	 * @return Maximum voltage limit given as a PhyiscalValueType
	 */
	public PhysicalValueType getEVSEMaximumVoltageLimit();
	
	
	/**
	 * Returns the minimum voltage limit of the EVSE for DC charging
	 * @return Minimum voltage limit given as a PhyiscalValueType
	 */
	public PhysicalValueType getEVSEMinimumVoltageLimit();
	
	
	/**
	 * Returns the maximum current limit of the EVSE for DC charging
	 * @return Maximum current limit given as a PhyiscalValueType
	 */
	public PhysicalValueType getEVSEMaximumCurrentLimit();
	
	
	/**
	 * Returns the minimum current limit of the EVSE for DC charging
	 * @return Minimum current limit given as a PhyiscalValueType
	 */
	public PhysicalValueType getEVSEMinimumCurrentLimit();
	
	
	/**
	 * Returns the maximum power limit of the EVSE for DC charging
	 * @return Maximum power limit given as a PhyiscalValueType
	 */
	public PhysicalValueType getEVSEMaximumPowerLimit();
	
	
	/**
	 * Returns TRUE, if the EVSE has reached its current limit.
	 * @return TRUE, if the EVSE has reached its current limit, false otherwise
	 */
	public boolean isEVSECurrentLimitAchieved();
	
	
	/**
	 * Returns TRUE, if the EVSE has reached its voltage limit.
	 * @return TRUE, if the EVSE has reached its voltage limit, false otherwise
	 */
	public boolean isEVSEVoltageLimitAchieved();
	
	
	/**
	 * Returns TRUE, if the EVSE has reached its power limit.
	 * @return TRUE, if the EVSE has reached its power limit, false otherwise
	 */
	public boolean isEVSEPowerLimitAchieved();
	
	
	/**
	 * Returns the peak-to-peak magnitude of the current ripple of the EVSE
	 * @return Peak given as a PhyiscalValueType
	 */
	public PhysicalValueType getEVSEPeakCurrentRipple();
	
	
	/**
	 * Returns the EVSE status for AC charging comprising notification, maxDelay and RCD
	 * @return The EVSE specific status
	 */
	public DCEVSEStatusType getDCEVSEStatus(EVSENotificationType notification);
}
