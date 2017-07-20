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


package org.eclipse.risev2g.shared.v2gMessages.msgDef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PaymentServiceSelectionReqType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentServiceSelectionReqType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="SelectedPaymentOption" type="{urn:iso:15118:2:2013:MsgDataTypes}paymentOptionType"/>
 *         &lt;element name="SelectedServiceList" type="{urn:iso:15118:2:2013:MsgDataTypes}SelectedServiceListType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentServiceSelectionReqType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "selectedPaymentOption",
    "selectedServiceList"
})
public class PaymentServiceSelectionReqType
    extends BodyBaseType
{

    @XmlElement(name = "SelectedPaymentOption", required = true)
    @XmlSchemaType(name = "string")
    protected PaymentOptionType selectedPaymentOption;
    @XmlElement(name = "SelectedServiceList", required = true)
    protected SelectedServiceListType selectedServiceList;

    /**
     * Ruft den Wert der selectedPaymentOption-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentOptionType }
     *     
     */
    public PaymentOptionType getSelectedPaymentOption() {
        return selectedPaymentOption;
    }

    /**
     * Legt den Wert der selectedPaymentOption-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentOptionType }
     *     
     */
    public void setSelectedPaymentOption(PaymentOptionType value) {
        this.selectedPaymentOption = value;
    }

    /**
     * Ruft den Wert der selectedServiceList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SelectedServiceListType }
     *     
     */
    public SelectedServiceListType getSelectedServiceList() {
        return selectedServiceList;
    }

    /**
     * Legt den Wert der selectedServiceList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectedServiceListType }
     *     
     */
    public void setSelectedServiceList(SelectedServiceListType value) {
        this.selectedServiceList = value;
    }

}
