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
package org.eclipse.risev2g.secc.states;

import org.eclipse.risev2g.secc.evseController.IDCEVSEController;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PreChargeReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PreChargeResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForPreChargeReq extends ServerState {

	private PreChargeResType preChargeRes;
	
	public WaitForPreChargeReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		preChargeRes = new PreChargeResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PreChargeReqType.class, preChargeRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			PreChargeReqType preChargeReq = 
					(PreChargeReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			// TODO how to react to failure status of DCEVStatus of cableCheckReq?
			
			IDCEVSEController evseController = (IDCEVSEController) getCommSessionContext().getDCEvseController();
			
			evseController.setTargetCurrent(preChargeReq.getEVTargetCurrent());
			evseController.setTargetVoltage(preChargeReq.getEVTargetVoltage());

			preChargeRes.setDCEVSEStatus(evseController.getDCEVSEStatus(EVSENotificationType.NONE));
			preChargeRes.setEVSEPresentVoltage(evseController.getPresentVoltage());
			
			((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
			.getAllowedRequests().add(V2GMessages.PRE_CHARGE_REQ);
			((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
			.getAllowedRequests().add(V2GMessages.POWER_DELIVERY_REQ);
		} 
		
		return getSendMessage(preChargeRes, 
							  (preChargeRes.getResponseCode().toString().startsWith("OK") ? 
							  V2GMessages.FORK : V2GMessages.NONE)
						 	 );
	}
}
