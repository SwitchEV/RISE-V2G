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
			getCommSessionContext().setSelectedPaymentOption(paymentServiceSelectionReq.getSelectedPaymentOption());
			
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
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
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

	
	@Override
	protected void setMandatoryFieldsForFailedRes() {
		// No other mandatory fields to be set besides response code
	}

}
