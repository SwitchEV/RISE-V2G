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
package org.eclipse.risev2g.evcc.evController;

import javax.xml.bind.JAXBElement;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;

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
}
