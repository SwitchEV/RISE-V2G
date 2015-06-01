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
 * <p>Java-Klasse für CertificateInstallationResType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CertificateInstallationResType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="ResponseCode" type="{urn:iso:15118:2:2013:MsgDataTypes}responseCodeType"/>
 *         &lt;element name="SAProvisioningCertificateChain" type="{urn:iso:15118:2:2013:MsgDataTypes}CertificateChainType"/>
 *         &lt;element name="ContractSignatureCertChain" type="{urn:iso:15118:2:2013:MsgDataTypes}CertificateChainType"/>
 *         &lt;element name="ContractSignatureEncryptedPrivateKey" type="{urn:iso:15118:2:2013:MsgDataTypes}ContractSignatureEncryptedPrivateKeyType"/>
 *         &lt;element name="DHpublickey" type="{urn:iso:15118:2:2013:MsgDataTypes}DiffieHellmanPublickeyType"/>
 *         &lt;element name="eMAID" type="{urn:iso:15118:2:2013:MsgDataTypes}EMAIDType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateInstallationResType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "responseCode",
    "saProvisioningCertificateChain",
    "contractSignatureCertChain",
    "contractSignatureEncryptedPrivateKey",
    "dHpublickey",
    "emaid"
})
public class CertificateInstallationResType
    extends BodyBaseType
{

    @XmlElement(name = "ResponseCode", required = true)
    @XmlSchemaType(name = "string")
    protected ResponseCodeType responseCode;
    @XmlElement(name = "SAProvisioningCertificateChain", required = true)
    protected CertificateChainType saProvisioningCertificateChain;
    @XmlElement(name = "ContractSignatureCertChain", required = true)
    protected CertificateChainType contractSignatureCertChain;
    @XmlElement(name = "ContractSignatureEncryptedPrivateKey", required = true)
    protected ContractSignatureEncryptedPrivateKeyType contractSignatureEncryptedPrivateKey;
    @XmlElement(name = "DHpublickey", required = true)
    protected DiffieHellmanPublickeyType dHpublickey;
    @XmlElement(name = "eMAID", required = true)
    protected EMAIDType emaid;

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
     * Ruft den Wert der saProvisioningCertificateChain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CertificateChainType }
     *     
     */
    public CertificateChainType getSAProvisioningCertificateChain() {
        return saProvisioningCertificateChain;
    }

    /**
     * Legt den Wert der saProvisioningCertificateChain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateChainType }
     *     
     */
    public void setSAProvisioningCertificateChain(CertificateChainType value) {
        this.saProvisioningCertificateChain = value;
    }

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
     * Ruft den Wert der contractSignatureEncryptedPrivateKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ContractSignatureEncryptedPrivateKeyType }
     *     
     */
    public ContractSignatureEncryptedPrivateKeyType getContractSignatureEncryptedPrivateKey() {
        return contractSignatureEncryptedPrivateKey;
    }

    /**
     * Legt den Wert der contractSignatureEncryptedPrivateKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ContractSignatureEncryptedPrivateKeyType }
     *     
     */
    public void setContractSignatureEncryptedPrivateKey(ContractSignatureEncryptedPrivateKeyType value) {
        this.contractSignatureEncryptedPrivateKey = value;
    }

    /**
     * Ruft den Wert der dHpublickey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DiffieHellmanPublickeyType }
     *     
     */
    public DiffieHellmanPublickeyType getDHpublickey() {
        return dHpublickey;
    }

    /**
     * Legt den Wert der dHpublickey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DiffieHellmanPublickeyType }
     *     
     */
    public void setDHpublickey(DiffieHellmanPublickeyType value) {
        this.dHpublickey = value;
    }

    /**
     * Ruft den Wert der emaid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EMAIDType }
     *     
     */
    public EMAIDType getEMAID() {
        return emaid;
    }

    /**
     * Legt den Wert der emaid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EMAIDType }
     *     
     */
    public void setEMAID(EMAIDType value) {
        this.emaid = value;
    }

}
