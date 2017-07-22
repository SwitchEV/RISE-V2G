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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DC_EVStatusType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DC_EVStatusType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgDataTypes}EVStatusType">
 *       &lt;sequence>
 *         &lt;element name="EVReady" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="EVErrorCode" type="{urn:iso:15118:2:2013:MsgDataTypes}DC_EVErrorCodeType"/>
 *         &lt;element name="EVRESSSOC" type="{urn:iso:15118:2:2013:MsgDataTypes}percentValueType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DC_EVStatusType", propOrder = {
    "evReady",
    "evErrorCode",
    "evresssoc"
})
public class DCEVStatusType
    extends EVStatusType
{

    @XmlElement(name = "EVReady")
    protected boolean evReady;
    @XmlElement(name = "EVErrorCode", required = true)
    @XmlSchemaType(name = "string")
    protected DCEVErrorCodeType evErrorCode;
    @XmlElement(name = "EVRESSSOC")
    protected byte evresssoc;

    /**
     * Ruft den Wert der evReady-Eigenschaft ab.
     * 
     */
    public boolean isEVReady() {
        return evReady;
    }

    /**
     * Legt den Wert der evReady-Eigenschaft fest.
     * 
     */
    public void setEVReady(boolean value) {
        this.evReady = value;
    }

    /**
     * Ruft den Wert der evErrorCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DCEVErrorCodeType }
     *     
     */
    public DCEVErrorCodeType getEVErrorCode() {
        return evErrorCode;
    }

    /**
     * Legt den Wert der evErrorCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DCEVErrorCodeType }
     *     
     */
    public void setEVErrorCode(DCEVErrorCodeType value) {
        this.evErrorCode = value;
    }

    /**
     * Ruft den Wert der evresssoc-Eigenschaft ab.
     * 
     */
    public byte getEVRESSSOC() {
        return evresssoc;
    }

    /**
     * Legt den Wert der evresssoc-Eigenschaft fest.
     * 
     */
    public void setEVRESSSOC(byte value) {
        this.evresssoc = value;
    }

}
