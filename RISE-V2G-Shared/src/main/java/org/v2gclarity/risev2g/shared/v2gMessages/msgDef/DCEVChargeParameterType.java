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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DC_EVChargeParameterType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DC_EVChargeParameterType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgDataTypes}EVChargeParameterType">
 *       &lt;sequence>
 *         &lt;element name="DC_EVStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}DC_EVStatusType"/>
 *         &lt;element name="EVMaximumCurrentLimit" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *         &lt;element name="EVMaximumPowerLimit" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="EVMaximumVoltageLimit" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *         &lt;element name="EVEnergyCapacity" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="EVEnergyRequest" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="FullSOC" type="{urn:iso:15118:2:2013:MsgDataTypes}percentValueType" minOccurs="0"/>
 *         &lt;element name="BulkSOC" type="{urn:iso:15118:2:2013:MsgDataTypes}percentValueType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DC_EVChargeParameterType", propOrder = {
    "dcevStatus",
    "evMaximumCurrentLimit",
    "evMaximumPowerLimit",
    "evMaximumVoltageLimit",
    "evEnergyCapacity",
    "evEnergyRequest",
    "fullSOC",
    "bulkSOC"
})
public class DCEVChargeParameterType
    extends EVChargeParameterType
{

    @XmlElement(name = "DC_EVStatus", required = true)
    protected DCEVStatusType dcevStatus;
    @XmlElement(name = "EVMaximumCurrentLimit", required = true)
    protected PhysicalValueType evMaximumCurrentLimit;
    @XmlElement(name = "EVMaximumPowerLimit")
    protected PhysicalValueType evMaximumPowerLimit;
    @XmlElement(name = "EVMaximumVoltageLimit", required = true)
    protected PhysicalValueType evMaximumVoltageLimit;
    @XmlElement(name = "EVEnergyCapacity")
    protected PhysicalValueType evEnergyCapacity;
    @XmlElement(name = "EVEnergyRequest")
    protected PhysicalValueType evEnergyRequest;
    @XmlElement(name = "FullSOC")
    protected Byte fullSOC;
    @XmlElement(name = "BulkSOC")
    protected Byte bulkSOC;

    /**
     * Ruft den Wert der dcevStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DCEVStatusType }
     *     
     */
    public DCEVStatusType getDCEVStatus() {
        return dcevStatus;
    }

    /**
     * Legt den Wert der dcevStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DCEVStatusType }
     *     
     */
    public void setDCEVStatus(DCEVStatusType value) {
        this.dcevStatus = value;
    }

    /**
     * Ruft den Wert der evMaximumCurrentLimit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVMaximumCurrentLimit() {
        return evMaximumCurrentLimit;
    }

    /**
     * Legt den Wert der evMaximumCurrentLimit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVMaximumCurrentLimit(PhysicalValueType value) {
        this.evMaximumCurrentLimit = value;
    }

    /**
     * Ruft den Wert der evMaximumPowerLimit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVMaximumPowerLimit() {
        return evMaximumPowerLimit;
    }

    /**
     * Legt den Wert der evMaximumPowerLimit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVMaximumPowerLimit(PhysicalValueType value) {
        this.evMaximumPowerLimit = value;
    }

    /**
     * Ruft den Wert der evMaximumVoltageLimit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVMaximumVoltageLimit() {
        return evMaximumVoltageLimit;
    }

    /**
     * Legt den Wert der evMaximumVoltageLimit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVMaximumVoltageLimit(PhysicalValueType value) {
        this.evMaximumVoltageLimit = value;
    }

    /**
     * Ruft den Wert der evEnergyCapacity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVEnergyCapacity() {
        return evEnergyCapacity;
    }

    /**
     * Legt den Wert der evEnergyCapacity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVEnergyCapacity(PhysicalValueType value) {
        this.evEnergyCapacity = value;
    }

    /**
     * Ruft den Wert der evEnergyRequest-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVEnergyRequest() {
        return evEnergyRequest;
    }

    /**
     * Legt den Wert der evEnergyRequest-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVEnergyRequest(PhysicalValueType value) {
        this.evEnergyRequest = value;
    }

    /**
     * Ruft den Wert der fullSOC-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getFullSOC() {
        return fullSOC;
    }

    /**
     * Legt den Wert der fullSOC-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setFullSOC(Byte value) {
        this.fullSOC = value;
    }

    /**
     * Ruft den Wert der bulkSOC-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getBulkSOC() {
        return bulkSOC;
    }

    /**
     * Legt den Wert der bulkSOC-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setBulkSOC(Byte value) {
        this.bulkSOC = value;
    }

}
