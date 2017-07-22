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
