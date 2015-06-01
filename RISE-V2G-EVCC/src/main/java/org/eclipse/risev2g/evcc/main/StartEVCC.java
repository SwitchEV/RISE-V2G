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
package org.eclipse.risev2g.evcc.main;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionHandlerEVCC;
import org.eclipse.risev2g.evcc.transportLayer.UDPClient;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.utils.MiscUtils;

public class StartEVCC {

	public static void main(String[] args) {
		MiscUtils.setV2gEntityConfig(GlobalValues.EVCC_CONFIG_PROPERTIES_PATH.toString());
		
		UDPClient udpClient = UDPClient.getInstance();
		
		if (udpClient.initialize()) {
			V2GCommunicationSessionHandlerEVCC sessionHandler = new V2GCommunicationSessionHandlerEVCC();
		}
	}

}
