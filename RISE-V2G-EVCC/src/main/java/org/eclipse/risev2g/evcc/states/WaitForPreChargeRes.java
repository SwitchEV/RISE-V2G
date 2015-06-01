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
package org.eclipse.risev2g.evcc.states;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PreChargeResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForPreChargeRes extends ClientState {

	public WaitForPreChargeRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PreChargeResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			PreChargeResType preChargeRes = 
					(PreChargeResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			// TODO how to react to DC_EVSEStatus and EVSEPresentVoltage?
			
			return getSendMessage(getPowerDeliveryReq(ChargeProgressType.START), V2GMessages.POWER_DELIVERY_RES);
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
