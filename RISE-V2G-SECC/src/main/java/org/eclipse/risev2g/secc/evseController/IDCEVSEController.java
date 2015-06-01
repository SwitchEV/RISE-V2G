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
package org.eclipse.risev2g.secc.evseController;

import javax.xml.bind.JAXBElement;

import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;

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
