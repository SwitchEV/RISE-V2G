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
package org.eclipse.risev2g.shared.enumerations;


public enum V2GMessages {
	 /*
	  * FORK does not represent a V2GMessage, but it is needed for the HashMap of
	  * V2GCommunicationSessionSECC/-EVCC which contains all states related to messages
	  */
	FORK("Fork"),
	/*
	 * NONE does not represent a V2GMessage, but it is needed for signaling the termination of 
	 * the state machine in the V2GCommunicationSessionSECC part (when calling getSendMessage() of State)
	 */
	NONE("None"),
	SECC_DISCOVERY_REQ("SECCDiscoveryReq"),
	SECC_DISCOVERY_RES("SECCDiscoveryRes"),
	SUPPORTED_APP_PROTOCOL_REQ("SupportedAppProtocolReq"),
	SUPPORTED_APP_PROTOCOL_RES("SupportedAppProtocolRes"),
	SESSION_SETUP_REQ("SessionSetupReqType"),
	SESSION_SETUP_RES("SessionSetupResType"),
	SERVICE_DISCOVERY_REQ("ServiceDiscoveryReqType"),
	SERVICE_DISCOVERY_RES("ServiceDiscoveryResType"),
	SERVICE_DETAIL_REQ("ServiceDetailReqType"),
	SERVICE_DETAIL_RES("ServiceDetailResType"),
	PAYMENT_SERVICE_SELECTION_REQ("PaymentServiceSelectionReqType"),
	PAYMENT_SERVICE_SELECTION_RES("PaymentServiceSelectionResType"),
	PAYMENT_DETAILS_REQ("PaymentDetailsReqType"),
	PAYMENT_DETAILS_RES("PaymentDetailsResType"),
	AUTHORIZATION_REQ("AuthorizationReqType"),
	AUTHORIZATION_RES("AuthorizationResType"),
	CHARGE_PARAMETER_DISCOVERY_REQ("ChargeParameterDiscoveryReqType"),
	CHARGE_PARAMETER_DISCOVERY_RES("ChargeParameterDiscoveryResType"),
	CHARGING_STATUS_REQ("ChargingStatusReqType"),
	CHARGING_STATUS_RES("ChargingStatusResType"),
	METERING_RECEIPT_REQ("MeteringReceiptReqType"),
	METERING_RECEIPT_RES("MeteringReceiptResType"),
	POWER_DELIVERY_REQ("PowerDeliveryReqType"),
	POWER_DELIVERY_RES("PowerDeliveryResType"),
	CABLE_CHECK_REQ("CableCheckReqType"),
	CABLE_CHECK_RES("CableCheckResType"),
	PRE_CHARGE_REQ("PreChargeReqType"),
	PRE_CHARGE_RES("PreChargeResType"),
	CURRENT_DEMAND_REQ("CurrentDemandReqType"),
	CURRENT_DEMAND_RES("CurrentDemandResType"),
	WELDING_DETECTION_REQ("WeldingDetectionReqType"),
	WELDING_DETECTION_RES("WeldingDetectionResType"),
	SESSION_STOP_REQ("SessionStopReqType"), 
	SESSION_STOP_RES("SessionStopResType"),
	CERTIFICATE_INSTALLATION_REQ("CertificateInstallationReqType"),
	CERTIFICATE_INSTALLATION_RES("CertificateInstallationResType"),
	CERTIFICATE_UPDATE_REQ("CertificateUpdateReqType"),
	CERTIFICATE_UPDATE_RES("CertificateUpdateResType");
    
	/*
	 * the String value must be the same as the class names in package shared.v2gMessages.msgbody and 
	 * shared.v2gMessages.appprotocol
	 */
	private final String value;

    V2GMessages(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static V2GMessages fromValue(String v) {
        for (V2GMessages v2gMessage: V2GMessages.values()) {
            if (v2gMessage.value.equals(v)) {
                return v2gMessage;
            }
        }
        throw new IllegalArgumentException(v);
    }
    

}
