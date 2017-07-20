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

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionStopReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionStopResType;

public class WaitForSessionStopReq extends ServerState {

	private SessionStopResType sessionStopRes;
	
	public WaitForSessionStopReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		sessionStopRes = new SessionStopResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, SessionStopReqType.class, sessionStopRes)) {
			getCommSessionContext().setStopV2GCommunicationSession(true);
		} else {
			setMandatoryFieldsForFailedRes();
		}
			
		return getSendMessage(sessionStopRes, V2GMessages.NONE);
	}
	

	@Override
	protected void setMandatoryFieldsForFailedRes() {
		// No other fields need to be set besides response code
	}

}
