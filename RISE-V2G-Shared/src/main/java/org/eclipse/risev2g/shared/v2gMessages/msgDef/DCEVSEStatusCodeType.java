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
