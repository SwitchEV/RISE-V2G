/*******************************************************************************
 *  Copyright (c) 2015 Marc Mültin (Chargepartner GmbH).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Dr.-Ing. Marc Mültin (Chargepartner GmbH) - initial API and implementation and initial documentation
 *******************************************************************************/
//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.10.07 um 04:56:57 PM CEST 
//


package org.eclipse.risev2g.shared.v2gMessages.appProtocol;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für responseCodeType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="responseCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="OK_SuccessfulNegotiation"/>
 *     &lt;enumeration value="OK_SuccessfulNegotiationWithMinorDeviation"/>
 *     &lt;enumeration value="Failed_NoNegotiation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "responseCodeType")
@XmlEnum
public enum ResponseCodeType {

    @XmlEnumValue("OK_SuccessfulNegotiation")
    OK_SUCCESSFUL_NEGOTIATION("OK_SuccessfulNegotiation"),
    @XmlEnumValue("OK_SuccessfulNegotiationWithMinorDeviation")
    OK_SUCCESSFUL_NEGOTIATION_WITH_MINOR_DEVIATION("OK_SuccessfulNegotiationWithMinorDeviation"),
    @XmlEnumValue("Failed_NoNegotiation")
    FAILED_NO_NEGOTIATION("Failed_NoNegotiation");
    private final String value;

    ResponseCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ResponseCodeType fromValue(String v) {
        for (ResponseCodeType c: ResponseCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
