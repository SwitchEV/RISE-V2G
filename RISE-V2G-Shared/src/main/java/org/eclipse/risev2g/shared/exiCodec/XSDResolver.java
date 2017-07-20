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
package org.eclipse.risev2g.shared.exiCodec;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.openexi.scomp.EXISchemaFactory;

/**
 * This class is needed to access all linked XSD files via an InputStream. Accessing XSD files
 * via an InputStream is needed when using them inside a jar file. Normally, if a XSD file imports  
 * other XSD files, an exception will be thrown because those files cannot be found (since there
 * is no file system to search at, just an InputStream). With this XSDResolver, every imported
 * XSD file must be added together with its InputStream to a Map of entities the GrammarLoader (EXIficient)
 * or Transmogrifier (OpenExi) can use for lookup. 
 */
public class XSDResolver implements XMLEntityResolver {

	private static final XSDResolver instance = new XSDResolver();
	private HashMap<String, XMLInputSource> xmlInputSourceEntities;
	
	private XSDResolver() {
		EXISchemaFactory exiSchemaFactory = new EXISchemaFactory();
		EXISchemaFactoryExceptionHandler esfe = new EXISchemaFactoryExceptionHandler();
		exiSchemaFactory.setCompilerErrorHandler(esfe);
		
		InputStream isV2GCIMsgDef = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_MSG_DEF.toString());
		XMLInputSource xmlISV2GCIMsgDef = new XMLInputSource(null, null, null, isV2GCIMsgDef, null);
		
		InputStream isV2GCIMsgHeader = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_MSG_HEADER.toString());
		XMLInputSource xmlISV2GCIMsgHeader = new XMLInputSource(null, null, null, isV2GCIMsgHeader, null);
		
		InputStream isV2GCIMsgBody = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_MSG_BODY.toString());
		XMLInputSource xmlISV2GCIMsgBody = new XMLInputSource(null, null, null, isV2GCIMsgBody, null);
		
		InputStream isV2GCIMsgDataTypes = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_MSG_DATA_TYPES.toString());
		XMLInputSource xmlISV2GCIMsgDataTypes = new XMLInputSource(null, null, null, isV2GCIMsgDataTypes, null);
		
		InputStream isXMLDSig = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_XMLDSIG.toString());
		XMLInputSource xmlISXMLDSig = new XMLInputSource(null, null, null, isXMLDSig, null);
		
		setEntity("V2G_CI_MsgDef.xsd", xmlISV2GCIMsgDef);
		setEntity("V2G_CI_MsgBody.xsd", xmlISV2GCIMsgBody);
		setEntity("V2G_CI_MsgHeader.xsd", xmlISV2GCIMsgHeader);
		setEntity("V2G_CI_MsgDataTypes.xsd", xmlISV2GCIMsgDataTypes);
		setEntity("xmldsig-core-schema.xsd", xmlISXMLDSig);
	}
	
	
	public static XSDResolver getInstance() {
		return instance;
	}
	
	
	public void setEntity(String literalSystemId, XMLInputSource xmlInput) {
		if (xmlInputSourceEntities == null) {
			xmlInputSourceEntities = new HashMap<String, XMLInputSource>();
		}
		
		xmlInputSourceEntities.put(literalSystemId, xmlInput);
	}


	public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) 
			throws XNIException, IOException {
		String literalSystemId = resourceIdentifier.getLiteralSystemId(); 
		
		if (xmlInputSourceEntities != null && xmlInputSourceEntities.containsKey(literalSystemId)) {
			return xmlInputSourceEntities.get(literalSystemId);
		}

		return null;
	}
}
