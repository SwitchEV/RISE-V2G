/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.10.07 um 04:55:05 PM CEST 
//


package com.v2gclarity.risev2g.shared.v2gMessages.msgDef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für EnergyTransferModeType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="EnergyTransferModeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AC_single_phase_core"/>
 *     &lt;enumeration value="AC_three_phase_core"/>
 *     &lt;enumeration value="DC_core"/>
 *     &lt;enumeration value="DC_extended"/>
 *     &lt;enumeration value="DC_combo_core"/>
 *     &lt;enumeration value="DC_unique"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EnergyTransferModeType")
@XmlEnum
public enum EnergyTransferModeType {

    @XmlEnumValue("AC_single_phase_core")
    AC_SINGLE_PHASE_CORE("AC_single_phase_core"),
    @XmlEnumValue("AC_three_phase_core")
    AC_THREE_PHASE_CORE("AC_three_phase_core"),
    @XmlEnumValue("DC_core")
    DC_CORE("DC_core"),
    @XmlEnumValue("DC_extended")
    DC_EXTENDED("DC_extended"),
    @XmlEnumValue("DC_combo_core")
    DC_COMBO_CORE("DC_combo_core"),
    @XmlEnumValue("DC_unique")
    DC_UNIQUE("DC_unique");
    private final String value;

    EnergyTransferModeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnergyTransferModeType fromValue(String v) {
        for (EnergyTransferModeType c: EnergyTransferModeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
