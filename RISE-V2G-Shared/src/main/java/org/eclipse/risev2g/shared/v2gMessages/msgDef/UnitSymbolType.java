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
// Generiert: 2014.10.07 um 04:55:05 PM CEST 
//


package org.eclipse.risev2g.shared.v2gMessages.msgDef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für unitSymbolType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="unitSymbolType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="h"/>
 *     &lt;enumeration value="m"/>
 *     &lt;enumeration value="s"/>
 *     &lt;enumeration value="A"/>
 *     &lt;enumeration value="V"/>
 *     &lt;enumeration value="W"/>
 *     &lt;enumeration value="Wh"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "unitSymbolType")
@XmlEnum
public enum UnitSymbolType {


    /**
     * Time in hours
     * 
     */
    @XmlEnumValue("h")
    H("h"),

    /**
     * Time in minutes
     * 
     */
    @XmlEnumValue("m")
    M("m"),

    /**
     * Time in seconds
     * 
     */
    @XmlEnumValue("s")
    S("s"),

    /**
     * Current in Ampere
     * 
     */
    A("A"),

    /**
     * Voltage in Volt
     * 
     */
    V("V"),

    /**
     * Active power in Watt
     * 
     */
    W("W"),

    /**
     * Real energy in Watt hours
     * 
     */
    @XmlEnumValue("Wh")
    WH("Wh");
    private final String value;

    UnitSymbolType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnitSymbolType fromValue(String v) {
        for (UnitSymbolType c: UnitSymbolType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
