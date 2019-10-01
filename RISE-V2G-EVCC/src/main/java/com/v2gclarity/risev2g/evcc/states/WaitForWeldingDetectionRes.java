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
package com.v2gclarity.risev2g.evcc.states;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingSessionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.WeldingDetectionResType;

public class WaitForWeldingDetectionRes extends ClientState {

	public WaitForWeldingDetectionRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, WeldingDetectionResType.class)) {
			WeldingDetectionResType weldingDetectionRes = 
					(WeldingDetectionResType) ((V2GMessage) message).getBody().getBodyElement().getValue();
			
			/*
			 * [V2G2-620] states that the EVCC shall send a WeldingDetectionReq again if the
			 * Welding Detection function has not finished on EV side. But how is this checked?
			 * 
			 * How to react on DCEVSEStatus values?
			 */
			
			if (getCommSessionContext().getChargingSession() != null && 
				getCommSessionContext().getChargingSession() == ChargingSessionType.PAUSE) {
				
				return getSendMessage(getSessionStopReq(ChargingSessionType.PAUSE), 
						  V2GMessages.SESSION_STOP_RES, "(ChargingSession = " + 
						  ChargingSessionType.PAUSE.toString() + ")");
			} else {	
				return getSendMessage(getSessionStopReq(ChargingSessionType.TERMINATE), 
						  V2GMessages.SESSION_STOP_RES, "(ChargingSession = " + 
						  ChargingSessionType.TERMINATE.toString() + ")");
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}

}
