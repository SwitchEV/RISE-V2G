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
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SelectedServiceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForPaymentServiceSelectionReq extends ServerState {
	
	private PaymentServiceSelectionResType paymentServiceSelectionRes;
	
	public WaitForPaymentServiceSelectionReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		paymentServiceSelectionRes = new PaymentServiceSelectionResType();
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, PaymentServiceSelectionReqType.class, paymentServiceSelectionRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			PaymentServiceSelectionReqType paymentServiceSelectionReq = 
					(PaymentServiceSelectionReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			getLogger().info("Payment option " + paymentServiceSelectionReq.getSelectedPaymentOption().toString() + 
							 " has been chosen by EVCC");
			
			if (isResponseCodeOK(paymentServiceSelectionReq)) {
				// see [V2G2-551]
				if (paymentServiceSelectionReq.getSelectedPaymentOption().equals(PaymentOptionType.CONTRACT)) {
					((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
						.getAllowedRequests().add(V2GMessages.PAYMENT_DETAILS_REQ);
					((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
						.getAllowedRequests().add(V2GMessages.CERTIFICATE_INSTALLATION_REQ);
					((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
						.getAllowedRequests().add(V2GMessages.CERTIFICATE_UPDATE_REQ);
					
					return getSendMessage(paymentServiceSelectionRes, V2GMessages.FORK);
				} else {
					return getSendMessage(paymentServiceSelectionRes, V2GMessages.AUTHORIZATION_REQ);
				}
			} else {
				getLogger().error("Response code '" + paymentServiceSelectionRes.getResponseCode() + "' will be sent");
			}
		} 

		return getSendMessage(paymentServiceSelectionRes, V2GMessages.NONE);
	}
	
	
	public boolean isResponseCodeOK(PaymentServiceSelectionReqType paymentServiceSelectionReq) {
		// Check if the charge service was selected and if all selected services were offered before
		boolean chargeServiceSelected = false;
		boolean selectedServiceOffered;
		
		for (SelectedServiceType selectedService : paymentServiceSelectionReq.getSelectedServiceList().getSelectedService()) {
			selectedServiceOffered = false;
			
			for (ServiceType offeredService : getCommSessionContext().getOfferedServices()) {
				if (offeredService.getServiceID() == selectedService.getServiceID()) {
					selectedServiceOffered = true;
					break;
					// TODO check for parameterSetID as well
				}
			}
			
			if (!selectedServiceOffered) {
				getLogger().error("Selected service with ID " + selectedService.getServiceID() + 
								  " is not offered");
				paymentServiceSelectionRes.setResponseCode(ResponseCodeType.FAILED_SERVICE_SELECTION_INVALID);
				return false;
			}
			
			if (selectedService.getServiceID() == 1) {
				chargeServiceSelected = true;
				break;
			}
		}
		
		if (!chargeServiceSelected) {
			paymentServiceSelectionRes.setResponseCode(ResponseCodeType.FAILED_NO_CHARGE_SERVICE_SELECTED);
			return false;
		}
		
		
		// Check if selected payment option is supported
		if (!getCommSessionContext().getPaymentOptions().getPaymentOption()
				.contains(paymentServiceSelectionReq.getSelectedPaymentOption())) {
			paymentServiceSelectionRes.setResponseCode(ResponseCodeType.FAILED_PAYMENT_SELECTION_INVALID);
			return false;
		}
		
		return true;
	}

}
