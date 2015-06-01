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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DC_EVSEStatusType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DC_EVSEStatusType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgDataTypes}EVSEStatusType">
 *       &lt;sequence>
 *         &lt;element name="EVSEIsolationStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}isolationLevelType" minOccurs="0"/>
 *         &lt;element name="EVSEStatusCode" type="{urn:iso:15118:2:2013:MsgDataTypes}DC_EVSEStatusCodeType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DC_EVSEStatusType", propOrder = {
    "evseIsolationStatus",
    "evseStatusCode"
})
public class DCEVSEStatusType
    extends EVSEStatusType
{

    @XmlElement(name = "EVSEIsolationStatus")
    @XmlSchemaType(name = "string")
    protected IsolationLevelType evseIsolationStatus;
    @XmlElement(name = "EVSEStatusCode", required = true)
    @XmlSchemaType(name = "string")
    protected DCEVSEStatusCodeType evseStatusCode;

    /**
     * Ruft den Wert der evseIsolationStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IsolationLevelType }
     *     
     */
    public IsolationLevelType getEVSEIsolationStatus() {
        return evseIsolationStatus;
    }

    /**
     * Legt den Wert der evseIsolationStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IsolationLevelType }
     *     
     */
    public void setEVSEIsolationStatus(IsolationLevelType value) {
        this.evseIsolationStatus = value;
    }

    /**
     * Ruft den Wert der evseStatusCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DCEVSEStatusCodeType }
     *     
     */
    public DCEVSEStatusCodeType getEVSEStatusCode() {
        return evseStatusCode;
    }

    /**
     * Legt den Wert der evseStatusCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DCEVSEStatusCodeType }
     *     
     */
    public void setEVSEStatusCode(DCEVSEStatusCodeType value) {
        this.evseStatusCode = value;
    }

}
