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
package org.eclipse.risev2g.secc.states;

import org.eclipse.risev2g.secc.evseController.IDCEVSEController;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.WeldingDetectionReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.WeldingDetectionResType;

public class WaitForWeldingDetectionReq extends ServerState {

	private WeldingDetectionResType weldingDetectionRes;
	
	public WaitForWeldingDetectionReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		weldingDetectionRes = new WeldingDetectionResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, WeldingDetectionReqType.class, weldingDetectionRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			WeldingDetectionReqType weldingDetectionReq = 
					(WeldingDetectionReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			// TODO how to react to failure status of DCEVStatus of weldingDetectionReq?
			
			IDCEVSEController evseController = (IDCEVSEController) getCommSessionContext().getDCEvseController();
			
			weldingDetectionRes.setDCEVSEStatus(evseController.getDCEVSEStatus(EVSENotificationType.NONE));
			weldingDetectionRes.setEVSEPresentVoltage(evseController.getPresentVoltage());
			
			((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
			.getAllowedRequests().add(V2GMessages.WELDING_DETECTION_REQ);
			((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
			.getAllowedRequests().add(V2GMessages.SESSION_STOP_REQ);
		} 
		
		return getSendMessage(weldingDetectionRes, 
				 			  (weldingDetectionRes.getResponseCode().toString().startsWith("OK") ? 
				 			  V2GMessages.FORK : V2GMessages.NONE)
			 			 	 );
	}
}
