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

import org.eclipse.risev2g.evcc.evController.IDCEVController;
import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CableCheckResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PreChargeReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCableCheckRes extends ClientState {

	public WaitForCableCheckRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CableCheckResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			CableCheckResType cableCheckRes = 
					(CableCheckResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			if (cableCheckRes.getEVSEProcessing().equals(EVSEProcessingType.FINISHED)) {
				getLogger().debug("EVSEProcessing was set to FINISHED");
				
				IDCEVController dcEvController = (IDCEVController) getCommSessionContext().getEvController();
				
				PreChargeReqType preChargeReq = new PreChargeReqType();
				preChargeReq.setDCEVStatus(dcEvController.getDCEVStatus());
				preChargeReq.setEVTargetCurrent(dcEvController.getTargetCurrent());
				preChargeReq.setEVTargetVoltage(dcEvController.getTargetVoltage());
				
				return getSendMessage(preChargeReq, V2GMessages.PRE_CHARGE_RES);
			} else {
				getLogger().debug("EVSEProcessing was set to ONGOING");
				
				return getSendMessage(getCableCheckReq(), V2GMessages.CABLE_CHECK_RES);
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
