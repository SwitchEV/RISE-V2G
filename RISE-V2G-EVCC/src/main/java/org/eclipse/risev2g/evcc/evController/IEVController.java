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
package org.eclipse.risev2g.evcc.evController;

import org.eclipse.risev2g.shared.enumerations.CPStates;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;

public interface IEVController {

	/**
	 * Returns the user-chosen payment method, either external identification means (EIM) such as an 
	 * RFID card or via Plug-and-Charge (PnC)
	 * @return The payment option Contract or ExternalPayment
	 */
	public PaymentOptionType getPaymentOption(PaymentOptionListType paymentOptionsOffered);
	
	
	/**
	 * Returns the EnergyTransferMode chosen by the driver
	 * @return The chosen EnergyTransferMode
	 */
	public EnergyTransferModeType getRequestedEnergyTransferMode();
	
	
	/**
	 * Returns the specific charging profile for the current charging session 
	 * (i.e. maximum amount of power drawn over time)
	 * @return The charging profile with a list of profile entries
	 */
	public ChargingProfileType getChargingProfile();
	
	
	/**
	 * Returns the unique identifier within a charging session for a SAScheduleTuple element 
	 * contained in the list of SASchedules delivered by the EVSE. An SAScheduleTupleID remains a 
	 * unique identifier for one schedule throughout a charging session.
	 * @return The unique ID given as a short value
	 */
	public short getChosenSAScheduleTupleID();
	
	
	/**
	 * Signals a CP state according to IEC 61851-1 (State A, B, C or D)
	 * @param state
	 * @return True, if the state signaling was successful, false otherwise
	 */
	public boolean setCPState(CPStates state);
	
	
	/**
	 * Returns the current CP state according IEC 61851-1 (State A, B, C or D)
	 * @return The respective CP state
	 */
	public CPStates getCPState();
}
