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
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CableCheckReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CableCheckResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCableCheckReq extends ServerState {

	private CableCheckResType cableCheckRes;
	private boolean evseProcessingFinished;
	
	public WaitForCableCheckReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		cableCheckRes = new CableCheckResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CableCheckReqType.class, cableCheckRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			CableCheckReqType cableCheckReq = 
					(CableCheckReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			// TODO how to react to failure status of DCEVStatus of cableCheckReq?
			
			/*
			 * TODO we need a timeout mechanism here so that a response can be sent within 2s
			 * the DCEVSEStatus should be generated according to already available values
			 * (if EVSEProcessing == ONGOING, maybe because of EVSE_IsolationMonitoringActive,
			 * within a certain timeout, then the status must be different)
			 */
			setEvseProcessingFinished(true);
			
			if (isEvseProcessingFinished()) {
				cableCheckRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
				cableCheckRes.setDCEVSEStatus(
						((IDCEVSEController) getCommSessionContext().getDCEvseController()).getDCEVSEStatus(EVSENotificationType.NONE)
						);
				return getSendMessage(cableCheckRes, V2GMessages.PRE_CHARGE_REQ);
			} else {
				cableCheckRes.setEVSEProcessing(EVSEProcessingType.ONGOING);
				return getSendMessage(cableCheckRes, V2GMessages.CABLE_CHECK_REQ);
			}
		} else {
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(cableCheckRes, V2GMessages.NONE);
	}
	

	public boolean isEvseProcessingFinished() {
		return evseProcessingFinished;
	}

	public void setEvseProcessingFinished(boolean evseProcessingFinished) {
		this.evseProcessingFinished = evseProcessingFinished;
	}

	
	@Override
	protected void setMandatoryFieldsForFailedRes() {
		cableCheckRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
		cableCheckRes.setDCEVSEStatus(
				((IDCEVSEController) getCommSessionContext().getDCEvseController()).getDCEVSEStatus(EVSENotificationType.NONE)
				);
	}

}
