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

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.CPStates;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;

public interface IEVController {

	/**
	 * Provides a reference to the current communication session for
	 * this controller instance.
	 * @param commSessionContext The active communication session
	 */
	public void setCommSessionContext(V2GCommunicationSessionEVCC commSessionContext);
	
	/**
	 * Returns the user-chosen payment method, either external identification means (EIM) such as an 
	 * RFID card or via Plug-and-Charge (PnC)
	 * @return The payment option Contract or ExternalPayment
	 */
	public PaymentOptionType getPaymentOption();
	
	
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
	
	
	/**
	 * Provides information on whether the charging loop should be active to charge the EV's battery, or not
	 * 
	 * @return True, if charging process should be continued, false otherwise
	 */
	public boolean isChargingLoopActive();
}
