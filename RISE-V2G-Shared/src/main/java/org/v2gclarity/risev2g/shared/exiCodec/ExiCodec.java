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
package org.v2gclarity.risev2g.shared.exiCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.utils.MiscUtils;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public abstract class ExiCodec {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private InputStream inStream;
	private Object decodedMessage;
	private String decodedExi;
	private boolean xmlRepresentation;
	
	public ExiCodec() {
		// Check if XML representation of sent messages is to be shown (for debug purposes)
		if ((boolean) MiscUtils.getPropertyValue("XMLRepresentationOfMessages")) 
			setXMLRepresentation(true);
		else
			setXMLRepresentation(false);
	}
	
	
	public InputStream marshalToInputStream(Object jaxbObject) {	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			if (getInStream() != null) getInStream().reset();
			getMarshaller().marshal(jaxbObject, baos);
			
			setInStream(new ByteArrayInputStream(baos.toByteArray()));
			baos.close();
			
			if (isXMLRepresentation()) showXMLRepresentationOfMessage(jaxbObject);
			return getInStream();
		} catch (JAXBException | IOException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to marshal to InputStream from JAXBElement", e);
			return null;
		}
	}
	
	
	public Object unmarshallToMessage(String decodedExiString) {
		try {
			if (getInStream() != null) getInStream().reset();
			setInStream(new ByteArrayInputStream(decodedExiString.getBytes()));
			Object unmarhalledObject = getUnmarshaller().unmarshal(getInStream());
			
			if (isXMLRepresentation()) showXMLRepresentationOfMessage(unmarhalledObject);
			return unmarhalledObject;
		} catch (IOException | JAXBException | RuntimeException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to unmarshall decoded message", e);
			return null;
		}
	}
	
	
	/**
	 * Shows the XML representation of a marshalled or unmarshalled message object. This is useful for debugging
	 * purposes.
	 * 
	 * @param message The (un)marshalled message object
	 */
	@SuppressWarnings("rawtypes")
	public void showXMLRepresentationOfMessage(Object message) {
		StringWriter sw = new StringWriter();
		String className = "";
		
		if (message instanceof V2GMessage) {
			className = ((V2GMessage) message).getBody().getBodyElement().getName().getLocalPart();
		} else if (message instanceof JAXBElement) {
			className = ((JAXBElement) message).getName().getLocalPart();
		} else if (message instanceof SupportedAppProtocolReq) {
			className = "SupportedAppProtocolReq"; 
		} else if (message instanceof SupportedAppProtocolRes) {
			className = "SupportedAppProtocolRes";
		} else {
			className = "marshalled JAXBElement";
		}
		
		try {
			getMarshaller().marshal(message, sw);
			getLogger().debug("XML representation of " + className + ":\n" + sw.toString());
		} catch (JAXBException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to show XML representation of " + className, e);
		}
	}
	
	
	/**
	 * Provides the EXI encoding of the header's SignedInfo element. The resulting byte array can then be used to
	 * verify a signature.
	 * 
	 * @param jaxbSignedInfo The SignedInfo element of the V2GMessage header, given as a JAXB element
	 * @return The EXI encoding of the SignedInfo element given as a byte array
	 */
	public byte[] getExiEncodedSignedInfo(JAXBElement jaxbSignedInfo) {
		// The schema-informed fragment grammar option needs to be used for EXI encodings in the header's signature
		setFragment(true);
		
		// The SignedInfo element must be encoded
		byte[] encodedSignedInfo = encodeEXI(
										jaxbSignedInfo, 
										GlobalValues.SCHEMA_PATH_XMLDSIG.toString()
								   );
		
		// Do not use the schema-informed fragment grammar option for other EXI encodings (message bodies)
		setFragment(false);
		
		return encodedSignedInfo;
	}
	
	
	public abstract byte[] encodeEXI(Object jaxbXML, String xsdSchemaPath);
	
	public abstract Object decodeEXI(byte[] exiEncodedMessage, boolean supportedAppProtocolHandshake);

	public abstract void setFragment(boolean useFragmentGrammar);
	
	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public Object getDecodedMessage() {
		return decodedMessage;
	}


	public void setDecodedMessage(Object decodedMessage) {
		this.decodedMessage = decodedMessage;
	}


	public String getDecodedExi() {
		return decodedExi;
	}


	public void setDecodedExi(String decodedExi) {
		this.decodedExi = decodedExi;
	}


	public InputStream getInStream() {
		return inStream;
	}


	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}
	
	
	private void setXMLRepresentation(boolean showXMLRepresentation) {
		this.xmlRepresentation = showXMLRepresentation;
	}
	
	public boolean isXMLRepresentation() {
		return xmlRepresentation;
	}
}
