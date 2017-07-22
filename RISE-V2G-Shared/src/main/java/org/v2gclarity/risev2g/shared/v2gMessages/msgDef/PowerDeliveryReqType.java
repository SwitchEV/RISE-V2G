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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PowerDeliveryReqType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PowerDeliveryReqType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="ChargeProgress" type="{urn:iso:15118:2:2013:MsgDataTypes}chargeProgressType"/>
 *         &lt;element name="SAScheduleTupleID" type="{urn:iso:15118:2:2013:MsgDataTypes}SAIDType"/>
 *         &lt;element name="ChargingProfile" type="{urn:iso:15118:2:2013:MsgDataTypes}ChargingProfileType" minOccurs="0"/>
 *         &lt;element ref="{urn:iso:15118:2:2013:MsgDataTypes}EVPowerDeliveryParameter" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PowerDeliveryReqType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "chargeProgress",
    "saScheduleTupleID",
    "chargingProfile",
    "evPowerDeliveryParameter"
})
public class PowerDeliveryReqType
    extends BodyBaseType
{

    @XmlElement(name = "ChargeProgress", required = true)
    @XmlSchemaType(name = "string")
    protected ChargeProgressType chargeProgress;
    @XmlElement(name = "SAScheduleTupleID")
    @XmlSchemaType(name = "unsignedByte")
    protected short saScheduleTupleID;
    @XmlElement(name = "ChargingProfile")
    protected ChargingProfileType chargingProfile;
    @XmlElementRef(name = "EVPowerDeliveryParameter", namespace = "urn:iso:15118:2:2013:MsgDataTypes", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends EVPowerDeliveryParameterType> evPowerDeliveryParameter;

    /**
     * Ruft den Wert der chargeProgress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargeProgressType }
     *     
     */
    public ChargeProgressType getChargeProgress() {
        return chargeProgress;
    }

    /**
     * Legt den Wert der chargeProgress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeProgressType }
     *     
     */
    public void setChargeProgress(ChargeProgressType value) {
        this.chargeProgress = value;
    }

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
     * Ruft den Wert der chargingProfile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ChargingProfileType }
     *     
     */
    public ChargingProfileType getChargingProfile() {
        return chargingProfile;
    }

    /**
     * Legt den Wert der chargingProfile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargingProfileType }
     *     
     */
    public void setChargingProfile(ChargingProfileType value) {
        this.chargingProfile = value;
    }

    /**
     * Ruft den Wert der evPowerDeliveryParameter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DCEVPowerDeliveryParameterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EVPowerDeliveryParameterType }{@code >}
     *     
     */
    public JAXBElement<? extends EVPowerDeliveryParameterType> getEVPowerDeliveryParameter() {
        return evPowerDeliveryParameter;
    }

    /**
     * Legt den Wert der evPowerDeliveryParameter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DCEVPowerDeliveryParameterType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EVPowerDeliveryParameterType }{@code >}
     *     
     */
    public void setEVPowerDeliveryParameter(JAXBElement<? extends EVPowerDeliveryParameterType> value) {
        this.evPowerDeliveryParameter = value;
    }

}
