/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-207  V2G Clarity (Dr.-Ing. Marc Mültin) 
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
package org.v2gclarity.risev2g.secc.states;

import org.v2gclarity.risev2g.secc.evseController.IDCEVSEController;
import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PreChargeReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PreChargeResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
		} else {
			if (preChargeRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new PreChargeResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, preChargeRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(preChargeRes, preChargeRes.getResponseCode());
			}
		}
		
		return getSendMessage(preChargeRes, 
							  (preChargeRes.getResponseCode().toString().startsWith("OK") ? 
							  V2GMessages.FORK : V2GMessages.NONE),
							  preChargeRes.getResponseCode()
						 	 );
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return preChargeRes;
	}
}
