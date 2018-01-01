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
package com.v2gclarity.risev2g.shared.messageHandling;

import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.exiCodec.EXIficientCodec;
import com.v2gclarity.risev2g.shared.exiCodec.ExiCodec;
import com.v2gclarity.risev2g.shared.exiCodec.OpenEXICodec;
import com.v2gclarity.risev2g.shared.misc.V2GTPMessage;
import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import com.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MessageHeaderType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.NotificationType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureValueType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignedInfoType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;


public final class MessageHandler {
	// -- BEGIN: SINGLETON DEFINITION --
	/*
	 *  Eager instantiation of the singleton, since a MessageHandler is always needed. 
	 *  The JVM creates the unique instance when the class is loaded and before any thread tries to 
	 *  access the instance variable -> thread safe.
	 */
	private static final MessageHandler instance = new MessageHandler();
	
	public static MessageHandler getInstance() {
		return instance;
	}
	// -- END: SINGLETON DEFINITION --
	
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName()); 
	private ExiCodec exiCodec;
	private JAXBContext jaxbContext;
	
	
	/**
	 * This constructor is used by V2GCommunicationSessionHandlerEVCC and -SECC
	 */
	public MessageHandler() {
		// Choose which implementation of an EXI codec to use in the respective properties file
		String exiCodecChoice = (String) MiscUtils.getPropertyValue("exi.codec");
		
		if (exiCodecChoice.equals("open_exi")) setExiCodec(OpenEXICodec.getInstance());
		else setExiCodec(EXIficientCodec.getInstance());
		
		// Setting the JAXBContext is a very time-consuming action and should only be done once during startup
		setJaxbContext(SupportedAppProtocolReq.class, SupportedAppProtocolRes.class, V2GMessage.class);
	} 
	
	public synchronized boolean isV2GTPMessageValid(V2GTPMessage v2gTpMessage) {
		if (isVersionAndInversionFieldCorrect(v2gTpMessage) && 
			isPayloadTypeCorrect(v2gTpMessage) && 
			isPayloadLengthCorrect(v2gTpMessage)) 
			return true;
		return false;
	}
	
	public synchronized boolean isVersionAndInversionFieldCorrect(V2GTPMessage v2gTpMessage) {
		if (v2gTpMessage.getProtocolVersion() != GlobalValues.V2GTP_VERSION_1_IS.getByteValue()) {
			getLogger().error("Protocol version (" + ByteUtils.toStringFromByte(v2gTpMessage.getProtocolVersion()) + 
							  ") is not supported!");
			return false;
		}
		
		if (v2gTpMessage.getInverseProtocolVersion() != (byte) (v2gTpMessage.getProtocolVersion() ^ 0xFF)) {
			getLogger().error("Inverse protocol version (" + ByteUtils.toStringFromByte(v2gTpMessage.getInverseProtocolVersion()) + 
							  ") does not match the inverse value of the protocol version (" + v2gTpMessage.getProtocolVersion() + ")!");
			return false;
		}
		
		return true;
	}
	
	public synchronized boolean isPayloadTypeCorrect(V2GTPMessage v2gTpMessage) {
		byte[] payloadType = v2gTpMessage.getPayloadType();

		if (Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue()) ||
			Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE.getByteArrayValue()) ||
			Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE.getByteArrayValue())) return true;
		
		getLogger().error("Payload type not supported! Proposed payload type: " + ByteUtils.toStringFromByteArray(v2gTpMessage.getPayloadType()));
		
		return false;
	}
	
	public synchronized boolean isPayloadLengthCorrect(V2GTPMessage v2gTpMessage) {
		if (ByteUtils.toLongFromByteArray(v2gTpMessage.getPayloadLength()) > GlobalValues.V2GTP_HEADER_MAX_PAYLOAD_LENGTH.getLongValue() ||
			ByteUtils.toLongFromByteArray(v2gTpMessage.getPayloadLength()) < 0L) {
			getLogger().error("Payload length (" + ByteUtils.toLongFromByteArray(v2gTpMessage.getPayloadLength()) + 
							  " bytes) not supported! Must be between 0 and " + 
							  GlobalValues.V2GTP_HEADER_MAX_PAYLOAD_LENGTH.getLongValue() + " bytes");
			return false;
		}
		
		int payLoadLengthField = ByteUtils.toIntFromByteArray(v2gTpMessage.getPayloadLength());
		if (v2gTpMessage.getPayload().length != payLoadLengthField) {
			getLogger().error("Length of payload (" + v2gTpMessage.getPayload().length + " bytes) does not match value of " +
							  "field payloadLength (" + payLoadLengthField + " bytes)");
			return false;
		}
		
		return true;
	}
	
	

	public synchronized Object suppAppProtocolMsgToExi(Object suppAppProtocolObject) {
		return getExiCodec().encodeEXI(suppAppProtocolObject, GlobalValues.SCHEMA_PATH_APP_PROTOCOL.toString());
	}
	

	public synchronized Object v2gMsgToExi(Object jaxbObject) {
		byte[] encodedEXI = getExiCodec().encodeEXI(jaxbObject, GlobalValues.SCHEMA_PATH_MSG_DEF.toString());
		return encodedEXI;
	}
	

	public synchronized Object exiToSuppAppProtocolMsg(byte[] exiEncodedMessage) {
		return getExiCodec().decodeEXI(exiEncodedMessage, true);
	}
	
	public synchronized Object exiToV2gMsg(byte[] exiEncodedMessage) {
		return getExiCodec().decodeEXI(exiEncodedMessage, false);
	}
	
	
	public synchronized V2GMessage getV2GMessage(
			byte[] sessionID, 
			HashMap<String, byte[]> xmlSignatureRefElements,
			ECPrivateKey signaturePrivateKey,
			JAXBElement<? extends BodyBaseType> v2gMessageInstance) {
		return getV2GMessage(sessionID, null, xmlSignatureRefElements, signaturePrivateKey, v2gMessageInstance);
	}

	
	public synchronized V2GMessage getV2GMessage(
			byte[] sessionID,
			NotificationType notification, 
			HashMap<String, byte[]> xmlSignatureRefElements, 
			ECPrivateKey signaturePrivateKey,
			JAXBElement<? extends BodyBaseType> v2gMessageInstance) {
		BodyType body = new BodyType();
		body.setBodyElement(v2gMessageInstance);
		
		V2GMessage v2gMessage = new V2GMessage();
		v2gMessage.setHeader(getHeader(sessionID, notification, v2gMessageInstance, xmlSignatureRefElements, signaturePrivateKey));
		v2gMessage.setBody(body);
		
		return v2gMessage;
	}
	
	
	private synchronized MessageHeaderType getHeader(
			byte[] sessionID,
			NotificationType notification,
			JAXBElement<? extends BodyBaseType> v2gMessageInstance,
			HashMap<String, byte[]> xmlSignatureRefElements,
			ECPrivateKey signaturePrivateKey) {
		MessageHeaderType header =  new MessageHeaderType();
		header.setSessionID(sessionID);
		header.setNotification(notification);
		
		if (xmlSignatureRefElements != null && xmlSignatureRefElements.size() != 0) {
			SignedInfoType signedInfo = SecurityUtils.getSignedInfo(xmlSignatureRefElements);
			
			byte[] signature = SecurityUtils.signSignedInfoElement(
									getExiCodec().getExiEncodedSignedInfo(getJaxbElement(signedInfo)), 
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
	
	
	/**
	 * Creates an XML element from the given object which may be a complete message or just a field of a
	 * message. In case of XML signature generation, for some messages certain fields need to be signed
	 * instead of the complete message. 
	 * 
	 * Suppressed unchecked warning, previously used a type-safe version such as new 
	 * JAXBElement<SessionStopReqType>(new QName ... ) but this seems to work as well 
	 * (I don't know how to infer the type correctly)
	 * 
	 * @param messageOrField The message or field for which a digest is to be generated
	 * @return The JAXBElement of the provided message or field
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized JAXBElement getJaxbElement(Object messageOrField) {
		String messageName = messageOrField.getClass().getSimpleName().replace("Type", "");
		String namespace = "";
		JAXBElement jaxbElement = null;
		
		if (messageOrField instanceof SignedInfoType) {
			namespace = GlobalValues.V2G_CI_XMLDSIG_NAMESPACE.toString();
		} else {
			namespace = GlobalValues.V2G_CI_MSG_BODY_NAMESPACE.toString();
			
			/* 
			 * We need to set the localPart of the QName object for the CertificateInstallationRes/CertificateUpdateRes parameters
			 * correctly. The messageOrField object's class name cannot be taken directly as it differs from what should be the 
			 * XML element name.
			 */
			switch (messageName) {
			case "CertificateChain":
				messageName = "ContractSignatureCertChain";
				break;
			case "DiffieHellmanPublickey":
				messageName = "DHpublickey";
				break;
			case "EMAID":
				messageName = "eMAID";
				break;
			case "ContractSignatureEncryptedPrivateKey":
				messageName = "ContractSignatureEncryptedPrivateKey";
				break;
			case "SalesTariff": // SalesTariff is not defined in MsgBody XSD schema, but MsgDataTypes XSD schema
				namespace = GlobalValues.V2G_CI_MSG_DATATYPES_NAMESPACE.toString();
				break;
			default:
				break;
			}
		}
		
		jaxbElement = new JAXBElement(new QName(namespace, messageName), 
				messageOrField.getClass(), 
				messageOrField);
		
		return jaxbElement; 
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
	
	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	private void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}
	
	public synchronized void setJaxbContext(Class... classesToBeBound) {
		try {
			setJaxbContext(JAXBContext.newInstance(classesToBeBound));
			
			// Every time we set the JAXBContext, we need to also set the marshaller and unmarshaller for EXICodec
			getExiCodec().setUnmarshaller(getJaxbContext().createUnmarshaller());
			getExiCodec().setMarshaller(getJaxbContext().createMarshaller());
			
			/*
			 * JAXB by default silently ignores errors. Adding this code to throw an exception if 
			 * something goes wrong.
			 */
			getExiCodec().getUnmarshaller().setEventHandler(
				    new ValidationEventHandler() {
				        @Override
						public boolean handleEvent(ValidationEvent event ) {
				            throw new RuntimeException(event.getMessage(),
				                                       event.getLinkedException());
				        }
				});
		} catch (JAXBException e) {
			getLogger().error("A JAXBException occurred while trying to set JAXB context", e);
		}
	}
}
