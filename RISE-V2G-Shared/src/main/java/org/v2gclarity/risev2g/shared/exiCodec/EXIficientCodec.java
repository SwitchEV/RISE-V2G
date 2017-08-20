package org.v2gclarity.risev2g.shared.exiCodec;

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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public final class EXIficientCodec extends ExiCodec {
	// -- BEGIN: SINGLETON DEFINITION --
	/*
	 *  Eager instantiation of the singleton, since a EXIficientCodec is always needed. 
	 *  The JVM creates the unique instance when the class is loaded and before any thread tries to 
	 *  access the instance variable -> thread safe.
	 */
	private static final EXIficientCodec instance = new EXIficientCodec();
	
	private EXIFactory exiFactory;
	private GrammarFactory grammarFactory;
	private Grammars grammarAppProtocol;
	private Grammars grammarMsgDef;
	private Grammars grammarXMLDSig;
	private OutputStream encodeOS;
	
	private EXIficientCodec() {
		super();
		
		setExiFactory(DefaultEXIFactory.newInstance());
		setGrammarFactory(GrammarFactory.newInstance());
		
		/*
		 *  The supportedAppProtocolReq and -Res message have a different schema than the rest
		 *  of the V2G application layer messages
		 */
		try {
			setGrammarAppProtocol(getGrammarFactory().createGrammars(
					getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_APP_PROTOCOL.toString())));
			setGrammarMsgDef(getGrammarFactory().createGrammars(
					getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_MSG_DEF.toString()),
					XSDResolver.getInstance()));
			setGrammarXMLDSig(getGrammarFactory().createGrammars(
					getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_XMLDSIG.toString()),
					XSDResolver.getInstance()));
		} catch (EXIException e) {
			getLogger().error("Error occurred while trying to initialize EXIficientCodec (EXIException)!", e);
		}
		
		// Non-default settings to fulfill requirements [V2G2-099] and [V2G2-600]
		getExiFactory().setValuePartitionCapacity(0);
		getExiFactory().setMaximumNumberOfBuiltInElementGrammars(0);
		getExiFactory().setMaximumNumberOfBuiltInProductions(0);
	}
	
	public static EXIficientCodec getInstance() {
		return instance;
	}
	// -- END: SINGLETON DEFINITION --

	
	@Override
	public synchronized byte[] encodeEXI(Object jaxbObject, String xsdSchemaPath) {
		Grammars grammar = null;
		
		if (xsdSchemaPath.equals(GlobalValues.SCHEMA_PATH_APP_PROTOCOL.toString()))
			grammar = getGrammarAppProtocol();
		else if (xsdSchemaPath.equals(GlobalValues.SCHEMA_PATH_MSG_DEF.toString()))
			grammar = getGrammarMsgDef();
		else if (xsdSchemaPath.equals(GlobalValues.SCHEMA_PATH_XMLDSIG.toString()))
			grammar = getGrammarXMLDSig();
		else {
			getLogger().error("False schema path provided for encoding jaxbObject into EXI");
			return null;
		}
			
		InputStream inStream = marshalToInputStream(jaxbObject);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos = ((ByteArrayOutputStream) encode(inStream, grammar));
		
		return baos.toByteArray();
	}

	private synchronized OutputStream encode(InputStream jaxbXML, Grammars grammar) {
		EXIResult exiResult = null;
		
		try {
			exiFactory.setGrammars(grammar);
			encodeOS = new ByteArrayOutputStream();
			exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(encodeOS);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());

			// parse xml file
			xmlReader.parse(new InputSource(jaxbXML));
			
			encodeOS.close();
		} catch (SAXException | IOException | EXIException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to encode", e);
		}  
		
		return encodeOS;
	}
	
	
	@Override
	public synchronized Object decodeEXI(byte[] exiEncodedMessage, boolean supportedAppProtocolHandshake) {
		ByteArrayInputStream bais = new ByteArrayInputStream(exiEncodedMessage);
		setDecodedExi(decode(bais, supportedAppProtocolHandshake));
		
		return unmarshallToMessage(getDecodedExi());
	}
	
	
	private synchronized String decode(InputStream exiInputStream, boolean supportedAppProtocolHandshake) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		StringWriter stringWriter = new StringWriter();

		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			getLogger().error("Error occurred while trying to decode (TransformerConfigurationException)", e);
		}
		
		if (supportedAppProtocolHandshake) exiFactory.setGrammars(grammarAppProtocol);
		else exiFactory.setGrammars(grammarMsgDef);
		
		try {
			EXISource saxSource = new EXISource(exiFactory);
			SAXSource exiSource = new SAXSource(new InputSource(exiInputStream));
			XMLReader exiReader = saxSource.getXMLReader();
			exiSource.setXMLReader(exiReader);
			transformer.transform(exiSource, new StreamResult(stringWriter));
		} catch (EXIException e) {
			getLogger().error("Error occurred while trying to decode (EXIException)", e);
		} catch (TransformerException e) {
			getLogger().error("Error occurred while trying to decode (TransformerException)", e);
		}
		
		return stringWriter.toString();
	}

	private Grammars getGrammarAppProtocol() {
		return grammarAppProtocol;
	}

	private void setGrammarAppProtocol(Grammars grammarAppProtocol) {
		this.grammarAppProtocol = grammarAppProtocol;
	}

	private Grammars getGrammarMsgDef() {
		return grammarMsgDef;
	}

	private void setGrammarMsgDef(Grammars grammarMsgDef) {
		this.grammarMsgDef = grammarMsgDef;
	}
	

	public Grammars getGrammarXMLDSig() {
		return grammarXMLDSig;
	}

	public void setGrammarXMLDSig(Grammars grammarXMLDSig) {
		this.grammarXMLDSig = grammarXMLDSig;
	}

	public EXIFactory getExiFactory() {
		return exiFactory;
	}

	private void setExiFactory(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
	}

	private GrammarFactory getGrammarFactory() {
		return grammarFactory;
	}

	private void setGrammarFactory(GrammarFactory grammarFactory) {
		this.grammarFactory = grammarFactory;
	}
	
	@Override
	public void setFragment(boolean useFragmentGrammar) {
		getExiFactory().setFragment(useFragmentGrammar);
	}
}
