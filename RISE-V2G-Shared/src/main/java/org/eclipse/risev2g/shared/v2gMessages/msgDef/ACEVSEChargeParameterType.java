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
 * <p>Java-Klasse für AC_EVSEChargeParameterType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AC_EVSEChargeParameterType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgDataTypes}EVSEChargeParameterType">
 *       &lt;sequence>
 *         &lt;element name="AC_EVSEStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}AC_EVSEStatusType"/>
 *         &lt;element name="EVSENominalVoltage" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *         &lt;element name="EVSEMaxCurrent" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AC_EVSEChargeParameterType", propOrder = {
    "acevseStatus",
    "evseNominalVoltage",
    "evseMaxCurrent"
})
public class ACEVSEChargeParameterType
    extends EVSEChargeParameterType
{

    @XmlElement(name = "AC_EVSEStatus", required = true)
    protected ACEVSEStatusType acevseStatus;
    @XmlElement(name = "EVSENominalVoltage", required = true)
    protected PhysicalValueType evseNominalVoltage;
    @XmlElement(name = "EVSEMaxCurrent", required = true)
    protected PhysicalValueType evseMaxCurrent;

    /**
     * Ruft den Wert der acevseStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ACEVSEStatusType }
     *     
     */
    public ACEVSEStatusType getACEVSEStatus() {
        return acevseStatus;
    }

    /**
     * Legt den Wert der acevseStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ACEVSEStatusType }
     *     
     */
    public void setACEVSEStatus(ACEVSEStatusType value) {
        this.acevseStatus = value;
    }

    /**
     * Ruft den Wert der evseNominalVoltage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVSENominalVoltage() {
        return evseNominalVoltage;
    }

    /**
     * Legt den Wert der evseNominalVoltage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVSENominalVoltage(PhysicalValueType value) {
        this.evseNominalVoltage = value;
    }

    /**
     * Ruft den Wert der evseMaxCurrent-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVSEMaxCurrent() {
        return evseMaxCurrent;
    }

    /**
     * Legt den Wert der evseMaxCurrent-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVSEMaxCurrent(PhysicalValueType value) {
        this.evseMaxCurrent = value;
    }

}
