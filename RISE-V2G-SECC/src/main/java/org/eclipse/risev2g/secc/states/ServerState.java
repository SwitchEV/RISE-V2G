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
import org.eclipse.risev2g.shared.messageHandling.SendMessage;
import org.eclipse.risev2g.shared.misc.State;
import org.eclipse.risev2g.shared.misc.TimeRestrictions;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CableCheckResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateUpdateResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ChargingStatusResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CurrentDemandResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeteringReceiptResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PreChargeResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDetailResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SessionStopResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.WeldingDetectionResType;

public abstract class ServerState extends State {

	private ResponseCodeType responseCode;
	
	public ServerState(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
	}

	public V2GCommunicationSessionSECC getCommSessionContext() {
		return (V2GCommunicationSessionSECC) super.getCommSessionContext();
	}
	
	
	protected boolean isIncomingMessageValid(
			Object incomingMessage, 
			Class<? extends BodyBaseType> expectedMessage,
			BodyBaseType responseMessage) {
		V2GMessage v2gMessage = null;
		ResponseCodeType responseCode = null;
		
		// Check if incoming request is a V2GMessage
		if (incomingMessage instanceof V2GMessage) {
			v2gMessage = (V2GMessage) incomingMessage;
			
			// Check if incoming request is expected
			if (expectedMessage.isAssignableFrom(v2gMessage.getBody().getBodyElement().getValue().getClass())) {
				getLogger().debug(v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName().replace("Type", "") + " received");
				
				// Check for correct session ID
				responseCode = getCommSessionContext().checkSessionID(v2gMessage.getHeader());
			} else {
				getLogger().fatal("Invalid message (" + v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName() + 
						  		  ") at this state (" + this.getClass().getSimpleName() + ")");
				responseCode = ResponseCodeType.FAILED_SEQUENCE_ERROR;
			}
		} else {
			getLogger().fatal("Incoming message is not a V2GMessage");
			responseCode = ResponseCodeType.FAILED_SEQUENCE_ERROR;
		}
		
		switch (responseMessage.getClass().getSimpleName()) {
		case "SessionSetupResType":
			((SessionSetupResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ServiceDiscoveryResType":
			((ServiceDiscoveryResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ServiceDetailResType":
			((ServiceDetailResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PaymentServiceSelectionResType":
			((PaymentServiceSelectionResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PaymentDetailsResType":
			((PaymentDetailsResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CertificateInstallationResType":
			((CertificateInstallationResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CertificateUpdateResType":
			((CertificateUpdateResType) responseMessage).setResponseCode(responseCode);
			break;
		case "AuthorizationResType":
			((AuthorizationResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ChargeParameterDiscoveryResType":
			((ChargeParameterDiscoveryResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CableCheckResType": 
			((CableCheckResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PreChargeResType": 
			((PreChargeResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PowerDeliveryResType":
			((PowerDeliveryResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ChargingStatusResType":
			((ChargingStatusResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CurrentDemandResType":
			((CurrentDemandResType) responseMessage).setResponseCode(responseCode);
			break;
		case "MeteringReceiptResType":
			((MeteringReceiptResType) responseMessage).setResponseCode(responseCode);
			break;
		case "WeldingDetectionResType":
			((WeldingDetectionResType) responseMessage).setResponseCode(responseCode);
			break;
		case "SessionStopResType":
			((SessionStopResType) responseMessage).setResponseCode(responseCode);
			break;
		default:
			getLogger().error("Response message could not be identified");
			return false;
		}
		
		if (responseCode.toString().startsWith("OK")) return true;
		else {
			getLogger().error("Response code '" + responseCode.toString() + "' will be sent");
			return false;
		}
	}
	
	
	protected SendMessage getSendMessage(
			SupportedAppProtocolRes message, 
			V2GMessages nextExpectedMessage) {
		String messageName = message.getClass().getSimpleName();
		
		getLogger().debug("Preparing to send " + messageName);
		return new SendMessage(message, getCommSessionContext().getStates().get(nextExpectedMessage), TimeRestrictions.V2G_SECC_SEQUENCE_TIMEOUT);
	}
	
	
	public ResponseCodeType getResponseCode() {
		return responseCode;
	}

	
	/**
	 * Provides additional information about the kind of response code 
	 * @param responseCode The response code to be sent
	 * @return True if the response code is positive (i.e. containing or starting with "OK"), false otherwise
	 */
	public boolean setResponseCode(ResponseCodeType responseCode) {
		// Only log a negative response code
		if (!responseCode.value().substring(0, 2).toUpperCase().equals("OK")) {
			getLogger().error("Response code '" + responseCode.value() + "' will be sent.");
			getCommSessionContext().setStopV2GCommunicationSession(true);
			return false;
		}
		
		this.responseCode = responseCode;
		return true;
	}
	
	
	/**
	 * In case a FAILED response code is sent, the mandatory fields still need to be set with minimum required values,
	 * otherwise the EVCC's EXI decoder will raise an error.
	 * 
	 * @param response The respective response message whose mandatory fields are to be set
	 */
	protected abstract void setMandatoryFieldsForFailedRes();

}
