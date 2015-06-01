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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ChargeServiceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ChargeServiceType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:iso:15118:2:2013:MsgDataTypes}ServiceType">
 *       &lt;sequence>
 *         &lt;element name="SupportedEnergyTransferMode" type="{urn:iso:15118:2:2013:MsgDataTypes}SupportedEnergyTransferModeType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeServiceType", propOrder = {
    "supportedEnergyTransferMode"
})
public class ChargeServiceType
    extends ServiceType
{

    @XmlElement(name = "SupportedEnergyTransferMode", required = true)
    protected SupportedEnergyTransferModeType supportedEnergyTransferMode;

    /**
     * Ruft den Wert der supportedEnergyTransferMode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SupportedEnergyTransferModeType }
     *     
     */
    public SupportedEnergyTransferModeType getSupportedEnergyTransferMode() {
        return supportedEnergyTransferMode;
    }

    /**
     * Legt den Wert der supportedEnergyTransferMode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportedEnergyTransferModeType }
     *     
     */
    public void setSupportedEnergyTransferMode(SupportedEnergyTransferModeType value) {
        this.supportedEnergyTransferMode = value;
    }

}
