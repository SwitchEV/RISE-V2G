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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PhysicalValueType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PhysicalValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Multiplier" type="{urn:iso:15118:2:2013:MsgDataTypes}unitMultiplierType"/>
 *         &lt;element name="Unit" type="{urn:iso:15118:2:2013:MsgDataTypes}unitSymbolType"/>
 *         &lt;element name="Value" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhysicalValueType", propOrder = {
    "multiplier",
    "unit",
    "value"
})
public class PhysicalValueType {

    @XmlElement(name = "Multiplier")
    protected byte multiplier;
    @XmlElement(name = "Unit", required = true)
    @XmlSchemaType(name = "string")
    protected UnitSymbolType unit;
    @XmlElement(name = "Value")
    protected short value;

    /**
     * Ruft den Wert der multiplier-Eigenschaft ab.
     * 
     */
    public byte getMultiplier() {
        return multiplier;
    }

    /**
     * Legt den Wert der multiplier-Eigenschaft fest.
     * 
     */
    public void setMultiplier(byte value) {
        this.multiplier = value;
    }

    /**
     * Ruft den Wert der unit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UnitSymbolType }
     *     
     */
    public UnitSymbolType getUnit() {
        return unit;
    }

    /**
     * Legt den Wert der unit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UnitSymbolType }
     *     
     */
    public void setUnit(UnitSymbolType value) {
        this.unit = value;
    }

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     */
    public short getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     */
    public void setValue(short value) {
        this.value = value;
    }

}
