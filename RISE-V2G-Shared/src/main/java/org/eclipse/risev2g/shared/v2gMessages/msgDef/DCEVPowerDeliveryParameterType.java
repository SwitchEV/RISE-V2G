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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DC_EVPowerDeliveryParameterType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DC_EVPowerDeliveryParameterType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgDataTypes}EVPowerDeliveryParameterType">
 *       &lt;sequence>
 *         &lt;element name="DC_EVStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}DC_EVStatusType"/>
 *         &lt;element name="BulkChargingComplete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ChargingComplete" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DC_EVPowerDeliveryParameterType", propOrder = {
    "dcevStatus",
    "bulkChargingComplete",
    "chargingComplete"
})
public class DCEVPowerDeliveryParameterType
    extends EVPowerDeliveryParameterType
{

    @XmlElement(name = "DC_EVStatus", required = true)
    protected DCEVStatusType dcevStatus;
    @XmlElement(name = "BulkChargingComplete")
    protected Boolean bulkChargingComplete;
    @XmlElement(name = "ChargingComplete")
    protected boolean chargingComplete;

    /**
     * Ruft den Wert der dcevStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DCEVStatusType }
     *     
     */
    public DCEVStatusType getDCEVStatus() {
        return dcevStatus;
    }

    /**
     * Legt den Wert der dcevStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DCEVStatusType }
     *     
     */
    public void setDCEVStatus(DCEVStatusType value) {
        this.dcevStatus = value;
    }

    /**
     * Ruft den Wert der bulkChargingComplete-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBulkChargingComplete() {
        return bulkChargingComplete;
    }

    /**
     * Legt den Wert der bulkChargingComplete-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBulkChargingComplete(Boolean value) {
        this.bulkChargingComplete = value;
    }

    /**
     * Ruft den Wert der chargingComplete-Eigenschaft ab.
     * 
     */
    public boolean isChargingComplete() {
        return chargingComplete;
    }

    /**
     * Legt den Wert der chargingComplete-Eigenschaft fest.
     * 
     */
    public void setChargingComplete(boolean value) {
        this.chargingComplete = value;
    }

}
