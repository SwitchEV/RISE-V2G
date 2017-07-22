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
