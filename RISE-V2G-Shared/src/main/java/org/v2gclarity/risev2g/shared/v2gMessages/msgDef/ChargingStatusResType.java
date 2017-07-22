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
 * <p>Java-Klasse für ChargingStatusResType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ChargingStatusResType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgBody}BodyBaseType">
 *       &lt;sequence>
 *         &lt;element name="ResponseCode" type="{urn:iso:15118:2:2013:MsgDataTypes}responseCodeType"/>
 *         &lt;element name="EVSEID" type="{urn:iso:15118:2:2013:MsgDataTypes}evseIDType"/>
 *         &lt;element name="SAScheduleTupleID" type="{urn:iso:15118:2:2013:MsgDataTypes}SAIDType"/>
 *         &lt;element name="EVSEMaxCurrent" type="{urn:iso:15118:2:2013:MsgDataTypes}PhysicalValueType" minOccurs="0"/>
 *         &lt;element name="MeterInfo" type="{urn:iso:15118:2:2013:MsgDataTypes}MeterInfoType" minOccurs="0"/>
 *         &lt;element name="ReceiptRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="AC_EVSEStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}AC_EVSEStatusType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargingStatusResType", namespace = "urn:iso:15118:2:2013:MsgBody", propOrder = {
    "responseCode",
    "evseid",
    "saScheduleTupleID",
    "evseMaxCurrent",
    "meterInfo",
    "receiptRequired",
    "acevseStatus"
})
public class ChargingStatusResType
    extends BodyBaseType
{

    @XmlElement(name = "ResponseCode", required = true)
    @XmlSchemaType(name = "string")
    protected ResponseCodeType responseCode;
    @XmlElement(name = "EVSEID", required = true)
    protected String evseid;
    @XmlElement(name = "SAScheduleTupleID")
    @XmlSchemaType(name = "unsignedByte")
    protected short saScheduleTupleID;
    @XmlElement(name = "EVSEMaxCurrent")
    protected PhysicalValueType evseMaxCurrent;
    @XmlElement(name = "MeterInfo")
    protected MeterInfoType meterInfo;
    @XmlElement(name = "ReceiptRequired")
    protected Boolean receiptRequired;
    @XmlElement(name = "AC_EVSEStatus", required = true)
    protected ACEVSEStatusType acevseStatus;

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
     * Ruft den Wert der evseid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEVSEID() {
        return evseid;
    }

    /**
     * Legt den Wert der evseid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEVSEID(String value) {
        this.evseid = value;
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
     * Ruft den Wert der evseMaxCurrent-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalValueType }
     *     
     */
    public PhysicalValueType getEVSEMaxCurrent() {
        return evseMaxCurrent;
    }

    /**
     * Legt den Wert der evseMaxCurrent-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalValueType }
     *     
     */
    public void setEVSEMaxCurrent(PhysicalValueType value) {
        this.evseMaxCurrent = value;
    }

    /**
     * Ruft den Wert der meterInfo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeterInfoType }
     *     
     */
    public MeterInfoType getMeterInfo() {
        return meterInfo;
    }

    /**
     * Legt den Wert der meterInfo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeterInfoType }
     *     
     */
    public void setMeterInfo(MeterInfoType value) {
        this.meterInfo = value;
    }

    /**
     * Ruft den Wert der receiptRequired-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReceiptRequired() {
        return receiptRequired;
    }

    /**
     * Legt den Wert der receiptRequired-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReceiptRequired(Boolean value) {
        this.receiptRequired = value;
    }

    /**
     * Ruft den Wert der acevseStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ACEVSEStatusType }
     *     
     */
    public ACEVSEStatusType getACEVSEStatus() {
        return acevseStatus;
    }

    /**
     * Legt den Wert der acevseStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ACEVSEStatusType }
     *     
     */
    public void setACEVSEStatus(ACEVSEStatusType value) {
        this.acevseStatus = value;
    }

}
