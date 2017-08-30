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
package org.v2gclarity.risev2g.shared.messageHandling;

import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.exiCodec.EXIficientCodec;
import org.v2gclarity.risev2g.shared.exiCodec.ExiCodec;
import org.v2gclarity.risev2g.shared.exiCodec.OpenEXICodec;
import org.v2gclarity.risev2g.shared.misc.V2GCommunicationSession;
import org.v2gclarity.risev2g.shared.misc.V2GTPMessage;
import org.v2gclarity.risev2g.shared.utils.ByteUtils;
import org.v2gclarity.risev2g.shared.utils.MiscUtils;
import org.v2gclarity.risev2g.shared.utils.SecurityUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.MessageHeaderType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.NotificationType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureValueType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignedInfoType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;


public class MessageHandler {
	
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName()); 
	private ExiCodec exiCodec;
	private V2GCommunicationSession commSessionContext;
	private JAXBContext jaxbContext;
	private enum jaxbContextEnum {
		SUPPORTED_APP_PROTOCOL_REQ,
		SUPPORTED_APP_PROTOCOL_RES,
		V2G_MESSAGE,
		OTHER // includes the jaxbContext needed for the parameters of CertificateInstallationRes/CertificateUpdateRes
	}
	private jaxbContextEnum currentJaxbContext;
	
	/**
	 * This constructor is used by V2GCommunicationSessionEVCC and -SECC
	 * 
	 * @param commSessionContext The respective V2GCommunicationSessionEVCC or -SECC instance
	 */
	public MessageHandler(V2GCommunicationSession commSessionContext) {
		this();
		setCommSessionContext(commSessionContext);
		setCurrentJaxbContext(jaxbContextEnum.SUPPORTED_APP_PROTOCOL_REQ);
	}
	
	/**
	 * This constructor is used by V2GCommunicationSessionHandlerEVCC and -SECC
	 */
	public MessageHandler() {
		// Choose which implementation of an EXI codec to use in the respective properties file
		String exiCodecChoice = (String) MiscUtils.getPropertyValue("EXICodec");
		
		if (exiCodecChoice.equals("open_exi")) setExiCodec(OpenEXICodec.getInstance());
		else setExiCodec(EXIficientCodec.getInstance());
		
		setJaxbContext(SupportedAppProtocolReq.class, SupportedAppProtocolRes.class, V2GMessage.class);
		setCurrentJaxbContext(jaxbContextEnum.SUPPORTED_APP_PROTOCOL_REQ);
	} 
	
	public boolean isV2GTPMessageValid(V2GTPMessage v2gTpMessage) {
		if (isVersionAndInversionFieldCorrect(v2gTpMessage) && 
			isPayloadTypeCorrect(v2gTpMessage) && 
			isPayloadLengthCorrect(v2gTpMessage)) 
			return true;
		return false;
	}
	
	public boolean isVersionAndInversionFieldCorrect(V2GTPMessage v2gTpMessage) {
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
	
	public boolean isPayloadTypeCorrect(V2GTPMessage v2gTpMessage) {
		byte[] payloadType = v2gTpMessage.getPayloadType();

		if (Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue()) ||
			Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE.getByteArrayValue()) ||
			Arrays.equals(payloadType, GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE.getByteArrayValue())) return true;
		
		getLogger().error("Payload type not supported! Proposed payload type: " + ByteUtils.toStringFromByteArray(v2gTpMessage.getPayloadType()));
		
		return false;
	}
	
	public boolean isPayloadLengthCorrect(V2GTPMessage v2gTpMessage) {
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
	 * Setting the JAXBContext is a little time consuming. Thus, this method checks which JAXBContext is currently set and does
	 * only set it anew if needed. For example, if the JAXBContext is already set for V2GMessage.class, then it will not be set anew
	 * if the JAXBElement for a message derived from V2GMessage is to be returned.
	 * The JAXBContext for the XML reference elements of CertificateInstallationRes/CertificateUpdateRes should be minimal and not
	 * comprise the complete V2GMessage.class.
	 * 
	 * Suppressed unchecked warning, previously used a type-safe version such as new 
	 * JAXBElement<SessionStopReqType>(new QName ... ) but this seems to work as well 
	 * (I don't know how to infer the type correctly)
	 * 
	 * @param messageOrField The message or field for which a digest is to be generated
	 * @return The JAXBElement of the provided message or field
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JAXBElement getJaxbElement(Object messageOrField) {
		String messageName = messageOrField.getClass().getSimpleName().replace("Type", "");
		String namespace = "";
		JAXBElement jaxbElement = null;
		
		if (messageName.equals("EMAID") || 
			messageName.equals("CertificateChain") ||
			messageName.equals("DiffieHellmanPublickey") ||
			messageName.equals("ContractSignatureEncryptedPrivateKey")) {
			
			/*
			 * If this branch is entered, we always need to set the JAXBContext anew because those elements don't repeat 
			 * (like the jaxbContext for V2GMessage.class)
			 */
			setJaxbContext(messageOrField.getClass());
			setCurrentJaxbContext(jaxbContextEnum.OTHER);
		} else if (messageOrField instanceof SupportedAppProtocolReq && 
				  !getCurrentJaxbContext().equals(jaxbContextEnum.SUPPORTED_APP_PROTOCOL_REQ)) {
			setJaxbContext(SupportedAppProtocolReq.class);
			setCurrentJaxbContext(jaxbContextEnum.SUPPORTED_APP_PROTOCOL_REQ);
		} else if (messageOrField instanceof SupportedAppProtocolRes && 
				  !getCurrentJaxbContext().equals(jaxbContextEnum.SUPPORTED_APP_PROTOCOL_RES)) {
			setJaxbContext(SupportedAppProtocolRes.class);
			setCurrentJaxbContext(jaxbContextEnum.SUPPORTED_APP_PROTOCOL_RES);
		} else if (!getCurrentJaxbContext().equals(jaxbContextEnum.V2G_MESSAGE)) {
			setJaxbContext(V2GMessage.class);
			setCurrentJaxbContext(jaxbContextEnum.V2G_MESSAGE);
		} else {
			// nothing to do here
		}
		
		if (messageOrField instanceof SignedInfoType) {
			namespace = GlobalValues.V2G_CI_XMLDSIG_NAMESPACE.toString();
		} else {
			namespace = GlobalValues.V2G_CI_MSG_BODY_NAMESPACE.toString();
			
			/* 
			 * We need to set the localPart of the QName object for the CertificateInstallationRes/CertificateUpdateRes parameters
			 * correctly. The messageOrField object's class name cannot be taken directly as it differs from what should be the 
			 * XML element name.
			 * 
			 * In principle, there are two ways of setting the namespace for the XML elements of the parameters of a 
			 * CertificateInstallationRes/CertificateUpdatenRes. Annex J of ISO 15118-2 is not clear about that. Standard rules of
			 * XSD would require to always set a so-called target namespace, in this case GlobalValues.V2G_CI_MSG_BODY_NAMESPACE.
			 * But you could also use the empty namespace "" and would still be conform to the standard.
			 * The choice of the namespace heavily influences interoperability as the resulting digest values will be different.
			 * 
			 * I recommend using the namespace GlobalValues.V2G_CI_MSG_BODY_NAMESPACE as this seems to be adopted by the industry.
			 */
			switch (messageName) {
			case "CertificateChain":
				messageName = "ContractSignatureCertChain";
//				namespace = "";
				break;
			case "DiffieHellmanPublickey":
				messageName = "DHpublickey";
//				namespace = "";
				break;
			case "EMAID":
				messageName = "eMAID";
//				namespace = "";
				break;
			case "ContractSignatureEncryptedPrivateKey":
				messageName = "ContractSignatureEncryptedPrivateKey";
//				namespace = "";
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

	public V2GCommunicationSession getCommSessionContext() {
		return commSessionContext;
	}

	public void setCommSessionContext(V2GCommunicationSession commSessionContext) {
		this.commSessionContext = commSessionContext;
	}
	
	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	private void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}
	
	public void setJaxbContext(Class... classesToBeBound) {
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

	public jaxbContextEnum getCurrentJaxbContext() {
		return currentJaxbContext;
	}

	public void setCurrentJaxbContext(jaxbContextEnum currentJaxbContext) {
		this.currentJaxbContext = currentJaxbContext;
	}
}
