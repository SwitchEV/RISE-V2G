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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ChangeProcessingState;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.misc.State;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
		V2GMessages incomingMessage = 
				V2GMessages.fromValue(v2gMessageReq.getBody().getBodyElement().getValue().getClass().getSimpleName());
		
		if (allowedRequests.contains(incomingMessage)) {
			State newState = getCommSessionContext().getStates().get(incomingMessage);
			
			if (newState == null) {
				getLogger().error("Error occurred while switching from ForkState to a new state: new state is null");
			}
			
			// delete all allowedRequests so that they won't be valid anymore
			allowedRequests.clear();
			
			return new ChangeProcessingState(message, newState);
		} else {
			return new TerminateSession("Invalid message (" + v2gMessageReq.getBody().getBodyElement().getValue().getClass().getSimpleName() + 
			  		  					") at this state (" + this.getClass().getSimpleName() + "). " +
										"Allowed messages are: " + this.getAllowedRequests().toString());
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

}
