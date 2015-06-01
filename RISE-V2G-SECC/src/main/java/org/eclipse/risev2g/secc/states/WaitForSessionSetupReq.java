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

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionSetupReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;

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
		} 
			
		return getSendMessage(sessionSetupRes, 
				  			  (sessionSetupRes.getResponseCode().toString().startsWith("OK") ? 
				  			  V2GMessages.SERVICE_DISCOVERY_REQ : V2GMessages.NONE)
				 			 );
	}

}
