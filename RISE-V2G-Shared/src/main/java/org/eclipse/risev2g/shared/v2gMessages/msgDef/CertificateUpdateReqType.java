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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für CertificateUpdateReqType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CertificateUpdateReqType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="ContractSignatureCertChain" type="{urn:iso:15118:2:2013:MsgDataTypes}CertificateChainType"/>
 *         &lt;element name="eMAID" type="{urn:iso:15118:2:2013:MsgDataTypes}eMAIDType"/>
 *         &lt;element name="ListOfRootCertificateIDs" type="{urn:iso:15118:2:2013:MsgDataTypes}ListOfRootCertificateIDsType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateUpdateReqType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "contractSignatureCertChain",
    "emaid",
    "listOfRootCertificateIDs"
})
public class CertificateUpdateReqType
    extends BodyBaseType
{

    @XmlElement(name = "ContractSignatureCertChain", required = true)
    protected CertificateChainType contractSignatureCertChain;
    @XmlElement(name = "eMAID", required = true)
    protected String emaid;
    @XmlElement(name = "ListOfRootCertificateIDs", required = true)
    protected ListOfRootCertificateIDsType listOfRootCertificateIDs;
    @XmlAttribute(name = "Id", namespace = "urn:iso:15118:2:2013:MsgBody", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Ruft den Wert der contractSignatureCertChain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CertificateChainType }
     *     
     */
    public CertificateChainType getContractSignatureCertChain() {
        return contractSignatureCertChain;
    }

    /**
     * Legt den Wert der contractSignatureCertChain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateChainType }
     *     
     */
    public void setContractSignatureCertChain(CertificateChainType value) {
        this.contractSignatureCertChain = value;
    }

    /**
     * Ruft den Wert der emaid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMAID() {
        return emaid;
    }

    /**
     * Legt den Wert der emaid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMAID(String value) {
        this.emaid = value;
    }

    /**
     * Ruft den Wert der listOfRootCertificateIDs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ListOfRootCertificateIDsType }
     *     
     */
    public ListOfRootCertificateIDsType getListOfRootCertificateIDs() {
        return listOfRootCertificateIDs;
    }

    /**
     * Legt den Wert der listOfRootCertificateIDs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfRootCertificateIDsType }
     *     
     */
    public void setListOfRootCertificateIDs(ListOfRootCertificateIDsType value) {
        this.listOfRootCertificateIDs = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
