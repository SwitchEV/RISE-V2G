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
// Generiert: 2014.10.07 um 04:55:05 PM CEST 
//


package org.eclipse.risev2g.shared.v2gMessages.msgDef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PreChargeReqType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PreChargeReqType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="DC_EVStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}DC_EVStatusType"/>
 *         &lt;element name="EVTargetVoltage" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *         &lt;element name="EVTargetCurrent" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreChargeReqType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "dcevStatus",
    "evTargetVoltage",
    "evTargetCurrent"
})
public class PreChargeReqType
    extends BodyBaseType
{

    @XmlElement(name = "DC_EVStatus", required = true)
    protected DCEVStatusType dcevStatus;
    @XmlElement(name = "EVTargetVoltage", required = true)
    protected PhysicalValueType evTargetVoltage;
    @XmlElement(name = "EVTargetCurrent", required = true)
    protected PhysicalValueType evTargetCurrent;

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

}
