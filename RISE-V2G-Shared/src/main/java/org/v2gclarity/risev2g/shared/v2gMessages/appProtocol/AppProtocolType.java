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
// Generiert: 2014.10.07 um 04:56:57 PM CEST 
//


package org.v2gclarity.risev2g.shared.v2gMessages.appProtocol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AppProtocolType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AppProtocolType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProtocolNamespace" type="{urn:iso:15118:2:2010:AppProtocol}protocolNamespaceType"/>
 *         &lt;element name="VersionNumberMajor" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="VersionNumberMinor" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *         &lt;element name="SchemaID" type="{urn:iso:15118:2:2010:AppProtocol}idType"/>
 *         &lt;element name="Priority" type="{urn:iso:15118:2:2010:AppProtocol}priorityType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppProtocolType", propOrder = {
    "protocolNamespace",
    "versionNumberMajor",
    "versionNumberMinor",
    "schemaID",
    "priority"
})
public class AppProtocolType {

    @XmlElement(name = "ProtocolNamespace", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String protocolNamespace;
    @XmlElement(name = "VersionNumberMajor")
    @XmlSchemaType(name = "unsignedInt")
    protected long versionNumberMajor;
    @XmlElement(name = "VersionNumberMinor")
    @XmlSchemaType(name = "unsignedInt")
    protected long versionNumberMinor;
    @XmlElement(name = "SchemaID")
    @XmlSchemaType(name = "unsignedByte")
    protected short schemaID;
    @XmlElement(name = "Priority")
    @XmlSchemaType(name = "unsignedByte")
    protected short priority;

    /**
     * Ruft den Wert der protocolNamespace-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolNamespace() {
        return protocolNamespace;
    }

    /**
     * Legt den Wert der protocolNamespace-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolNamespace(String value) {
        this.protocolNamespace = value;
    }

    /**
     * Ruft den Wert der versionNumberMajor-Eigenschaft ab.
     * 
     */
    public long getVersionNumberMajor() {
        return versionNumberMajor;
    }

    /**
     * Legt den Wert der versionNumberMajor-Eigenschaft fest.
     * 
     */
    public void setVersionNumberMajor(long value) {
        this.versionNumberMajor = value;
    }

    /**
     * Ruft den Wert der versionNumberMinor-Eigenschaft ab.
     * 
     */
    public long getVersionNumberMinor() {
        return versionNumberMinor;
    }

    /**
     * Legt den Wert der versionNumberMinor-Eigenschaft fest.
     * 
     */
    public void setVersionNumberMinor(long value) {
        this.versionNumberMinor = value;
    }

    /**
     * Ruft den Wert der schemaID-Eigenschaft ab.
     * 
     */
    public short getSchemaID() {
        return schemaID;
    }

    /**
     * Legt den Wert der schemaID-Eigenschaft fest.
     * 
     */
    public void setSchemaID(short value) {
        this.schemaID = value;
    }

    /**
     * Ruft den Wert der priority-Eigenschaft ab.
     * 
     */
    public short getPriority() {
        return priority;
    }

    /**
     * Legt den Wert der priority-Eigenschaft fest.
     * 
     */
    public void setPriority(short value) {
        this.priority = value;
    }

}
