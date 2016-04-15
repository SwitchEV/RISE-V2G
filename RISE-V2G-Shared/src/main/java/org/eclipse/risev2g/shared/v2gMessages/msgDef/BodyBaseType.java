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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für BodyBaseType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="BodyBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BodyBaseType", namespace = "urn:iso:15118:2:2013:MsgBody")
@XmlSeeAlso({
    AuthorizationResType.class,
    PaymentDetailsReqType.class,
    AuthorizationReqType.class,
    WeldingDetectionReqType.class,
    PaymentDetailsResType.class,
    WeldingDetectionResType.class,
    ChargeParameterDiscoveryResType.class,
    CertificateUpdateReqType.class,
    ChargeParameterDiscoveryReqType.class,
    PreChargeReqType.class,
    ServiceDetailResType.class,
    MeteringReceiptReqType.class,
    ServiceDetailReqType.class,
    CertificateInstallationResType.class,
    PreChargeResType.class,
    CertificateInstallationReqType.class,
    CableCheckResType.class,
    CableCheckReqType.class,
    MeteringReceiptResType.class,
    ChargingStatusReqType.class,
    ServiceDiscoveryResType.class,
    PowerDeliveryReqType.class,
    ChargingStatusResType.class,
    ServiceDiscoveryReqType.class,
    PowerDeliveryResType.class,
    SessionStopResType.class,
    SessionSetupResType.class,
    PaymentServiceSelectionResType.class,
    CertificateUpdateResType.class,
    PaymentServiceSelectionReqType.class,
    CurrentDemandReqType.class,
    CurrentDemandResType.class,
    SessionStopReqType.class,
    SessionSetupReqType.class
})
public abstract class BodyBaseType {


}
