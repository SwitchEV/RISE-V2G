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
//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.10.07 um 04:56:57 PM CEST 
//


package org.eclipse.risev2g.shared.v2gMessages.appProtocol;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the v2gMessages.appProtocol package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: v2gMessages.appProtocol
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SupportedAppProtocolRes }
     * 
     */
    public SupportedAppProtocolRes createSupportedAppProtocolRes() {
        return new SupportedAppProtocolRes();
    }

    /**
     * Create an instance of {@link SupportedAppProtocolReq }
     * 
     */
    public SupportedAppProtocolReq createSupportedAppProtocolReq() {
        return new SupportedAppProtocolReq();
    }

    /**
     * Create an instance of {@link AppProtocolType }
     * 
     */
    public AppProtocolType createAppProtocolType() {
        return new AppProtocolType();
    }

}
