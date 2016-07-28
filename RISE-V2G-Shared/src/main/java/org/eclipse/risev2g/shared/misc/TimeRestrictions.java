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
package org.eclipse.risev2g.shared.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;

/**
 * All time restrictions are given as millisecond values. 
 * 
 * @author Marc
 *
 */
public class TimeRestrictions {
	
	private static Logger logger = LogManager.getLogger(TimeRestrictions.class.getSimpleName());
	public static final int V2G_EVCC_SEQUENCE_PERFORMANCE_TIME = 40000;
	public static final int V2G_SECC_SEQUENCE_TIMEOUT = 60000;
	
	/**
	 * SDP client shall wait for SECC Discovery Response message for _at least_ 250 ms (see [V2G2-159])
	 */
	public static final int SDP_RESPONSE_TIMEOUT = 250; 
	
	/**
	 * A maximum of 50 consecutive SECC Discovery Request messages is allowed (see [V2G-161])
	 */
	public static final int SDP_REQUEST_MAX_COUNTER = 50;
	
	/**
	 * Timeout for the communication setup timer, includes time span ...
	 * - from:  plug present (state B transition)
	 * - until: SessionSetupRes 
	 */
	public static final int V2G_EVCC_COMMUNICATION_SETUP_TIMEOUT = 20000;
	
	/**
	 * Timeout for retrieving a response from the ProtoTCPClient after having sent a request
	 */
	public static final int PROTO_TCP_CLIENT_RESPONSE_TIMEOUT = 30000;
	
	/**
	 * Threshold time in seconds for sending the EV controller to sleep
	 */
	public static final int STAY_AWAKE_THRESHOLD = 125;
	
	public static int getV2G_EVCC_Msg_Timeout(V2GMessages messageType) {
		switch(messageType) {
			case SUPPORTED_APP_PROTOCOL_RES : return 2000;
			case SESSION_SETUP_RES: return 2000;
			case SERVICE_DISCOVERY_RES: return 2000;
			case SERVICE_DETAIL_RES: return 5000;
			case PAYMENT_SERVICE_SELECTION_RES: return 2000;
			case PAYMENT_DETAILS_RES: return 5000; 
			case AUTHORIZATION_RES: return 2000; 
			case CHARGE_PARAMETER_DISCOVERY_RES: return 2000;
			case CHARGING_STATUS_RES: return 2000;
			case METERING_RECEIPT_RES: return 2000;
			case POWER_DELIVERY_RES: return 5000;
			case CABLE_CHECK_RES: return 2000;
			case PRE_CHARGE_RES: return 2000;
			case CURRENT_DEMAND_RES: return 250;
			case WELDING_DETECTION_RES: return 2000;
			case SESSION_STOP_RES: return 2000;
			case CERTIFICATE_INSTALLATION_RES: return 5000;
			case CERTIFICATE_UPDATE_RES: return 5000;
			default: {
				logger.error("MessageType '" + messageType + "' does not have a timeout value assigend. Timeout value 0 returned");
				return 0;
			}
		}
	}
}
