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
package org.eclipse.risev2g.shared.exiCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.shared.utils.MiscUtils;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public abstract class ExiCodec {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private JAXBContext jaxbContext;
	private InputStream inStream;
	private Object decodedMessage;
	private String decodedExi;
	private boolean xmlRepresentation;
	
	public ExiCodec() {
		try {
			setJaxbContext(JAXBContext.newInstance(SupportedAppProtocolReq.class, SupportedAppProtocolRes.class, V2GMessage.class));
			setUnmarshaller(getJaxbContext().createUnmarshaller());
			setMarshaller(getJaxbContext().createMarshaller());
			
			/*
			 * JAXB by default silently ignores errors. Adding this code to throw an exception if 
			 * something goes wrong.
			 */
			getUnmarshaller().setEventHandler(
				    new ValidationEventHandler() {
				        public boolean handleEvent(ValidationEvent event ) {
				            throw new RuntimeException(event.getMessage(),
				                                       event.getLinkedException());
				        }
				});
			
			// Check if XML representation of sent messages is to be shown (for debug purposes)
			if ((boolean) MiscUtils.getPropertyValue("XMLRepresentationOfMessages")) 
				setXMLRepresentation(true);
			else
				setXMLRepresentation(false);
		} catch (JAXBException e) {
			getLogger().error("A JAXBException occurred while trying to instantiate " + this.getClass().getSimpleName(), e);
		}
	}
	
	
	public InputStream marshalToInputStream(Object jaxbObject) {	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			if (getInStream() != null) getInStream().reset();
			getMarshaller().marshal(jaxbObject, baos);
			setInStream(new ByteArrayInputStream(baos.toByteArray()));
			baos.close();
			
			if (isXMLRepresentation()) {
				// For debugging purposes, you can view the XML representation of marshalled messages
				StringWriter sw = new StringWriter();
				String className = "";
	
				if (jaxbObject instanceof V2GMessage) {
					className = ((V2GMessage) jaxbObject).getBody().getBodyElement().getName().getLocalPart();
				} else if (jaxbObject instanceof SupportedAppProtocolReq) {
					className = "SupportedAppProtocolReq"; 
				} else if (jaxbObject instanceof SupportedAppProtocolRes) {
					className = "SupportedAppProtocolRes";
				} else {
					className = "marshalled JAXBElement";
				}
				
				getMarshaller().marshal(jaxbObject, sw);
				getLogger().debug("XML representation of " + className + ":\n" + sw.toString());
				sw.close();
			}
			
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
			return getUnmarshaller().unmarshal(getInStream());
		} catch (IOException | JAXBException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to unmarshall decoded message", e);
			return null;
		}
	}
	
	
	public abstract byte[] encodeEXI(Object jaxbXML, boolean supportedAppProtocolHandshake);
	
	public abstract Object decodeEXI(byte[] exiEncodedMessage, boolean supportedAppProtocolHandshake);

	
	public Marshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	public void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
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
