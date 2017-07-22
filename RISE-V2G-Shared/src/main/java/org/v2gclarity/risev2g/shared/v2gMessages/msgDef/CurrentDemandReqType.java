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
 * <p>Java-Klasse für CurrentDemandReqType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CurrentDemandReqType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="DC_EVStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}DC_EVStatusType"/>
 *         &lt;element name="EVTargetCurrent" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *         &lt;element name="EVMaximumVoltageLimit" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="EVMaximumCurrentLimit" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="EVMaximumPowerLimit" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="BulkChargingComplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ChargingComplete" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="RemainingTimeToFullSoC" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="RemainingTimeToBulkSoC" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="EVTargetVoltage" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrentDemandReqType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "dcevStatus",
    "evTargetCurrent",
    "evMaximumVoltageLimit",
    "evMaximumCurrentLimit",
    "evMaximumPowerLimit",
    "bulkChargingComplete",
    "chargingComplete",
    "remainingTimeToFullSoC",
    "remainingTimeToBulkSoC",
    "evTargetVoltage"
})
public class CurrentDemandReqType
    extends BodyBaseType
{

    @XmlElement(name = "DC_EVStatus", required = true)
    protected DCEVStatusType dcevStatus;
    @XmlElement(name = "EVTargetCurrent", required = true)
    protected PhysicalValueType evTargetCurrent;
    @XmlElement(name = "EVMaximumVoltageLimit")
    protected PhysicalValueType evMaximumVoltageLimit;
    @XmlElement(name = "EVMaximumCurrentLimit")
    protected PhysicalValueType evMaximumCurrentLimit;
    @XmlElement(name = "EVMaximumPowerLimit")
    protected PhysicalValueType evMaximumPowerLimit;
    @XmlElement(name = "BulkChargingComplete")
    protected Boolean bulkChargingComplete;
    @XmlElement(name = "ChargingComplete")
    protected boolean chargingComplete;
    @XmlElement(name = "RemainingTimeToFullSoC")
    protected PhysicalValueType remainingTimeToFullSoC;
    @XmlElement(name = "RemainingTimeToBulkSoC")
    protected PhysicalValueType remainingTimeToBulkSoC;
    @XmlElement(name = "EVTargetVoltage", required = true)
    protected PhysicalValueType evTargetVoltage;

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
     * Ruft den Wert der evTargetCurrent-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVTargetCurrent() {
        return evTargetCurrent;
    }

    /**
     * Legt den Wert der evTargetCurrent-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVTargetCurrent(PhysicalValueType value) {
        this.evTargetCurrent = value;
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
     * Ruft den Wert der bulkChargingComplete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBulkChargingComplete() {
        return bulkChargingComplete;
    }

    /**
     * Legt den Wert der bulkChargingComplete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBulkChargingComplete(Boolean value) {
        this.bulkChargingComplete = value;
    }

    /**
     * Ruft den Wert der chargingComplete-Eigenschaft ab.
     * 
     */
    public boolean isChargingComplete() {
        return chargingComplete;
    }

    /**
     * Legt den Wert der chargingComplete-Eigenschaft fest.
     * 
     */
    public void setChargingComplete(boolean value) {
        this.chargingComplete = value;
    }

    /**
     * Ruft den Wert der remainingTimeToFullSoC-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getRemainingTimeToFullSoC() {
        return remainingTimeToFullSoC;
    }

    /**
     * Legt den Wert der remainingTimeToFullSoC-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setRemainingTimeToFullSoC(PhysicalValueType value) {
        this.remainingTimeToFullSoC = value;
    }

    /**
     * Ruft den Wert der remainingTimeToBulkSoC-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getRemainingTimeToBulkSoC() {
        return remainingTimeToBulkSoC;
    }

    /**
     * Legt den Wert der remainingTimeToBulkSoC-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setRemainingTimeToBulkSoC(PhysicalValueType value) {
        this.remainingTimeToBulkSoC = value;
    }

    /**
     * Ruft den Wert der evTargetVoltage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVTargetVoltage() {
        return evTargetVoltage;
    }

    /**
     * Legt den Wert der evTargetVoltage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVTargetVoltage(PhysicalValueType value) {
        this.evTargetVoltage = value;
    }

}
