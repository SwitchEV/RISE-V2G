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
package com.v2gclarity.risev2g.secc.evseController;

import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeterInfoType;


public interface IEVSEController {

	/**
	 * The EVSEID is formatted according to Annex H of ISO/IEC 15118 and consists of minimum 7, max 37
	 * characters.
	 * 
	 * @return ID given as a string that uniquely identifies the EVSE and the power outlet the 
	 * 		   vehicle is connected to
	 */
	public String getEvseID();
	

	/**
	 * Closes the contactor if CP state C was measured (which is a prerequisite for power transfer)
	 * upon receipt of PowerDeliveryReq with ChargeProgress set to START. A timeout of 3s is allowed.
	 * @return True, if contactor is closed, false otherwise
	 */
	public boolean closeContactor();
	
	
	/**
	 * Opens the contactor if CP state B was measured upon receipt of PowerDeliveryReq with 
	 * ChargeProgress set to STOP. A timeout of 3s is allowed.
	 * @return True, if contactor is opened, false otherwise
	 */
	public boolean openContactor();
	
	
	/**
	 * Returns the MeterInfo record containing the latest meter reading and other meter relevant data.
	 * @return Meter reading and other meter data contained in MeterInfoType
	 */
	public MeterInfoType getMeterInfo();

}
