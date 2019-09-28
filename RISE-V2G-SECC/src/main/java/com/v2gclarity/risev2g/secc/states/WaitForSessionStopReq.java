/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
package com.v2gclarity.risev2g.secc.states;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionStopReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionStopResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForSessionStopReq extends ServerState {

	private SessionStopResType sessionStopRes;
	
	public WaitForSessionStopReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		sessionStopRes = new SessionStopResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, SessionStopReqType.class, sessionStopRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			SessionStopReqType sessionStopReq = 
					(SessionStopReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			getLogger().info("EV indicated to " + sessionStopReq.getChargingSession() + " the charging session");
			
			getCommSessionContext().setStopV2GCommunicationSession(true);
		} else {
			if (sessionStopRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new SessionStopResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, sessionStopRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(sessionStopRes, sessionStopRes.getResponseCode());
			}
		}
			
		return getSendMessage(sessionStopRes, V2GMessages.NONE, sessionStopRes.getResponseCode());
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return sessionStopRes;
	}

}
