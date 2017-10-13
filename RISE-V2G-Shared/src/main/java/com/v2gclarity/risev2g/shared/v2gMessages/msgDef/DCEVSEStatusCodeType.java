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
 * <p>Java-Klasse für DC_EVSEStatusCodeType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DC_EVSEStatusCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EVSE_NotReady"/>
 *     &lt;enumeration value="EVSE_Ready"/>
 *     &lt;enumeration value="EVSE_Shutdown"/>
 *     &lt;enumeration value="EVSE_UtilityInterruptEvent"/>
 *     &lt;enumeration value="EVSE_IsolationMonitoringActive"/>
 *     &lt;enumeration value="EVSE_EmergencyShutdown"/>
 *     &lt;enumeration value="EVSE_Malfunction"/>
 *     &lt;enumeration value="Reserved_8"/>
 *     &lt;enumeration value="Reserved_9"/>
 *     &lt;enumeration value="Reserved_A"/>
 *     &lt;enumeration value="Reserved_B"/>
 *     &lt;enumeration value="Reserved_C"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DC_EVSEStatusCodeType")
@XmlEnum
public enum DCEVSEStatusCodeType {

    @XmlEnumValue("EVSE_NotReady")
    EVSE_NOT_READY("EVSE_NotReady"),
    @XmlEnumValue("EVSE_Ready")
    EVSE_READY("EVSE_Ready"),
    @XmlEnumValue("EVSE_Shutdown")
    EVSE_SHUTDOWN("EVSE_Shutdown"),
    @XmlEnumValue("EVSE_UtilityInterruptEvent")
    EVSE_UTILITY_INTERRUPT_EVENT("EVSE_UtilityInterruptEvent"),
    @XmlEnumValue("EVSE_IsolationMonitoringActive")
    EVSE_ISOLATION_MONITORING_ACTIVE("EVSE_IsolationMonitoringActive"),
    @XmlEnumValue("EVSE_EmergencyShutdown")
    EVSE_EMERGENCY_SHUTDOWN("EVSE_EmergencyShutdown"),
    @XmlEnumValue("EVSE_Malfunction")
    EVSE_MALFUNCTION("EVSE_Malfunction"),
    @XmlEnumValue("Reserved_8")
    RESERVED_8("Reserved_8"),
    @XmlEnumValue("Reserved_9")
    RESERVED_9("Reserved_9"),
    @XmlEnumValue("Reserved_A")
    RESERVED_A("Reserved_A"),
    @XmlEnumValue("Reserved_B")
    RESERVED_B("Reserved_B"),
    @XmlEnumValue("Reserved_C")
    RESERVED_C("Reserved_C");
    private final String value;

    DCEVSEStatusCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DCEVSEStatusCodeType fromValue(String v) {
        for (DCEVSEStatusCodeType c: DCEVSEStatusCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
