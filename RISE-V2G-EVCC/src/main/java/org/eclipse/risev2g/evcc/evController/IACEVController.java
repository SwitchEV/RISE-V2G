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

import javax.xml.bind.JAXBElement;

import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;

public interface IACEVController extends IEVController {

	/**
	 * Returns the charge parameter for AC charging 
	 * @return The battery specific charge parameter for the current charging session
	 */
	public JAXBElement<ACEVChargeParameterType> getACEVChargeParamter();
	
	
	/**
	 * Indicates the maximum line current per phase the EV can draw (as allowed by the SECC)
	 * @param evseMaxCurrent
	 */
	public void adjustMaxCurrent(PhysicalValueType evseMaxCurrent);
}
