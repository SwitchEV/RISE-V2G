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
package org.eclipse.risev2g.secc.evseController;

import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeterInfoType;


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
