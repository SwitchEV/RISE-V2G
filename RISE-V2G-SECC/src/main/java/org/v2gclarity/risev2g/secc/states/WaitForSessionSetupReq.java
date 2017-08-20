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

import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionSetupReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;

public class WaitForSessionSetupReq extends ServerState {
	
	private SessionSetupResType sessionSetupRes; 
	
	public WaitForSessionSetupReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		sessionSetupRes = new SessionSetupResType();
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, SessionSetupReqType.class, sessionSetupRes)) {
			sessionSetupRes.setEVSEID(getCommSessionContext().getEvseController().getEvseID());
			
			// Unix time stamp is needed (seconds instead of milliseconds)
			sessionSetupRes.setEVSETimeStamp(System.currentTimeMillis() / 1000L);
		} else {
			if (sessionSetupRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new SessionSetupResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, sessionSetupRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(sessionSetupRes, sessionSetupRes.getResponseCode());
			}
		} 
			
		return getSendMessage(sessionSetupRes, 
				  			  (sessionSetupRes.getResponseCode().toString().startsWith("OK") ? 
				  			  V2GMessages.SERVICE_DISCOVERY_REQ : V2GMessages.NONE),
				  			  sessionSetupRes.getResponseCode()
				 			 );
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return sessionSetupRes;
	}

}
