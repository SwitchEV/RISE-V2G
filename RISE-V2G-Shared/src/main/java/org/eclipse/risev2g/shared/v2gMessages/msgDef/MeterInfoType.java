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


package org.eclipse.risev2g.shared.v2gMessages.msgDef;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für MeterInfoType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="MeterInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MeterID" type="{urn:iso:15118:2:2013:MsgDataTypes}meterIDType"/>
 *         &lt;element name="MeterReading" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" minOccurs="0"/>
 *         &lt;element name="SigMeterReading" type="{urn:iso:15118:2:2013:MsgDataTypes}sigMeterReadingType" minOccurs="0"/>
 *         &lt;element name="MeterStatus" type="{urn:iso:15118:2:2013:MsgDataTypes}meterStatusType" minOccurs="0"/>
 *         &lt;element name="TMeter" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeterInfoType", propOrder = {
    "meterID",
    "meterReading",
    "sigMeterReading",
    "meterStatus",
    "tMeter"
})
public class MeterInfoType {

    @XmlElement(name = "MeterID", required = true)
    protected String meterID;
    @XmlElement(name = "MeterReading")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger meterReading;
    @XmlElement(name = "SigMeterReading")
    protected byte[] sigMeterReading;
    @XmlElement(name = "MeterStatus")
    protected Short meterStatus;
    @XmlElement(name = "TMeter")
    protected Long tMeter;

    /**
     * Ruft den Wert der meterID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterID() {
        return meterID;
    }

    /**
     * Legt den Wert der meterID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterID(String value) {
        this.meterID = value;
    }

    /**
     * Ruft den Wert der meterReading-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMeterReading() {
        return meterReading;
    }

    /**
     * Legt den Wert der meterReading-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMeterReading(BigInteger value) {
        this.meterReading = value;
    }

    /**
     * Ruft den Wert der sigMeterReading-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSigMeterReading() {
        return sigMeterReading;
    }

    /**
     * Legt den Wert der sigMeterReading-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSigMeterReading(byte[] value) {
        this.sigMeterReading = value;
    }

    /**
     * Ruft den Wert der meterStatus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getMeterStatus() {
        return meterStatus;
    }

    /**
     * Legt den Wert der meterStatus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setMeterStatus(Short value) {
        this.meterStatus = value;
    }

    /**
     * Ruft den Wert der tMeter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTMeter() {
        return tMeter;
    }

    /**
     * Legt den Wert der tMeter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTMeter(Long value) {
        this.tMeter = value;
    }

}
