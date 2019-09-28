/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.10.07 um 04:55:05 PM CEST 
//


package com.v2gclarity.risev2g.shared.v2gMessages.msgDef;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ChargeParameterDiscoveryReqType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ChargeParameterDiscoveryReqType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="MaxEntriesSAScheduleTuple" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" minOccurs="0"/>
 *         &lt;element name="RequestedEnergyTransferMode" type="{urn:iso:15118:2:2013:MsgDataTypes}EnergyTransferModeType"/>
 *         &lt;element ref="{urn:iso:15118:2:2013:MsgDataTypes}EVChargeParameter"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeParameterDiscoveryReqType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "maxEntriesSAScheduleTuple",
    "requestedEnergyTransferMode",
    "evChargeParameter"
})
public class ChargeParameterDiscoveryReqType
    extends BodyBaseType
{

    @XmlElement(name = "MaxEntriesSAScheduleTuple")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer maxEntriesSAScheduleTuple;
    @XmlElement(name = "RequestedEnergyTransferMode", required = true)
    @XmlSchemaType(name = "string")
    protected EnergyTransferModeType requestedEnergyTransferMode;
    @XmlElementRef(name = "EVChargeParameter", namespace = "urn:iso:15118:2:2013:MsgDataTypes", type = JAXBElement.class)
    protected JAXBElement<? extends EVChargeParameterType> evChargeParameter;

    /**
     * Ruft den Wert der maxEntriesSAScheduleTuple-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxEntriesSAScheduleTuple() {
        return maxEntriesSAScheduleTuple;
    }

    /**
     * Legt den Wert der maxEntriesSAScheduleTuple-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxEntriesSAScheduleTuple(Integer value) {
        this.maxEntriesSAScheduleTuple = value;
    }

    /**
     * Ruft den Wert der requestedEnergyTransferMode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EnergyTransferModeType }
     *     
     */
    public EnergyTransferModeType getRequestedEnergyTransferMode() {
        return requestedEnergyTransferMode;
    }

    /**
     * Legt den Wert der requestedEnergyTransferMode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EnergyTransferModeType }
     *     
     */
    public void setRequestedEnergyTransferMode(EnergyTransferModeType value) {
        this.requestedEnergyTransferMode = value;
    }

    /**
     * Ruft den Wert der evChargeParameter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EVChargeParameterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DCEVChargeParameterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ACEVChargeParameterType }{@code >}
     *     
     */
    public JAXBElement<? extends EVChargeParameterType> getEVChargeParameter() {
        return evChargeParameter;
    }

    /**
     * Legt den Wert der evChargeParameter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EVChargeParameterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DCEVChargeParameterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ACEVChargeParameterType }{@code >}
     *     
     */
    public void setEVChargeParameter(JAXBElement<? extends EVChargeParameterType> value) {
        this.evChargeParameter = value;
    }

}
