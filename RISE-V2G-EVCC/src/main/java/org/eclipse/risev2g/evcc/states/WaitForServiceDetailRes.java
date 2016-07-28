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
package org.eclipse.risev2g.evcc.states;

import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDetailReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDetailResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForServiceDetailRes extends ClientState {

	public WaitForServiceDetailRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ServiceDetailResType.class)) {
			ServiceDetailResType serviceDetailRes = 
					(ServiceDetailResType) ((V2GMessage) message).getBody().getBodyElement().getValue();
			
			/*
			 * Currently there seems to be no need to check the service details, the parameterSets are clearly 
			 * defined in Table 105 of ISO/IEC 15118. If no negative response code was received for the 
			 * requested details of a serviceID, then the EVCC as well as SECC should offer the same parameterSets
			 */
			
			ServiceDetailReqType serviceDetailReq = getServiceDetailReq();
			if (serviceDetailReq != null)
				return getSendMessage(serviceDetailReq, V2GMessages.SERVICE_DETAIL_RES);
			else
				return getSendMessage(getPaymentServiceSelectionReq(), V2GMessages.PAYMENT_SERVICE_SELECTION_RES);
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
