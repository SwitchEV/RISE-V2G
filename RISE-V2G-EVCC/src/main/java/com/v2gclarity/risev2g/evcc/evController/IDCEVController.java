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

import javax.xml.bind.JAXBElement;

import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVChargeParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVPowerDeliveryParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVStatusType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;

public interface IDCEVController extends IEVController {

	/**
	 * Returns the charge parameter for AC charging 
	 * @return The battery specific charge parameter for the current charging session
	 */
	public JAXBElement<DCEVChargeParameterType> getDCEVChargeParamter();
	
	
	/**
	 * Returns the EV status parameter for DC charging 
	 * @return The EV status for DC charging for the current charging session
	 */
	public DCEVStatusType getDCEVStatus();
	
	
	/**
	 * Returns the target voltage for DC charging
	 * @return Target voltage given as a PhysicalValueType
	 */
	public PhysicalValueType getTargetVoltage();
	
	
	/**
	 * Returns the target current for DC charging
	 * @return Target current given as a PhysicalValueType
	 */
	public PhysicalValueType getTargetCurrent();
	
	
	/**
	 * If set to TRUE, the EV indicates that bulk charging (approx. 80% SOC) is complete.
	 * @return True, if bulk charge is complete, false otherwise
	 */
	public boolean isBulkChargingComplete();
	
	
	/**
	 * If set to TRUE, the EV indicates that charging process is complete.
	 * @return True, if charging process is complete, false otherwise
	 */
	public boolean isChargingComplete();
	
	
	/**
	 * Returns the maximum voltage limit for DC charging
	 * @return The maximum voltage given as a PhysicalValueType
	 */
	public PhysicalValueType getMaximumVoltageLimit();
	
	
	/**
	 * Returns the maximum current limit for DC charging
	 * @return The maximum current given as a PhysicalValueType
	 */
	public PhysicalValueType getMaximumCurrentLimit();
	
	
	/**
	 * Returns the maximum power limit for DC charging
	 * @return The maximum power given as a PhysicalValueType
	 */
	public PhysicalValueType getMaximumPowerLimit();
	
	
	/**
	 * Returns the estimated or calculated time until full charge (100% SOC) is complete
	 * @return The estimated time given as a PhysicalValueType
	 */
	public PhysicalValueType getRemainingTimeToFullSOC();
	
	
	
	/**
	 * Returns the estimated or calculated time until bulk charge (approx. 80% SOC) is complete
	 * @return The estimated time given as a PhysicalValueType
	 */
	public PhysicalValueType getRemainingTimeToBulkSOC();
	
	
	/**
	 * Returns the DC_EVPowerDeliverParameter
	 * @return The DC_EVPowerDeliverParameter
	 */
	public DCEVPowerDeliveryParameterType getEVPowerDeliveryParameter();
}
