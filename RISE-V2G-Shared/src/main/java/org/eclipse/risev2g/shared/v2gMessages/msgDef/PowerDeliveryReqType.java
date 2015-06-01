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
