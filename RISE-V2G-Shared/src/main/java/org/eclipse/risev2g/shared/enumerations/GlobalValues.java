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
package org.eclipse.risev2g.shared.enumerations;

import java.util.Arrays;

import org.eclipse.risev2g.shared.utils.ByteUtils;


public enum GlobalValues {
	// Relative file paths to the config files for EV and EVSE
	EVCC_CONFIG_PROPERTIES_PATH("./EVCCConfig.properties"),
	SECC_CONFIG_PROPERTIES_PATH("./SECCConfig.properties"),
	
	/*
	 * TODO THIS IS NOT A SECURE WAY OF STORING A PASSWORD FOR THE CERTIFICATES' KEYS
	 * However, for testing purposes it is OK to use one passphrase for every key, certificate and
	 * keystore. This issue needs to be solved for productive use of this code.
	 */
	PASSPHRASE_FOR_CERTIFICATES_AND_KEYS("123456"),
	
	// Aliases for contract certificate chain and OEM provisioning certificate chain
	ALIAS_CONTRACT_CERTIFICATE("contract_cert"),
	ALIAS_OEM_PROV_CERTIFICATE("oem_prov_cert"),
	
	// Period of time in days in which contract certificate update is recommended 
	CERTIFICATE_EXPIRES_SOON_PERIOD((short) 21, GlobalTypes.SECURITY),
	
	/*
	 * Relative file path to the EVCC and SECC keystore and truststore. 
	 * Since at least the evccKeystore needs to be modified upon certificate installation / update, the 
	 * keystores must not be kept inside the JAR (which is read-only). For easier handling, we put all 
	 * keystores at the same level as the JAR.
	 */
	EVCC_KEYSTORE_FILEPATH("./evccKeystore.jks"),
	EVCC_TRUSTSTORE_FILEPATH("./evccTruststore.jks"),
	SECC_KEYSTORE_FILEPATH("./seccKeystore.jks"),
	SECC_TRUSTSTORE_FILEPATH("./seccTruststore.jks"),
	
	// SECC Discovery Protocol (SDP) multicast address (see [V2G2-139]) and port (see table 15, page 36) 
	SDP_MULTICAST_ADDRESS("FF02::1"),
	V2G_UDP_SDP_SERVER_PORT((short) 15118, GlobalTypes.PORT),
	
	/*
	 * Maximum payload length allowed in bytes (0 ... 4294967295), see Table 9
	 * With Integer being a signed type in Java, we need to multiply the maximum value of Integer by 2
	 */
	V2GTP_HEADER_MAX_PAYLOAD_LENGTH((long) Integer.MAX_VALUE * 2, GlobalTypes.PAYLOAD_LENGTH),
	
	// Protocol versions (1 = IS compliant), see Table 9
	V2GTP_VERSION_1_IS(ByteUtils.toByteFromHexString("01"), GlobalTypes.PROTOCOL_VERSION),
	
	// Schema information
	V2G_CI_MSG_DEF_NAMESPACE("urn:iso:15118:2:2013:MsgDef"),
	V2G_CI_MSG_BODY_NAMESPACE("urn:iso:15118:2:2013:MsgBody"), 
	V2G_CI_MSG_DATATYPES_NAMESPACE("urn:iso:15118:2:2013:MsgDataTypes"), 
	V2G_CI_XMLDSIG_NAMESPACE("http://www.w3.org/2000/09/xmldsig#"), 
	SCHEMA_PATH_APP_PROTOCOL("/schemas/V2G_CI_AppProtocol.xsd"),
	SCHEMA_PATH_MSG_DEF("/schemas/V2G_CI_MsgDef.xsd"),
	SCHEMA_PATH_XMLDSIG("/schemas/xmldsig-core-schema.xsd"),
	SCHEMA_PATH_MSG_BODY("/schemas/V2G_CI_MsgBody.xsd"),
	SCHEMA_PATH_MSG_HEADER("/schemas/V2G_CI_MsgHeader.xsd"),
	SCHEMA_PATH_MSG_DATA_TYPES("/schemas/V2G_CI_MsgDataTypes.xsd"),
	
	// Encoding for the requested security option (see [V2G2-623])
	V2G_SECURITY_WITH_TLS(ByteUtils.toByteFromHexString("00"), GlobalTypes.SECURITY),
	V2G_SECURITY_WITHOUT_TLS(ByteUtils.toByteFromHexString("10"), GlobalTypes.SECURITY),
	
	// Encoding for requested transport protocol (see [V2G2-623])
	V2G_TRANSPORT_PROTOCOL_TCP(ByteUtils.toByteFromHexString("00"), GlobalTypes.TRANSPORT_PROTOCOL),
	V2G_TRANSPORT_PROTOCOL_UDP(ByteUtils.toByteFromHexString("10"), GlobalTypes.TRANSPORT_PROTOCOL),
	
	// Payload types
	V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE(ByteUtils.toByteArrayFromHexString("8001"), GlobalTypes.PAYLOAD_TYPE),
	V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE(ByteUtils.toByteArrayFromHexString("9000"), GlobalTypes.PAYLOAD_TYPE),
	V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE(ByteUtils.toByteArrayFromHexString("9001"), GlobalTypes.PAYLOAD_TYPE);
		
	private GlobalTypes type;
	private byte byteValue;
	private byte[] byteArrayValue;
	private short shortValue;
	private long longValue;
	private String stringValue;
	
	private GlobalValues(byte byteValue, GlobalTypes type) {
		this.byteValue = byteValue;
		this.type = type;
	}
	
	private GlobalValues(byte[] byteArrayValue, GlobalTypes type) {
		this.byteArrayValue = byteArrayValue;
		this.type = type;
	}
	
	private GlobalValues(short shortValue, GlobalTypes type) {
		this.shortValue = shortValue;
		this.type = type;
	}
	
	private GlobalValues(long longValue, GlobalTypes type) {
		this.longValue = longValue;
		this.type = type;
	}
	
	private GlobalValues(String stringValue) {
		this.stringValue = stringValue;
	}
	
	public byte getByteValue() {
		return byteValue;
	}	
	
	public byte[] getByteArrayValue() {
		return byteArrayValue;
	}
	
	public long getLongValue() {
		return longValue;
	}
	
	public short getShortValue() {
		return shortValue;
	}
	
	public String toString() {
		switch (this) {
		case EVCC_CONFIG_PROPERTIES_PATH:
			 return stringValue;
		case SECC_CONFIG_PROPERTIES_PATH:
			 return stringValue;
		case PASSPHRASE_FOR_CERTIFICATES_AND_KEYS:
			 return stringValue;
		case SDP_MULTICAST_ADDRESS:
			 return stringValue;
		case V2G_UDP_SDP_SERVER_PORT:
			 return stringValue;
		case V2GTP_HEADER_MAX_PAYLOAD_LENGTH:
			 return "4294967295 bytes";
		case V2GTP_VERSION_1_IS:
			 return "version 1 (IS compliant)";
		case V2G_CI_MSG_DEF_NAMESPACE:
			 return stringValue;
		case V2G_CI_MSG_BODY_NAMESPACE:
			 return stringValue;
		case V2G_CI_MSG_DATATYPES_NAMESPACE:
			 return stringValue;
		case V2G_CI_XMLDSIG_NAMESPACE:
			 return stringValue;
		case SCHEMA_PATH_APP_PROTOCOL:
			 return stringValue;
		case SCHEMA_PATH_MSG_DEF:
			 return stringValue;
		case SCHEMA_PATH_XMLDSIG:
			 return stringValue;
		case SCHEMA_PATH_MSG_BODY:
			 return stringValue;
		case SCHEMA_PATH_MSG_HEADER:
			 return stringValue;
		case SCHEMA_PATH_MSG_DATA_TYPES:
			 return stringValue;
		case V2G_SECURITY_WITH_TLS:
			 return "TLS enabled";
		case V2G_SECURITY_WITHOUT_TLS:
			 return "TLS disabled";
		case V2G_TRANSPORT_PROTOCOL_TCP:
			 return "transport protocol TCP";
		case V2G_TRANSPORT_PROTOCOL_UDP:
			 return "transport protocol UDP";
		case V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE:
			 return "EXI encoded Message";
		case V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE:
			 return "SDP request message";
		case V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE:
			 return "SDP response message";
		case ALIAS_CONTRACT_CERTIFICATE:
			return stringValue;
		case ALIAS_OEM_PROV_CERTIFICATE:
			return stringValue;
		case EVCC_KEYSTORE_FILEPATH:
			return stringValue;
		case EVCC_TRUSTSTORE_FILEPATH:
			return stringValue;
		case SECC_KEYSTORE_FILEPATH:
			return stringValue;
		case SECC_TRUSTSTORE_FILEPATH:
			return stringValue;
		case CERTIFICATE_EXPIRES_SOON_PERIOD:
			return shortValue + " days";
		default: return "Invalid GlobalValue type";
		}
	}
	
	/*
	 * This method needs the second parameter from type GlobalTypes to distinguish between byte
	 * values which may be equal, e.g. TRANSPORT_PROTOCOL and SECURITY, which both may take values
	 * such as 0x00 or 0x01.
	 */
	public static String toString(byte[] globalValue, GlobalTypes globalType) {
		String globalFound = "";
		
		for (GlobalValues glValue : GlobalValues.values()) {
			if (glValue.type == globalType && Arrays.equals(glValue.byteArrayValue, globalValue)) {
				globalFound = glValue.toString();
				break;
			}
		}

		return globalFound;
	}
	
	/*
	 * This method needs the second parameter from type GlobalTypes to distinguish between byte array
	 * values which may be equal.
	 */
	public static String toString(byte globalValue, GlobalTypes globalType) {
		String globalFound = "";
		
		for (GlobalValues glValue : GlobalValues.values()) {
			if (glValue.type == globalType && glValue.byteValue == globalValue) {
				globalFound = glValue.toString();
				break;
			}
		}

		return globalFound;
	}

}
