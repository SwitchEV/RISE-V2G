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
 * <p>Java-Klasse für ServiceDiscoveryResType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ServiceDiscoveryResType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="ResponseCode" type="{urn:iso:15118:2:2013:MsgDataTypes}responseCodeType"/>
 *         &lt;element name="PaymentOptionList" type="{urn:iso:15118:2:2013:MsgDataTypes}PaymentOptionListType"/>
 *         &lt;element name="ChargeService" type="{urn:iso:15118:2:2013:MsgDataTypes}ChargeServiceType"/>
 *         &lt;element name="ServiceList" type="{urn:iso:15118:2:2013:MsgDataTypes}ServiceListType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceDiscoveryResType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "responseCode",
    "paymentOptionList",
    "chargeService",
    "serviceList"
})
public class ServiceDiscoveryResType
    extends BodyBaseType
{

    @XmlElement(name = "ResponseCode", required = true)
    @XmlSchemaType(name = "string")
    protected ResponseCodeType responseCode;
    @XmlElement(name = "PaymentOptionList", required = true)
    protected PaymentOptionListType paymentOptionList;
    @XmlElement(name = "ChargeService", required = true)
    protected ChargeServiceType chargeService;
    @XmlElement(name = "ServiceList")
    protected ServiceListType serviceList;

    /**
     * Ruft den Wert der responseCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ResponseCodeType }
     *     
     */
    public ResponseCodeType getResponseCode() {
        return responseCode;
    }

    /**
     * Legt den Wert der responseCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseCodeType }
     *     
     */
    public void setResponseCode(ResponseCodeType value) {
        this.responseCode = value;
    }

    /**
     * Ruft den Wert der paymentOptionList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentOptionListType }
     *     
     */
    public PaymentOptionListType getPaymentOptionList() {
        return paymentOptionList;
    }

    /**
     * Legt den Wert der paymentOptionList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentOptionListType }
     *     
     */
    public void setPaymentOptionList(PaymentOptionListType value) {
        this.paymentOptionList = value;
    }

    /**
     * Ruft den Wert der chargeService-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargeServiceType }
     *     
     */
    public ChargeServiceType getChargeService() {
        return chargeService;
    }

    /**
     * Legt den Wert der chargeService-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeServiceType }
     *     
     */
    public void setChargeService(ChargeServiceType value) {
        this.chargeService = value;
    }

    /**
     * Ruft den Wert der serviceList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceListType }
     *     
     */
    public ServiceListType getServiceList() {
        return serviceList;
    }

    /**
     * Legt den Wert der serviceList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceListType }
     *     
     */
    public void setServiceList(ServiceListType value) {
        this.serviceList = value;
    }

}
