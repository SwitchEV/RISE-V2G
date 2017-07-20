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
package org.eclipse.risev2g.shared.messageHandling;

import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.exiCodec.EXIficientCodec;
import org.eclipse.risev2g.shared.exiCodec.ExiCodec;
import org.eclipse.risev2g.shared.exiCodec.OpenEXICodec;
import org.eclipse.risev2g.shared.misc.V2GCommunicationSession;
import org.eclipse.risev2g.shared.misc.V2GTPMessage;
import org.eclipse.risev2g.shared.utils.ByteUtils;
import org.eclipse.risev2g.shared.utils.MiscUtils;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.BodyType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MessageHeaderType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.NotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureValueType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignedInfoType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;


public class MessageHandler {
	
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName()); 
	private ExiCodec exiCodec;
	private V2GCommunicationSession commSessionContext;
	
	/**
	 * This constructor is used by V2GCommunicationSessionEVCC and -SECC
	 * 
	 * @param commSessionContext The respective V2GCommunicationSessionEVCC or -SECC instance
	 */
	public MessageHandler(V2GCommunicationSession commSessionContext) {
		this();
		setCommSessionContext(commSessionContext);
	}
	
	/**
	 * This constructor is used by V2GCommunicationSessionHandlerEVCC and -SECC
	 */
	public MessageHandler() {
		// Choose which implementation of an EXI codec to use
		setExiCodec(EXIficientCodec.getInstance());
//		setExiCodec(OpenEXICodec.getInstance());
	} 
	
	public boolean isV2GTPMessageValid(V2GTPMessage v2gTpMessage) {
		if (isVersionAndInversionFieldCorrect(v2gTpMessage) && 
			isPayloadTypeCorrect(v2gTpMessage) && 
			checkPayloadLength(v2gTpMessage)) 
			return true;
		return false;
	}
	
	public boolean isVersionAndInversionFieldCorrect(V2GTPMessage v2gTpMessage) {
		/* 
		 * The inversion field is set by a private method in V2GTPMessage.java and cannot be set from the outside
		 * Therefore an additional check for the inversion field is not necessary.
		 */
		if (v2gTpMessage.getProtocolVersion() == GlobalValues.V2GTP_VERSION_1_IS.getByteValue()) return true;
		
		getLogger().error("Protocol version or inverse protocol version of '" + 
						  ByteUtils.toStringFromByte(v2gTpMessage.getProtocolVersion()) + 
						  "' is not supported!");
		
		return false;
	}
	
	public boolean isPayloadTypeCorrect(V2GTPMessage v2gTpMessage) {
		byte[] payloadType = v2gTpMessage.getPayloadType();

		if (Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue()) ||
			Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE.getByteArrayValue()) ||
			Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE.getByteArrayValue())) return true;
		
		getLogger().error("Payload type not supported! Proposed payload type: " + ByteUtils.toStringFromByteArray(v2gTpMessage.getPayloadType()));
		
		return false;
	}
	
	public boolean checkPayloadLength(V2GTPMessage v2gTpMessage) {
		if (ByteUtils.toLongFromByteArray(v2gTpMessage.getPayloadLength()) <= 
			GlobalValues.V2GTP_HEADER_MAX_PAYLOAD_LENGTH.getLongValue()) return true;

		getLogger().error("Payload length not supported! Payload length: " + 
				ByteUtils.toLongFromByteArray(v2gTpMessage.getPayloadLength()) + " bytes");
		
		return false;
	}
	
	

	public synchronized Object suppAppProtocolMsgToExi(Object suppAppProtocolObject) {
		return getExiCodec().encodeEXI(suppAppProtocolObject, GlobalValues.SCHEMA_PATH_APP_PROTOCOL.toString());
	}
	

	public synchronized Object v2gMsgToExi(Object jaxbObject) {
		byte[] encodedEXI = getExiCodec().encodeEXI(jaxbObject, GlobalValues.SCHEMA_PATH_MSG_DEF.toString());
		
		// For test purposes you can log the byte array
//		getLogger().debug("Encoded EXI byte array: " + ByteUtils.toHexString(encodedEXI));
		
		return encodedEXI;
	}
	

	public synchronized Object exiToSuppAppProtocolMsg(byte[] exiEncodedMessage) {
		return getExiCodec().decodeEXI(exiEncodedMessage, true);
	}
	
	public synchronized Object exiToV2gMsg(byte[] exiEncodedMessage) {
		// For debugging purposes
//		getLogger().debug("Hex string of encoded EXI byte array: " + ByteUtils.toHexString(exiEncodedMessage));
		
		return getExiCodec().decodeEXI(exiEncodedMessage, false);
	}
	
	
	public V2GMessage getV2GMessage(
			HashMap<String, byte[]> xmlSignatureRefElements,
			ECPrivateKey signaturePrivateKey,
			JAXBElement<? extends BodyBaseType> v2gMessageInstance) {
		return getV2GMessage(null, xmlSignatureRefElements, signaturePrivateKey, v2gMessageInstance);
	}

	
	public V2GMessage getV2GMessage(
			NotificationType notification, 
			HashMap<String, byte[]> xmlSignatureRefElements, 
			ECPrivateKey signaturePrivateKey,
			JAXBElement<? extends BodyBaseType> v2gMessageInstance) {
		BodyType body = new BodyType();
		body.setBodyElement(v2gMessageInstance);
		
		V2GMessage v2gMessage = new V2GMessage();
		v2gMessage.setHeader(getHeader(notification, v2gMessageInstance, xmlSignatureRefElements, signaturePrivateKey));
		v2gMessage.setBody(body);
		
		return v2gMessage;
	}
	
	
	private MessageHeaderType getHeader(
			NotificationType notification,
			JAXBElement<? extends BodyBaseType> v2gMessageInstance,
			HashMap<String, byte[]> xmlSignatureRefElements,
			ECPrivateKey signaturePrivateKey) {
		MessageHeaderType header =  new MessageHeaderType();
		header.setSessionID(getCommSessionContext().getSessionID());
		header.setNotification(notification);
		
		if (xmlSignatureRefElements != null && xmlSignatureRefElements.size() != 0) {
			SignedInfoType signedInfo = SecurityUtils.getSignedInfo(xmlSignatureRefElements);
			
			byte[] signature = SecurityUtils.signSignedInfoElement(
									getExiCodec().getExiEncodedSignedInfo(signedInfo), 
									signaturePrivateKey
							   );
			
			SignatureValueType signatureValue = new SignatureValueType();
			signatureValue.setValue(signature);
			
			SignatureType xmlSignature = new SignatureType();
			xmlSignature.setSignatureValue(signatureValue);
			xmlSignature.setSignedInfo(signedInfo);
			
			header.setSignature(xmlSignature);
		}
		
		return header;
	}
	
	
	public Logger getLogger() {
		return logger;
	}

	public ExiCodec getExiCodec() {
		return exiCodec;
	}

	public void setExiCodec(ExiCodec exiCodec) {
		this.exiCodec = exiCodec;
		SecurityUtils.setExiCodec(exiCodec);
	}

	public V2GCommunicationSession getCommSessionContext() {
		return commSessionContext;
	}

	public void setCommSessionContext(V2GCommunicationSession commSessionContext) {
		this.commSessionContext = commSessionContext;
	}
}
