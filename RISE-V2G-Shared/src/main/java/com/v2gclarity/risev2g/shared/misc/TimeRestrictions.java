/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright 2017 Dr.-Ing. Marc Mültin (V2G Clarity)
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
package com.v2gclarity.risev2g.shared.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;

public class TimeRestrictions {
	
	private static Logger logger = LogManager.getLogger(TimeRestrictions.class.getSimpleName());
	public static final int V2G_EVCC_SEQUENCE_PERFORMANCE_TIME = 40000;
	public static final int V2G_SECC_SEQUENCE_TIMEOUT = 60000;
	public static final int V2G_EVCC_ONGOING_TIMEOUT = 60000;
	public static final int V2G_EVCC_CABLE_CHECK_TIMEOUT = 40000;
	public static final int V2G_EVCC_PRE_CHARGE_TIMEOUT = 7000;
	
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
	
	
	public static int getV2gEvccMsgTimeout(V2GMessages messageType) {
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
