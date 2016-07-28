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

import javax.xml.bind.JAXBElement;

import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEChargeParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;

public interface IACEVSEController extends IEVSEController {

	/**
	 * Returns the charge parameter for AC charging 
	 * @return The EVSE specific charge parameter for the current charging session
	 */
	public JAXBElement<ACEVSEChargeParameterType> getACEVSEChargeParameter();
	
	
	/**
	 * Returns the EVSE status for AC charging comprising notification, maxDelay and RCD
	 * @param evseNotification An evse notification can optionally be set for testing purposes
	 * @return The EVSE specific status
	 */
	public ACEVSEStatusType getACEVSEStatus(EVSENotificationType evseNotification);
}
