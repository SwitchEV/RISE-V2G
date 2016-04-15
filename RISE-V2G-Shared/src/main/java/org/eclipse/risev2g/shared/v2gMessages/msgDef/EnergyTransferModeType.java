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
