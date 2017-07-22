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


package org.v2gclarity.risev2g.shared.v2gMessages.msgDef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DC_EVErrorCodeType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DC_EVErrorCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NO_ERROR"/>
 *     &lt;enumeration value="FAILED_RESSTemperatureInhibit"/>
 *     &lt;enumeration value="FAILED_EVShiftPosition"/>
 *     &lt;enumeration value="FAILED_ChargerConnectorLockFault"/>
 *     &lt;enumeration value="FAILED_EVRESSMalfunction"/>
 *     &lt;enumeration value="FAILED_ChargingCurrentdifferential"/>
 *     &lt;enumeration value="FAILED_ChargingVoltageOutOfRange"/>
 *     &lt;enumeration value="Reserved_A"/>
 *     &lt;enumeration value="Reserved_B"/>
 *     &lt;enumeration value="Reserved_C"/>
 *     &lt;enumeration value="FAILED_ChargingSystemIncompatibility"/>
 *     &lt;enumeration value="NoData"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DC_EVErrorCodeType")
@XmlEnum
public enum DCEVErrorCodeType {

    NO_ERROR("NO_ERROR"),
    @XmlEnumValue("FAILED_RESSTemperatureInhibit")
    FAILED_RESS_TEMPERATURE_INHIBIT("FAILED_RESSTemperatureInhibit"),
    @XmlEnumValue("FAILED_EVShiftPosition")
    FAILED_EV_SHIFT_POSITION("FAILED_EVShiftPosition"),
    @XmlEnumValue("FAILED_ChargerConnectorLockFault")
    FAILED_CHARGER_CONNECTOR_LOCK_FAULT("FAILED_ChargerConnectorLockFault"),
    @XmlEnumValue("FAILED_EVRESSMalfunction")
    FAILED_EVRESS_MALFUNCTION("FAILED_EVRESSMalfunction"),
    @XmlEnumValue("FAILED_ChargingCurrentdifferential")
    FAILED_CHARGING_CURRENTDIFFERENTIAL("FAILED_ChargingCurrentdifferential"),
    @XmlEnumValue("FAILED_ChargingVoltageOutOfRange")
    FAILED_CHARGING_VOLTAGE_OUT_OF_RANGE("FAILED_ChargingVoltageOutOfRange"),
    @XmlEnumValue("Reserved_A")
    RESERVED_A("Reserved_A"),
    @XmlEnumValue("Reserved_B")
    RESERVED_B("Reserved_B"),
    @XmlEnumValue("Reserved_C")
    RESERVED_C("Reserved_C"),
    @XmlEnumValue("FAILED_ChargingSystemIncompatibility")
    FAILED_CHARGING_SYSTEM_INCOMPATIBILITY("FAILED_ChargingSystemIncompatibility"),
    @XmlEnumValue("NoData")
    NO_DATA("NoData");
    private final String value;

    DCEVErrorCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DCEVErrorCodeType fromValue(String v) {
        for (DCEVErrorCodeType c: DCEVErrorCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
