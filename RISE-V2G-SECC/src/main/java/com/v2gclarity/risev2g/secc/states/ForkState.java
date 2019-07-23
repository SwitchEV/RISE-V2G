/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017  V2G Clarity (Dr.-Ing. Marc Mültin)
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
package com.v2gclarity.risev2g.secc.states;

import java.util.ArrayList;
import java.util.List;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ChangeProcessingState;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.misc.State;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class ForkState extends ServerState {

	private List<V2GMessages> allowedRequests;
	
	public ForkState(V2GCommunicationSessionSECC commSessionContext, 
			List<V2GMessages> allowedRequests) {
		super(commSessionContext);
		this.allowedRequests = allowedRequests;
	}
	
	public ForkState(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		this.allowedRequests = new ArrayList<V2GMessages>();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		V2GMessage v2gMessageReq = (V2GMessage) message;
		V2GMessages incomingMessage = null;
		
		try {
			incomingMessage = 
				V2GMessages.fromValue(v2gMessageReq.getBody().getBodyElement().getValue().getClass().getSimpleName());
		} catch (NullPointerException e) {
			return new TerminateSession("No valid V2GMessage received");
		}
		
		State newState = getCommSessionContext().getStates().get(incomingMessage);
		
		if (newState == null) {
			getLogger().error("Error occurred while switching from ForkState to a new state: new state is null");
			
			return new TerminateSession("Invalid message (" + v2gMessageReq.getBody().getBodyElement().getValue().getClass().getSimpleName() + 
					  ") at this state (" + this.getClass().getSimpleName() + "). " +
					  "Allowed messages are: " + this.getAllowedRequests().toString());
		}
		
		if (allowedRequests.contains(incomingMessage)) {
			// delete all allowedRequests so that they won't be valid anymore
			allowedRequests.clear();
			return new ChangeProcessingState(message, newState);
		} else {
			getLogger().error("Invalid message (" + v2gMessageReq.getBody().getBodyElement().getValue().getClass().getSimpleName() + 
	  						  ") at this state (" + this.getClass().getSimpleName() + "). " +
	  						  "Allowed messages are: " + this.getAllowedRequests().toString());
			
			BodyBaseType responseMessage = getSequenceErrorResMessage(v2gMessageReq);
			ServerState newServerState = (ServerState) newState;

			return newServerState.getSendMessage(responseMessage, V2GMessages.NONE, ResponseCodeType.FAILED_SEQUENCE_ERROR);
		}
	}

	public List<V2GMessages> getAllowedRequests() {
		return allowedRequests;
	}

	public void setAllowedRequests(List<V2GMessages> allowedRequests) {
		this.allowedRequests = allowedRequests;
	}
	
	@Override
	public String toString() {
		String allowedRequests = "";
		for (V2GMessages message : getAllowedRequests()) {
			allowedRequests += message.getClass().getSimpleName() + ", ";
		}
		
		return allowedRequests;
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return null;
	}

}
