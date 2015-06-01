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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für EVSEStatusType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EVSEStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NotificationMaxDelay" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="EVSENotification" type="{urn:iso:15118:2:2013:MsgDataTypes}EVSENotificationType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EVSEStatusType", propOrder = {
    "notificationMaxDelay",
    "evseNotification"
})
@XmlSeeAlso({
    DCEVSEStatusType.class,
    ACEVSEStatusType.class
})
public abstract class EVSEStatusType {

    @XmlElement(name = "NotificationMaxDelay")
    @XmlSchemaType(name = "unsignedShort")
    protected int notificationMaxDelay;
    @XmlElement(name = "EVSENotification", required = true)
    @XmlSchemaType(name = "string")
    protected EVSENotificationType evseNotification;

    /**
     * Ruft den Wert der notificationMaxDelay-Eigenschaft ab.
     * 
     */
    public int getNotificationMaxDelay() {
        return notificationMaxDelay;
    }

    /**
     * Legt den Wert der notificationMaxDelay-Eigenschaft fest.
     * 
     */
    public void setNotificationMaxDelay(int value) {
        this.notificationMaxDelay = value;
    }

    /**
     * Ruft den Wert der evseNotification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EVSENotificationType }
     *     
     */
    public EVSENotificationType getEVSENotification() {
        return evseNotification;
    }

    /**
     * Legt den Wert der evseNotification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EVSENotificationType }
     *     
     */
    public void setEVSENotification(EVSENotificationType value) {
        this.evseNotification = value;
    }

}
