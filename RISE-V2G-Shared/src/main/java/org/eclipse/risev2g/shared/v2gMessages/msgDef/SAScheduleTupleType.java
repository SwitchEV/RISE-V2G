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
 * <p>Java-Klasse für SAScheduleTupleType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SAScheduleTupleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SAScheduleTupleID" type="{urn:iso:15118:2:2013:MsgDataTypes}SAIDType"/>
 *         &lt;element name="PMaxSchedule" type="{urn:iso:15118:2:2013:MsgDataTypes}PMaxScheduleType"/>
 *         &lt;element name="SalesTariff" type="{urn:iso:15118:2:2013:MsgDataTypes}SalesTariffType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SAScheduleTupleType", propOrder = {
    "saScheduleTupleID",
    "pMaxSchedule",
    "salesTariff"
})
public class SAScheduleTupleType {

    @XmlElement(name = "SAScheduleTupleID")
    @XmlSchemaType(name = "unsignedByte")
    protected short saScheduleTupleID;
    @XmlElement(name = "PMaxSchedule", required = true)
    protected PMaxScheduleType pMaxSchedule;
    @XmlElement(name = "SalesTariff")
    protected SalesTariffType salesTariff;

    /**
     * Ruft den Wert der saScheduleTupleID-Eigenschaft ab.
     * 
     */
    public short getSAScheduleTupleID() {
        return saScheduleTupleID;
    }

    /**
     * Legt den Wert der saScheduleTupleID-Eigenschaft fest.
     * 
     */
    public void setSAScheduleTupleID(short value) {
        this.saScheduleTupleID = value;
    }

    /**
     * Ruft den Wert der pMaxSchedule-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PMaxScheduleType }
     *     
     */
    public PMaxScheduleType getPMaxSchedule() {
        return pMaxSchedule;
    }

    /**
     * Legt den Wert der pMaxSchedule-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PMaxScheduleType }
     *     
     */
    public void setPMaxSchedule(PMaxScheduleType value) {
        this.pMaxSchedule = value;
    }

    /**
     * Ruft den Wert der salesTariff-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SalesTariffType }
     *     
     */
    public SalesTariffType getSalesTariff() {
        return salesTariff;
    }

    /**
     * Legt den Wert der salesTariff-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SalesTariffType }
     *     
     */
    public void setSalesTariff(SalesTariffType value) {
        this.salesTariff = value;
    }

}
