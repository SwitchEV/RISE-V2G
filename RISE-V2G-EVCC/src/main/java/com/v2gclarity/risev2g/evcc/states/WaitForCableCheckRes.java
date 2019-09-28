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

import java.util.concurrent.TimeUnit;

import com.v2gclarity.risev2g.evcc.evController.IDCEVController;
import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CableCheckResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PreChargeReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForCableCheckRes extends ClientState {

	public WaitForCableCheckRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, CableCheckResType.class)) {
			V2GMessage v2gMessageRes = (V2GMessage) message;
			CableCheckResType cableCheckRes = 
					(CableCheckResType) v2gMessageRes.getBody().getBodyElement().getValue();
			
			if (cableCheckRes.getEVSEProcessing().equals(EVSEProcessingType.FINISHED)) {
				getLogger().debug("EVSEProcessing was set to FINISHED");
				
				IDCEVController dcEvController = (IDCEVController) getCommSessionContext().getEvController();
				
				PreChargeReqType preChargeReq = new PreChargeReqType();
				preChargeReq.setDCEVStatus(dcEvController.getDCEVStatus());
				preChargeReq.setEVTargetCurrent(dcEvController.getTargetCurrent());
				preChargeReq.setEVTargetVoltage(dcEvController.getTargetVoltage());
				
				getCommSessionContext().setOngoingTimer(System.nanoTime());
				getCommSessionContext().setOngoingTimerActive(true);
				
				return getSendMessage(preChargeReq, V2GMessages.PRE_CHARGE_RES);
			} else {
				getLogger().debug("EVSEProcessing was set to ONGOING");
				
				long elapsedTimeInMs = 0;
				
				if (getCommSessionContext().isOngoingTimerActive()) {
					long elapsedTime = System.nanoTime() - getCommSessionContext().getOngoingTimer();
					elapsedTimeInMs = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
					
					if (elapsedTimeInMs > TimeRestrictions.V2G_EVCC_CABLE_CHECK_TIMEOUT) 
						return new TerminateSession("CableCheck timer timed out for CableCheckReq");
				} else {
					getCommSessionContext().setOngoingTimer(System.nanoTime());
					getCommSessionContext().setOngoingTimerActive(true);
				}
				
				return getSendMessage(getCableCheckReq(), V2GMessages.CABLE_CHECK_RES, Math.min((TimeRestrictions.V2G_EVCC_CABLE_CHECK_TIMEOUT - (int) elapsedTimeInMs), TimeRestrictions.getV2gEvccMsgTimeout(V2GMessages.CABLE_CHECK_RES)));
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
}
