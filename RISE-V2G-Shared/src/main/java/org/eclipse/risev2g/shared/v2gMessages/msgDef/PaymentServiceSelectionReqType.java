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
