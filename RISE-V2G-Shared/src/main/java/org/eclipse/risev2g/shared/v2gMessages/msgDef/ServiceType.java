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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ServiceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ServiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceID" type="{urn:iso:15118:2:2013:MsgDataTypes}serviceIDType"/>
 *         &lt;element name="ServiceName" type="{urn:iso:15118:2:2013:MsgDataTypes}serviceNameType" minOccurs="0"/>
 *         &lt;element name="ServiceCategory" type="{urn:iso:15118:2:2013:MsgDataTypes}serviceCategoryType"/>
 *         &lt;element name="ServiceScope" type="{urn:iso:15118:2:2013:MsgDataTypes}serviceScopeType" minOccurs="0"/>
 *         &lt;element name="FreeService" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceType", propOrder = {
    "serviceID",
    "serviceName",
    "serviceCategory",
    "serviceScope",
    "freeService"
})
@XmlSeeAlso({
    ChargeServiceType.class
})
public class ServiceType {

    @XmlElement(name = "ServiceID")
    @XmlSchemaType(name = "unsignedShort")
    protected int serviceID;
    @XmlElement(name = "ServiceName")
    protected String serviceName;
    @XmlElement(name = "ServiceCategory", required = true)
    @XmlSchemaType(name = "string")
    protected ServiceCategoryType serviceCategory;
    @XmlElement(name = "ServiceScope")
    protected String serviceScope;
    @XmlElement(name = "FreeService")
    protected boolean freeService;

    /**
     * Ruft den Wert der serviceID-Eigenschaft ab.
     * 
     */
    public int getServiceID() {
        return serviceID;
    }

    /**
     * Legt den Wert der serviceID-Eigenschaft fest.
     * 
     */
    public void setServiceID(int value) {
        this.serviceID = value;
    }

    /**
     * Ruft den Wert der serviceName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Legt den Wert der serviceName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Ruft den Wert der serviceCategory-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCategoryType }
     *     
     */
    public ServiceCategoryType getServiceCategory() {
        return serviceCategory;
    }

    /**
     * Legt den Wert der serviceCategory-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCategoryType }
     *     
     */
    public void setServiceCategory(ServiceCategoryType value) {
        this.serviceCategory = value;
    }

    /**
     * Ruft den Wert der serviceScope-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceScope() {
        return serviceScope;
    }

    /**
     * Legt den Wert der serviceScope-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceScope(String value) {
        this.serviceScope = value;
    }

    /**
     * Ruft den Wert der freeService-Eigenschaft ab.
     * 
     */
    public boolean isFreeService() {
        return freeService;
    }

    /**
     * Legt den Wert der freeService-Eigenschaft fest.
     * 
     */
    public void setFreeService(boolean value) {
        this.freeService = value;
    }

}
