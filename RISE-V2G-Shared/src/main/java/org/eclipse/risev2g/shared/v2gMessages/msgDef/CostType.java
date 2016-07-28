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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CostType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CostType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="costKind" type="{urn:iso:15118:2:2013:MsgDataTypes}costKindType"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="amountMultiplier" type="{urn:iso:15118:2:2013:MsgDataTypes}unitMultiplierType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CostType", propOrder = {
    "costKind",
    "amount",
    "amountMultiplier"
})
public class CostType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected CostKindType costKind;
    @XmlSchemaType(name = "unsignedInt")
    protected long amount;
    protected Byte amountMultiplier;

    /**
     * Ruft den Wert der costKind-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CostKindType }
     *     
     */
    public CostKindType getCostKind() {
        return costKind;
    }

    /**
     * Legt den Wert der costKind-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CostKindType }
     *     
     */
    public void setCostKind(CostKindType value) {
        this.costKind = value;
    }

    /**
     * Ruft den Wert der amount-Eigenschaft ab.
     * 
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Legt den Wert der amount-Eigenschaft fest.
     * 
     */
    public void setAmount(long value) {
        this.amount = value;
    }

    /**
     * Ruft den Wert der amountMultiplier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getAmountMultiplier() {
        return amountMultiplier;
    }

    /**
     * Legt den Wert der amountMultiplier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setAmountMultiplier(Byte value) {
        this.amountMultiplier = value;
    }

}
