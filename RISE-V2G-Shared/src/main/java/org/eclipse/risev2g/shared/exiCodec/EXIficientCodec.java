package org.eclipse.risev2g.shared.exiCodec;

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


//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.StringWriter;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.sax.SAXSource;
//import javax.xml.transform.stream.StreamResult;
//import org.eclipse.risev2g.shared.enumerations.GlobalValues;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//import org.xml.sax.helpers.XMLReaderFactory;
//import com.siemens.ct.exi.EXIFactory;
//import com.siemens.ct.exi.GrammarFactory;
//import com.siemens.ct.exi.api.sax.EXIResult;
//import com.siemens.ct.exi.api.sax.EXISource;
//import com.siemens.ct.exi.exceptions.EXIException;
//import com.siemens.ct.exi.grammars.Grammars;
//import com.siemens.ct.exi.helpers.DefaultEXIFactory;
//
//public final class EXIficientCodec extends ExiCodec {
//	// -- BEGIN: SINGLETON DEFINITION --
//	/*
//	 *  Eager instantiation of the singleton, since a EXIficientCodec is always needed. 
//	 *  The JVM creates the unique instance when the class is loaded and before any thread tries to 
//	 *  access the instance variable -> thread safe.
//	 */
//	private static final EXIficientCodec instance = new EXIficientCodec();
//	
//	private EXIFactory exiFactory;
//	private GrammarFactory grammarFactory;
//	private Grammars grammarAppProtocol;
//	private Grammars grammarMsgDef;
//	private OutputStream encodeOS;
//	
//	private EXIficientCodec() {
//		super();
//		
//		setExiFactory(DefaultEXIFactory.newInstance());
//		getExiFactory().setValuePartitionCapacity(0);
//		setGrammarFactory(GrammarFactory.newInstance());
//		
//		/*
//		 *  The supportedAppProtocolReq and -Res message have a different schema than the rest
//		 *  of the V2G application layer messages
//		 */
//		try {
//			setGrammarAppProtocol(getGrammarFactory().createGrammars(
//					getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_APP_PROTOCOL.toString())));
//			setGrammarMsgDef(getGrammarFactory().createGrammars(
//					getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_MSG_DEF.toString()),
//					XSDResolver.getInstance()));
//		} catch (EXIException e) {
//			getLogger().error("Error occurred while trying to initialize EXIficientCodec (EXIException)!", e);
//		}
//	}
//	
//	public static EXIficientCodec getInstance() {
//		return instance;
//	}
//	// -- END: SINGLETON DEFINITION --
//	
//
//
//	
//	public synchronized byte[] encodeEXI(Object jaxbObject, boolean supportedAppProtocolHandshake) {
//		InputStream inStream = marshalToInputStream(jaxbObject);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		baos = ((ByteArrayOutputStream) encode(inStream, supportedAppProtocolHandshake));
//		
//		return baos.toByteArray();
//	}
//
//	
//	private synchronized OutputStream encode(InputStream jaxbXML, boolean supportedAppProtocolHandshake) {
//		if (supportedAppProtocolHandshake) return encode(jaxbXML, getGrammarAppProtocol());
//		else return encode(jaxbXML, getGrammarMsgDef());
//	}
//	
//	
//	private synchronized OutputStream encode(InputStream jaxbXML, Grammars grammar) {
//		EXIResult exiResult = null;
//		
//		try {
//			exiFactory.setGrammars(grammar);
//			encodeOS = new ByteArrayOutputStream();
//			exiResult = new EXIResult(exiFactory);
//			exiResult.setOutputStream(encodeOS);
//			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
//			xmlReader.setContentHandler(exiResult.getHandler());
//
//			// parse xml file
//			xmlReader.parse(new InputSource(jaxbXML));
//			
//			encodeOS.close();
//		} catch (SAXException | IOException | EXIException e) {
//			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to encode", e);
//		}  
//		
//		return encodeOS;
//	}
//	
//	
//	@Override
//	public synchronized Object decodeEXI(byte[] exiEncodedMessage, boolean supportedAppProtocolHandshake) {
//		ByteArrayInputStream bais = new ByteArrayInputStream(exiEncodedMessage);
//		setDecodedExi(decode(bais, supportedAppProtocolHandshake));
//		
//		return unmarshallToMessage(getDecodedExi());
//	}
//	
//	
//	private synchronized String decode(InputStream exiInputStream, boolean supportedAppProtocolHandshake) {
//		TransformerFactory tf = TransformerFactory.newInstance();
//		Transformer transformer = null;
//		StringWriter stringWriter = new StringWriter();
//
//		try {
//			transformer = tf.newTransformer();
//		} catch (TransformerConfigurationException e) {
//			getLogger().error("Error occurred while trying to decode (TransformerConfigurationException)", e);
//		}
//		
//		if (supportedAppProtocolHandshake) exiFactory.setGrammars(grammarAppProtocol);
//		else exiFactory.setGrammars(grammarMsgDef);
//		
//		try {
//			EXISource saxSource = new EXISource(exiFactory);
//			SAXSource exiSource = new SAXSource(new InputSource(exiInputStream));
//			XMLReader exiReader = saxSource.getXMLReader();
//			exiSource.setXMLReader(exiReader);
//			transformer.transform(exiSource, new StreamResult(stringWriter));
//		} catch (EXIException e) {
//			getLogger().error("Error occurred while trying to decode (EXIException)", e);
//		} catch (TransformerException e) {
//			getLogger().error("Error occurred while trying to decode (TransformerException)", e);
//		}
//		
//		return stringWriter.toString();
//	}
//
//	private Grammars getGrammarAppProtocol() {
//		return grammarAppProtocol;
//	}
//
//	private void setGrammarAppProtocol(Grammars grammarAppProtocol) {
//		this.grammarAppProtocol = grammarAppProtocol;
//	}
//
//	private Grammars getGrammarMsgDef() {
//		return grammarMsgDef;
//	}
//
//	private void setGrammarMsgDef(Grammars grammarMsgDef) {
//		this.grammarMsgDef = grammarMsgDef;
//	}
//
//	private EXIFactory getExiFactory() {
//		return exiFactory;
//	}
//
//	private void setExiFactory(EXIFactory exiFactory) {
//		this.exiFactory = exiFactory;
//	}
//
//	private GrammarFactory getGrammarFactory() {
//		return grammarFactory;
//	}
//
//	private void setGrammarFactory(GrammarFactory grammarFactory) {
//		this.grammarFactory = grammarFactory;
//	}
//}
