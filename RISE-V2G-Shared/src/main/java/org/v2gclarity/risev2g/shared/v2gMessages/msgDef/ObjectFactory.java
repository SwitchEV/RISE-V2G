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

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the v2gMessages.msgDef package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SessionSetupRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "SessionSetupRes");
    private final static QName _PaymentServiceSelectionRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PaymentServiceSelectionRes");
    private final static QName _KeyInfo_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
    private final static QName _PaymentServiceSelectionReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PaymentServiceSelectionReq");
    private final static QName _SASchedules_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "SASchedules");
    private final static QName _SessionSetupReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "SessionSetupReq");
    private final static QName _CableCheckRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CableCheckRes");
    private final static QName _CableCheckReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CableCheckReq");
    private final static QName _EVStatus_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "EVStatus");
    private final static QName _PMaxScheduleEntry_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "PMaxScheduleEntry");
    private final static QName _ServiceDiscoveryRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ServiceDiscoveryRes");
    private final static QName _ChargingStatusReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ChargingStatusReq");
    private final static QName _ServiceDiscoveryReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ServiceDiscoveryReq");
    private final static QName _ChargingStatusRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ChargingStatusRes");
    private final static QName _SignatureProperty_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureProperty");
    private final static QName _RelativeTimeInterval_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "RelativeTimeInterval");
    private final static QName _RSAKeyValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "RSAKeyValue");
    private final static QName _MeteringReceiptReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "MeteringReceiptReq");
    private final static QName _SignatureMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureMethod");
    private final static QName _Object_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Object");
    private final static QName _EVSEStatus_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "EVSEStatus");
    private final static QName _PGPData_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "PGPData");
    private final static QName _EVSEChargeParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "EVSEChargeParameter");
    private final static QName _EVChargeParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "EVChargeParameter");
    private final static QName _RetrievalMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "RetrievalMethod");
    private final static QName _WeldingDetectionReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "WeldingDetectionReq");
    private final static QName _DSAKeyValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "DSAKeyValue");
    private final static QName _WeldingDetectionRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "WeldingDetectionRes");
    private final static QName _ChargeParameterDiscoveryRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ChargeParameterDiscoveryRes");
    private final static QName _DCEVChargeParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVChargeParameter");
    private final static QName _ChargeParameterDiscoveryReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ChargeParameterDiscoveryReq");
    private final static QName _SessionStopRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "SessionStopRes");
    private final static QName _SPKIData_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SPKIData");
    private final static QName _CertificateUpdateRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CertificateUpdateRes");
    private final static QName _EVPowerDeliveryParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "EVPowerDeliveryParameter");
    private final static QName _SignatureValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureValue");
    private final static QName _ACEVSEChargeParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVSEChargeParameter");
    private final static QName _TimeInterval_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "TimeInterval");
    private final static QName _CurrentDemandReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CurrentDemandReq");
    private final static QName _CurrentDemandRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CurrentDemandRes");
    private final static QName _DCEVSEStatus_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVSEStatus");
    private final static QName _SessionStopReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "SessionStopReq");
    private final static QName _ACEVSEStatus_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVSEStatus");
    private final static QName _DCEVPowerDeliveryParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVPowerDeliveryParameter");
    private final static QName _KeyValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "KeyValue");
    private final static QName _SAScheduleList_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "SAScheduleList");
    private final static QName _Transforms_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Transforms");
    private final static QName _MeteringReceiptRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "MeteringReceiptRes");
    private final static QName _DCEVSEChargeParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVSEChargeParameter");
    private final static QName _DigestMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestMethod");
    private final static QName _PowerDeliveryReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PowerDeliveryReq");
    private final static QName _X509Data_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509Data");
    private final static QName _PowerDeliveryRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PowerDeliveryRes");
    private final static QName _PreChargeReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PreChargeReq");
    private final static QName _KeyName_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "KeyName");
    private final static QName _Signature_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature");
    private final static QName _MgmtData_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "MgmtData");
    private final static QName _ServiceDetailRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ServiceDetailRes");
    private final static QName _ServiceDetailReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "ServiceDetailReq");
    private final static QName _SignatureProperties_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignatureProperties");
    private final static QName _CertificateInstallationRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CertificateInstallationRes");
    private final static QName _CertificateInstallationReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CertificateInstallationReq");
    private final static QName _Transform_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Transform");
    private final static QName _PreChargeRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PreChargeRes");
    private final static QName _AuthorizationRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "AuthorizationRes");
    private final static QName _PaymentDetailsReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PaymentDetailsReq");
    private final static QName _AuthorizationReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "AuthorizationReq");
    private final static QName _DCEVStatus_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVStatus");
    private final static QName _PaymentDetailsRes_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "PaymentDetailsRes");
    private final static QName _Reference_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Reference");
    private final static QName _SalesTariffEntry_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "SalesTariffEntry");
    private final static QName _ACEVChargeParameter_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVChargeParameter");
    private final static QName _DigestValue_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "DigestValue");
    private final static QName _BodyElement_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "BodyElement");
    private final static QName _CanonicalizationMethod_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod");
    private final static QName _CertificateUpdateReq_QNAME = new QName("urn:iso:15118:2:2013:MsgBody", "CertificateUpdateReq");
    private final static QName _Entry_QNAME = new QName("urn:iso:15118:2:2013:MsgDataTypes", "Entry");
    private final static QName _SignedInfo_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SignedInfo");
    private final static QName _Manifest_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "Manifest");
    private final static QName _TransformTypeXPath_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "XPath");
    private final static QName _SPKIDataTypeSPKISexp_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "SPKISexp");
    private final static QName _SignatureMethodTypeHMACOutputLength_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "HMACOutputLength");
    private final static QName _X509DataTypeX509IssuerSerial_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509IssuerSerial");
    private final static QName _X509DataTypeX509CRL_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509CRL");
    private final static QName _X509DataTypeX509SubjectName_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509SubjectName");
    private final static QName _X509DataTypeX509SKI_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509SKI");
    private final static QName _X509DataTypeX509Certificate_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "X509Certificate");
    private final static QName _PGPDataTypePGPKeyID_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "PGPKeyID");
    private final static QName _PGPDataTypePGPKeyPacket_QNAME = new QName("http://www.w3.org/2000/09/xmldsig#", "PGPKeyPacket");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: v2gMessages.msgDef
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link V2GMessage }
     * 
     */
    public V2GMessage createV2GMessage() {
        return new V2GMessage();
    }

    /**
     * Create an instance of {@link MessageHeaderType }
     * 
     */
    public MessageHeaderType createMessageHeaderType() {
        return new MessageHeaderType();
    }

    /**
     * Create an instance of {@link BodyType }
     * 
     */
    public BodyType createBodyType() {
        return new BodyType();
    }

    /**
     * Create an instance of {@link DCEVSEStatusType }
     * 
     */
    public DCEVSEStatusType createDCEVSEStatusType() {
        return new DCEVSEStatusType();
    }

    /**
     * Create an instance of {@link ACEVSEChargeParameterType }
     * 
     */
    public ACEVSEChargeParameterType createACEVSEChargeParameterType() {
        return new ACEVSEChargeParameterType();
    }

    /**
     * Create an instance of {@link RelativeTimeIntervalType }
     * 
     */
    public RelativeTimeIntervalType createRelativeTimeIntervalType() {
        return new RelativeTimeIntervalType();
    }

    /**
     * Create an instance of {@link DCEVChargeParameterType }
     * 
     */
    public DCEVChargeParameterType createDCEVChargeParameterType() {
        return new DCEVChargeParameterType();
    }

    /**
     * Create an instance of {@link ACEVChargeParameterType }
     * 
     */
    public ACEVChargeParameterType createACEVChargeParameterType() {
        return new ACEVChargeParameterType();
    }

    /**
     * Create an instance of {@link DCEVSEChargeParameterType }
     * 
     */
    public DCEVSEChargeParameterType createDCEVSEChargeParameterType() {
        return new DCEVSEChargeParameterType();
    }

    /**
     * Create an instance of {@link SalesTariffEntryType }
     * 
     */
    public SalesTariffEntryType createSalesTariffEntryType() {
        return new SalesTariffEntryType();
    }

    /**
     * Create an instance of {@link PMaxScheduleEntryType }
     * 
     */
    public PMaxScheduleEntryType createPMaxScheduleEntryType() {
        return new PMaxScheduleEntryType();
    }

    /**
     * Create an instance of {@link SAScheduleListType }
     * 
     */
    public SAScheduleListType createSAScheduleListType() {
        return new SAScheduleListType();
    }

    /**
     * Create an instance of {@link DCEVStatusType }
     * 
     */
    public DCEVStatusType createDCEVStatusType() {
        return new DCEVStatusType();
    }

    /**
     * Create an instance of {@link ACEVSEStatusType }
     * 
     */
    public ACEVSEStatusType createACEVSEStatusType() {
        return new ACEVSEStatusType();
    }

    /**
     * Create an instance of {@link DCEVPowerDeliveryParameterType }
     * 
     */
    public DCEVPowerDeliveryParameterType createDCEVPowerDeliveryParameterType() {
        return new DCEVPowerDeliveryParameterType();
    }

    /**
     * Create an instance of {@link PhysicalValueType }
     * 
     */
    public PhysicalValueType createPhysicalValueType() {
        return new PhysicalValueType();
    }

    /**
     * Create an instance of {@link DiffieHellmanPublickeyType }
     * 
     */
    public DiffieHellmanPublickeyType createDiffieHellmanPublickeyType() {
        return new DiffieHellmanPublickeyType();
    }

    /**
     * Create an instance of {@link ChargingProfileType }
     * 
     */
    public ChargingProfileType createChargingProfileType() {
        return new ChargingProfileType();
    }

    /**
     * Create an instance of {@link SubCertificatesType }
     * 
     */
    public SubCertificatesType createSubCertificatesType() {
        return new SubCertificatesType();
    }

    /**
     * Create an instance of {@link ServiceListType }
     * 
     */
    public ServiceListType createServiceListType() {
        return new ServiceListType();
    }

    /**
     * Create an instance of {@link ServiceType }
     * 
     */
    public ServiceType createServiceType() {
        return new ServiceType();
    }

    /**
     * Create an instance of {@link MeterInfoType }
     * 
     */
    public MeterInfoType createMeterInfoType() {
        return new MeterInfoType();
    }

    /**
     * Create an instance of {@link ProfileEntryType }
     * 
     */
    public ProfileEntryType createProfileEntryType() {
        return new ProfileEntryType();
    }

    /**
     * Create an instance of {@link PMaxScheduleType }
     * 
     */
    public PMaxScheduleType createPMaxScheduleType() {
        return new PMaxScheduleType();
    }

    /**
     * Create an instance of {@link SupportedEnergyTransferModeType }
     * 
     */
    public SupportedEnergyTransferModeType createSupportedEnergyTransferModeType() {
        return new SupportedEnergyTransferModeType();
    }

    /**
     * Create an instance of {@link CertificateChainType }
     * 
     */
    public CertificateChainType createCertificateChainType() {
        return new CertificateChainType();
    }

    /**
     * Create an instance of {@link NotificationType }
     * 
     */
    public NotificationType createNotificationType() {
        return new NotificationType();
    }

    /**
     * Create an instance of {@link ServiceParameterListType }
     * 
     */
    public ServiceParameterListType createServiceParameterListType() {
        return new ServiceParameterListType();
    }

    /**
     * Create an instance of {@link SelectedServiceType }
     * 
     */
    public SelectedServiceType createSelectedServiceType() {
        return new SelectedServiceType();
    }

    /**
     * Create an instance of {@link ConsumptionCostType }
     * 
     */
    public ConsumptionCostType createConsumptionCostType() {
        return new ConsumptionCostType();
    }

    /**
     * Create an instance of {@link SelectedServiceListType }
     * 
     */
    public SelectedServiceListType createSelectedServiceListType() {
        return new SelectedServiceListType();
    }

    /**
     * Create an instance of {@link SAScheduleTupleType }
     * 
     */
    public SAScheduleTupleType createSAScheduleTupleType() {
        return new SAScheduleTupleType();
    }

    /**
     * Create an instance of {@link ListOfRootCertificateIDsType }
     * 
     */
    public ListOfRootCertificateIDsType createListOfRootCertificateIDsType() {
        return new ListOfRootCertificateIDsType();
    }

    /**
     * Create an instance of {@link CostType }
     * 
     */
    public CostType createCostType() {
        return new CostType();
    }

    /**
     * Create an instance of {@link PaymentOptionListType }
     * 
     */
    public PaymentOptionListType createPaymentOptionListType() {
        return new PaymentOptionListType();
    }

    /**
     * Create an instance of {@link EMAIDType }
     * 
     */
    public EMAIDType createEMAIDType() {
        return new EMAIDType();
    }

    /**
     * Create an instance of {@link ContractSignatureEncryptedPrivateKeyType }
     * 
     */
    public ContractSignatureEncryptedPrivateKeyType createContractSignatureEncryptedPrivateKeyType() {
        return new ContractSignatureEncryptedPrivateKeyType();
    }

    /**
     * Create an instance of {@link SalesTariffType }
     * 
     */
    public SalesTariffType createSalesTariffType() {
        return new SalesTariffType();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link ParameterSetType }
     * 
     */
    public ParameterSetType createParameterSetType() {
        return new ParameterSetType();
    }

    /**
     * Create an instance of {@link ChargeServiceType }
     * 
     */
    public ChargeServiceType createChargeServiceType() {
        return new ChargeServiceType();
    }

    /**
     * Create an instance of {@link PGPDataType }
     * 
     */
    public PGPDataType createPGPDataType() {
        return new PGPDataType();
    }

    /**
     * Create an instance of {@link KeyValueType }
     * 
     */
    public KeyValueType createKeyValueType() {
        return new KeyValueType();
    }

    /**
     * Create an instance of {@link DSAKeyValueType }
     * 
     */
    public DSAKeyValueType createDSAKeyValueType() {
        return new DSAKeyValueType();
    }

    /**
     * Create an instance of {@link ReferenceType }
     * 
     */
    public ReferenceType createReferenceType() {
        return new ReferenceType();
    }

    /**
     * Create an instance of {@link RetrievalMethodType }
     * 
     */
    public RetrievalMethodType createRetrievalMethodType() {
        return new RetrievalMethodType();
    }

    /**
     * Create an instance of {@link TransformsType }
     * 
     */
    public TransformsType createTransformsType() {
        return new TransformsType();
    }

    /**
     * Create an instance of {@link CanonicalizationMethodType }
     * 
     */
    public CanonicalizationMethodType createCanonicalizationMethodType() {
        return new CanonicalizationMethodType();
    }

    /**
     * Create an instance of {@link DigestMethodType }
     * 
     */
    public DigestMethodType createDigestMethodType() {
        return new DigestMethodType();
    }

    /**
     * Create an instance of {@link ManifestType }
     * 
     */
    public ManifestType createManifestType() {
        return new ManifestType();
    }

    /**
     * Create an instance of {@link SignaturePropertyType }
     * 
     */
    public SignaturePropertyType createSignaturePropertyType() {
        return new SignaturePropertyType();
    }

    /**
     * Create an instance of {@link X509DataType }
     * 
     */
    public X509DataType createX509DataType() {
        return new X509DataType();
    }

    /**
     * Create an instance of {@link SignedInfoType }
     * 
     */
    public SignedInfoType createSignedInfoType() {
        return new SignedInfoType();
    }

    /**
     * Create an instance of {@link RSAKeyValueType }
     * 
     */
    public RSAKeyValueType createRSAKeyValueType() {
        return new RSAKeyValueType();
    }

    /**
     * Create an instance of {@link SPKIDataType }
     * 
     */
    public SPKIDataType createSPKIDataType() {
        return new SPKIDataType();
    }

    /**
     * Create an instance of {@link SignatureValueType }
     * 
     */
    public SignatureValueType createSignatureValueType() {
        return new SignatureValueType();
    }

    /**
     * Create an instance of {@link KeyInfoType }
     * 
     */
    public KeyInfoType createKeyInfoType() {
        return new KeyInfoType();
    }

    /**
     * Create an instance of {@link SignatureType }
     * 
     */
    public SignatureType createSignatureType() {
        return new SignatureType();
    }

    /**
     * Create an instance of {@link SignaturePropertiesType }
     * 
     */
    public SignaturePropertiesType createSignaturePropertiesType() {
        return new SignaturePropertiesType();
    }

    /**
     * Create an instance of {@link SignatureMethodType }
     * 
     */
    public SignatureMethodType createSignatureMethodType() {
        return new SignatureMethodType();
    }

    /**
     * Create an instance of {@link ObjectType }
     * 
     */
    public ObjectType createObjectType() {
        return new ObjectType();
    }

    /**
     * Create an instance of {@link TransformType }
     * 
     */
    public TransformType createTransformType() {
        return new TransformType();
    }

    /**
     * Create an instance of {@link X509IssuerSerialType }
     * 
     */
    public X509IssuerSerialType createX509IssuerSerialType() {
        return new X509IssuerSerialType();
    }

    /**
     * Create an instance of {@link AuthorizationResType }
     * 
     */
    public AuthorizationResType createAuthorizationResType() {
        return new AuthorizationResType();
    }

    /**
     * Create an instance of {@link PaymentDetailsReqType }
     * 
     */
    public PaymentDetailsReqType createPaymentDetailsReqType() {
        return new PaymentDetailsReqType();
    }

    /**
     * Create an instance of {@link AuthorizationReqType }
     * 
     */
    public AuthorizationReqType createAuthorizationReqType() {
        return new AuthorizationReqType();
    }

    /**
     * Create an instance of {@link WeldingDetectionReqType }
     * 
     */
    public WeldingDetectionReqType createWeldingDetectionReqType() {
        return new WeldingDetectionReqType();
    }

    /**
     * Create an instance of {@link PaymentDetailsResType }
     * 
     */
    public PaymentDetailsResType createPaymentDetailsResType() {
        return new PaymentDetailsResType();
    }

    /**
     * Create an instance of {@link WeldingDetectionResType }
     * 
     */
    public WeldingDetectionResType createWeldingDetectionResType() {
        return new WeldingDetectionResType();
    }

    /**
     * Create an instance of {@link ChargeParameterDiscoveryResType }
     * 
     */
    public ChargeParameterDiscoveryResType createChargeParameterDiscoveryResType() {
        return new ChargeParameterDiscoveryResType();
    }

    /**
     * Create an instance of {@link CertificateUpdateReqType }
     * 
     */
    public CertificateUpdateReqType createCertificateUpdateReqType() {
        return new CertificateUpdateReqType();
    }

    /**
     * Create an instance of {@link ChargeParameterDiscoveryReqType }
     * 
     */
    public ChargeParameterDiscoveryReqType createChargeParameterDiscoveryReqType() {
        return new ChargeParameterDiscoveryReqType();
    }

    /**
     * Create an instance of {@link PreChargeReqType }
     * 
     */
    public PreChargeReqType createPreChargeReqType() {
        return new PreChargeReqType();
    }

    /**
     * Create an instance of {@link ServiceDetailResType }
     * 
     */
    public ServiceDetailResType createServiceDetailResType() {
        return new ServiceDetailResType();
    }

    /**
     * Create an instance of {@link MeteringReceiptReqType }
     * 
     */
    public MeteringReceiptReqType createMeteringReceiptReqType() {
        return new MeteringReceiptReqType();
    }

    /**
     * Create an instance of {@link ServiceDetailReqType }
     * 
     */
    public ServiceDetailReqType createServiceDetailReqType() {
        return new ServiceDetailReqType();
    }

    /**
     * Create an instance of {@link CertificateInstallationResType }
     * 
     */
    public CertificateInstallationResType createCertificateInstallationResType() {
        return new CertificateInstallationResType();
    }

    /**
     * Create an instance of {@link PreChargeResType }
     * 
     */
    public PreChargeResType createPreChargeResType() {
        return new PreChargeResType();
    }

    /**
     * Create an instance of {@link CertificateInstallationReqType }
     * 
     */
    public CertificateInstallationReqType createCertificateInstallationReqType() {
        return new CertificateInstallationReqType();
    }

    /**
     * Create an instance of {@link CableCheckResType }
     * 
     */
    public CableCheckResType createCableCheckResType() {
        return new CableCheckResType();
    }

    /**
     * Create an instance of {@link CableCheckReqType }
     * 
     */
    public CableCheckReqType createCableCheckReqType() {
        return new CableCheckReqType();
    }

    /**
     * Create an instance of {@link MeteringReceiptResType }
     * 
     */
    public MeteringReceiptResType createMeteringReceiptResType() {
        return new MeteringReceiptResType();
    }

    /**
     * Create an instance of {@link ChargingStatusReqType }
     * 
     */
    public ChargingStatusReqType createChargingStatusReqType() {
        return new ChargingStatusReqType();
    }

    /**
     * Create an instance of {@link ServiceDiscoveryResType }
     * 
     */
    public ServiceDiscoveryResType createServiceDiscoveryResType() {
        return new ServiceDiscoveryResType();
    }

    /**
     * Create an instance of {@link PowerDeliveryReqType }
     * 
     */
    public PowerDeliveryReqType createPowerDeliveryReqType() {
        return new PowerDeliveryReqType();
    }

    /**
     * Create an instance of {@link ChargingStatusResType }
     * 
     */
    public ChargingStatusResType createChargingStatusResType() {
        return new ChargingStatusResType();
    }

    /**
     * Create an instance of {@link ServiceDiscoveryReqType }
     * 
     */
    public ServiceDiscoveryReqType createServiceDiscoveryReqType() {
        return new ServiceDiscoveryReqType();
    }

    /**
     * Create an instance of {@link PowerDeliveryResType }
     * 
     */
    public PowerDeliveryResType createPowerDeliveryResType() {
        return new PowerDeliveryResType();
    }

    /**
     * Create an instance of {@link SessionStopResType }
     * 
     */
    public SessionStopResType createSessionStopResType() {
        return new SessionStopResType();
    }

    /**
     * Create an instance of {@link SessionSetupResType }
     * 
     */
    public SessionSetupResType createSessionSetupResType() {
        return new SessionSetupResType();
    }

    /**
     * Create an instance of {@link PaymentServiceSelectionResType }
     * 
     */
    public PaymentServiceSelectionResType createPaymentServiceSelectionResType() {
        return new PaymentServiceSelectionResType();
    }

    /**
     * Create an instance of {@link CertificateUpdateResType }
     * 
     */
    public CertificateUpdateResType createCertificateUpdateResType() {
        return new CertificateUpdateResType();
    }

    /**
     * Create an instance of {@link PaymentServiceSelectionReqType }
     * 
     */
    public PaymentServiceSelectionReqType createPaymentServiceSelectionReqType() {
        return new PaymentServiceSelectionReqType();
    }

    /**
     * Create an instance of {@link CurrentDemandReqType }
     * 
     */
    public CurrentDemandReqType createCurrentDemandReqType() {
        return new CurrentDemandReqType();
    }

    /**
     * Create an instance of {@link CurrentDemandResType }
     * 
     */
    public CurrentDemandResType createCurrentDemandResType() {
        return new CurrentDemandResType();
    }

    /**
     * Create an instance of {@link SessionStopReqType }
     * 
     */
    public SessionStopReqType createSessionStopReqType() {
        return new SessionStopReqType();
    }

    /**
     * Create an instance of {@link SessionSetupReqType }
     * 
     */
    public SessionSetupReqType createSessionSetupReqType() {
        return new SessionSetupReqType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionSetupResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "SessionSetupRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<SessionSetupResType> createSessionSetupRes(SessionSetupResType value) {
        return new JAXBElement<SessionSetupResType>(_SessionSetupRes_QNAME, SessionSetupResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentServiceSelectionResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PaymentServiceSelectionRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PaymentServiceSelectionResType> createPaymentServiceSelectionRes(PaymentServiceSelectionResType value) {
        return new JAXBElement<PaymentServiceSelectionResType>(_PaymentServiceSelectionRes_QNAME, PaymentServiceSelectionResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeyInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "KeyInfo")
    public JAXBElement<KeyInfoType> createKeyInfo(KeyInfoType value) {
        return new JAXBElement<KeyInfoType>(_KeyInfo_QNAME, KeyInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentServiceSelectionReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PaymentServiceSelectionReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PaymentServiceSelectionReqType> createPaymentServiceSelectionReq(PaymentServiceSelectionReqType value) {
        return new JAXBElement<PaymentServiceSelectionReqType>(_PaymentServiceSelectionReq_QNAME, PaymentServiceSelectionReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SASchedulesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "SASchedules")
    public JAXBElement<SASchedulesType> createSASchedules(SASchedulesType value) {
        return new JAXBElement<SASchedulesType>(_SASchedules_QNAME, SASchedulesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionSetupReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "SessionSetupReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<SessionSetupReqType> createSessionSetupReq(SessionSetupReqType value) {
        return new JAXBElement<SessionSetupReqType>(_SessionSetupReq_QNAME, SessionSetupReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CableCheckResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CableCheckRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CableCheckResType> createCableCheckRes(CableCheckResType value) {
        return new JAXBElement<CableCheckResType>(_CableCheckRes_QNAME, CableCheckResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CableCheckReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CableCheckReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CableCheckReqType> createCableCheckReq(CableCheckReqType value) {
        return new JAXBElement<CableCheckReqType>(_CableCheckReq_QNAME, CableCheckReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EVStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "EVStatus")
    public JAXBElement<EVStatusType> createEVStatus(EVStatusType value) {
        return new JAXBElement<EVStatusType>(_EVStatus_QNAME, EVStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PMaxScheduleEntryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "PMaxScheduleEntry", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "Entry")
    public JAXBElement<PMaxScheduleEntryType> createPMaxScheduleEntry(PMaxScheduleEntryType value) {
        return new JAXBElement<PMaxScheduleEntryType>(_PMaxScheduleEntry_QNAME, PMaxScheduleEntryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceDiscoveryResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ServiceDiscoveryRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ServiceDiscoveryResType> createServiceDiscoveryRes(ServiceDiscoveryResType value) {
        return new JAXBElement<ServiceDiscoveryResType>(_ServiceDiscoveryRes_QNAME, ServiceDiscoveryResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChargingStatusReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ChargingStatusReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ChargingStatusReqType> createChargingStatusReq(ChargingStatusReqType value) {
        return new JAXBElement<ChargingStatusReqType>(_ChargingStatusReq_QNAME, ChargingStatusReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceDiscoveryReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ServiceDiscoveryReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ServiceDiscoveryReqType> createServiceDiscoveryReq(ServiceDiscoveryReqType value) {
        return new JAXBElement<ServiceDiscoveryReqType>(_ServiceDiscoveryReq_QNAME, ServiceDiscoveryReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChargingStatusResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ChargingStatusRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ChargingStatusResType> createChargingStatusRes(ChargingStatusResType value) {
        return new JAXBElement<ChargingStatusResType>(_ChargingStatusRes_QNAME, ChargingStatusResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignaturePropertyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignatureProperty")
    public JAXBElement<SignaturePropertyType> createSignatureProperty(SignaturePropertyType value) {
        return new JAXBElement<SignaturePropertyType>(_SignatureProperty_QNAME, SignaturePropertyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RelativeTimeIntervalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "RelativeTimeInterval", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "TimeInterval")
    public JAXBElement<RelativeTimeIntervalType> createRelativeTimeInterval(RelativeTimeIntervalType value) {
        return new JAXBElement<RelativeTimeIntervalType>(_RelativeTimeInterval_QNAME, RelativeTimeIntervalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RSAKeyValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "RSAKeyValue")
    public JAXBElement<RSAKeyValueType> createRSAKeyValue(RSAKeyValueType value) {
        return new JAXBElement<RSAKeyValueType>(_RSAKeyValue_QNAME, RSAKeyValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeteringReceiptReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "MeteringReceiptReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<MeteringReceiptReqType> createMeteringReceiptReq(MeteringReceiptReqType value) {
        return new JAXBElement<MeteringReceiptReqType>(_MeteringReceiptReq_QNAME, MeteringReceiptReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignatureMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignatureMethod")
    public JAXBElement<SignatureMethodType> createSignatureMethod(SignatureMethodType value) {
        return new JAXBElement<SignatureMethodType>(_SignatureMethod_QNAME, SignatureMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObjectType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Object")
    public JAXBElement<ObjectType> createObject(ObjectType value) {
        return new JAXBElement<ObjectType>(_Object_QNAME, ObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EVSEStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "EVSEStatus")
    public JAXBElement<EVSEStatusType> createEVSEStatus(EVSEStatusType value) {
        return new JAXBElement<EVSEStatusType>(_EVSEStatus_QNAME, EVSEStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PGPDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "PGPData")
    public JAXBElement<PGPDataType> createPGPData(PGPDataType value) {
        return new JAXBElement<PGPDataType>(_PGPData_QNAME, PGPDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EVSEChargeParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "EVSEChargeParameter")
    public JAXBElement<EVSEChargeParameterType> createEVSEChargeParameter(EVSEChargeParameterType value) {
        return new JAXBElement<EVSEChargeParameterType>(_EVSEChargeParameter_QNAME, EVSEChargeParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EVChargeParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "EVChargeParameter")
    public JAXBElement<EVChargeParameterType> createEVChargeParameter(EVChargeParameterType value) {
        return new JAXBElement<EVChargeParameterType>(_EVChargeParameter_QNAME, EVChargeParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RetrievalMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "RetrievalMethod")
    public JAXBElement<RetrievalMethodType> createRetrievalMethod(RetrievalMethodType value) {
        return new JAXBElement<RetrievalMethodType>(_RetrievalMethod_QNAME, RetrievalMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeldingDetectionReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "WeldingDetectionReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<WeldingDetectionReqType> createWeldingDetectionReq(WeldingDetectionReqType value) {
        return new JAXBElement<WeldingDetectionReqType>(_WeldingDetectionReq_QNAME, WeldingDetectionReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DSAKeyValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "DSAKeyValue")
    public JAXBElement<DSAKeyValueType> createDSAKeyValue(DSAKeyValueType value) {
        return new JAXBElement<DSAKeyValueType>(_DSAKeyValue_QNAME, DSAKeyValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WeldingDetectionResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "WeldingDetectionRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<WeldingDetectionResType> createWeldingDetectionRes(WeldingDetectionResType value) {
        return new JAXBElement<WeldingDetectionResType>(_WeldingDetectionRes_QNAME, WeldingDetectionResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChargeParameterDiscoveryResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ChargeParameterDiscoveryRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ChargeParameterDiscoveryResType> createChargeParameterDiscoveryRes(ChargeParameterDiscoveryResType value) {
        return new JAXBElement<ChargeParameterDiscoveryResType>(_ChargeParameterDiscoveryRes_QNAME, ChargeParameterDiscoveryResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCEVChargeParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "DC_EVChargeParameter", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVChargeParameter")
    public JAXBElement<DCEVChargeParameterType> createDCEVChargeParameter(DCEVChargeParameterType value) {
        return new JAXBElement<DCEVChargeParameterType>(_DCEVChargeParameter_QNAME, DCEVChargeParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChargeParameterDiscoveryReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ChargeParameterDiscoveryReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ChargeParameterDiscoveryReqType> createChargeParameterDiscoveryReq(ChargeParameterDiscoveryReqType value) {
        return new JAXBElement<ChargeParameterDiscoveryReqType>(_ChargeParameterDiscoveryReq_QNAME, ChargeParameterDiscoveryReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionStopResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "SessionStopRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<SessionStopResType> createSessionStopRes(SessionStopResType value) {
        return new JAXBElement<SessionStopResType>(_SessionStopRes_QNAME, SessionStopResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SPKIDataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SPKIData")
    public JAXBElement<SPKIDataType> createSPKIData(SPKIDataType value) {
        return new JAXBElement<SPKIDataType>(_SPKIData_QNAME, SPKIDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CertificateUpdateResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CertificateUpdateRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CertificateUpdateResType> createCertificateUpdateRes(CertificateUpdateResType value) {
        return new JAXBElement<CertificateUpdateResType>(_CertificateUpdateRes_QNAME, CertificateUpdateResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EVPowerDeliveryParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "EVPowerDeliveryParameter")
    public JAXBElement<EVPowerDeliveryParameterType> createEVPowerDeliveryParameter(EVPowerDeliveryParameterType value) {
        return new JAXBElement<EVPowerDeliveryParameterType>(_EVPowerDeliveryParameter_QNAME, EVPowerDeliveryParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignatureValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignatureValue")
    public JAXBElement<SignatureValueType> createSignatureValue(SignatureValueType value) {
        return new JAXBElement<SignatureValueType>(_SignatureValue_QNAME, SignatureValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ACEVSEChargeParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "AC_EVSEChargeParameter", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVSEChargeParameter")
    public JAXBElement<ACEVSEChargeParameterType> createACEVSEChargeParameter(ACEVSEChargeParameterType value) {
        return new JAXBElement<ACEVSEChargeParameterType>(_ACEVSEChargeParameter_QNAME, ACEVSEChargeParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IntervalType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "TimeInterval")
    public JAXBElement<IntervalType> createTimeInterval(IntervalType value) {
        return new JAXBElement<IntervalType>(_TimeInterval_QNAME, IntervalType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurrentDemandReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CurrentDemandReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CurrentDemandReqType> createCurrentDemandReq(CurrentDemandReqType value) {
        return new JAXBElement<CurrentDemandReqType>(_CurrentDemandReq_QNAME, CurrentDemandReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CurrentDemandResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CurrentDemandRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CurrentDemandResType> createCurrentDemandRes(CurrentDemandResType value) {
        return new JAXBElement<CurrentDemandResType>(_CurrentDemandRes_QNAME, CurrentDemandResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCEVSEStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "DC_EVSEStatus", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVSEStatus")
    public JAXBElement<DCEVSEStatusType> createDCEVSEStatus(DCEVSEStatusType value) {
        return new JAXBElement<DCEVSEStatusType>(_DCEVSEStatus_QNAME, DCEVSEStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionStopReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "SessionStopReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<SessionStopReqType> createSessionStopReq(SessionStopReqType value) {
        return new JAXBElement<SessionStopReqType>(_SessionStopReq_QNAME, SessionStopReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ACEVSEStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "AC_EVSEStatus", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVSEStatus")
    public JAXBElement<ACEVSEStatusType> createACEVSEStatus(ACEVSEStatusType value) {
        return new JAXBElement<ACEVSEStatusType>(_ACEVSEStatus_QNAME, ACEVSEStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCEVPowerDeliveryParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "DC_EVPowerDeliveryParameter", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVPowerDeliveryParameter")
    public JAXBElement<DCEVPowerDeliveryParameterType> createDCEVPowerDeliveryParameter(DCEVPowerDeliveryParameterType value) {
        return new JAXBElement<DCEVPowerDeliveryParameterType>(_DCEVPowerDeliveryParameter_QNAME, DCEVPowerDeliveryParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KeyValueType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "KeyValue")
    public JAXBElement<KeyValueType> createKeyValue(KeyValueType value) {
        return new JAXBElement<KeyValueType>(_KeyValue_QNAME, KeyValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SAScheduleListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "SAScheduleList", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "SASchedules")
    public JAXBElement<SAScheduleListType> createSAScheduleList(SAScheduleListType value) {
        return new JAXBElement<SAScheduleListType>(_SAScheduleList_QNAME, SAScheduleListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransformsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Transforms")
    public JAXBElement<TransformsType> createTransforms(TransformsType value) {
        return new JAXBElement<TransformsType>(_Transforms_QNAME, TransformsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MeteringReceiptResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "MeteringReceiptRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<MeteringReceiptResType> createMeteringReceiptRes(MeteringReceiptResType value) {
        return new JAXBElement<MeteringReceiptResType>(_MeteringReceiptRes_QNAME, MeteringReceiptResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCEVSEChargeParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "DC_EVSEChargeParameter", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVSEChargeParameter")
    public JAXBElement<DCEVSEChargeParameterType> createDCEVSEChargeParameter(DCEVSEChargeParameterType value) {
        return new JAXBElement<DCEVSEChargeParameterType>(_DCEVSEChargeParameter_QNAME, DCEVSEChargeParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DigestMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "DigestMethod")
    public JAXBElement<DigestMethodType> createDigestMethod(DigestMethodType value) {
        return new JAXBElement<DigestMethodType>(_DigestMethod_QNAME, DigestMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PowerDeliveryReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PowerDeliveryReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PowerDeliveryReqType> createPowerDeliveryReq(PowerDeliveryReqType value) {
        return new JAXBElement<PowerDeliveryReqType>(_PowerDeliveryReq_QNAME, PowerDeliveryReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link X509DataType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509Data")
    public JAXBElement<X509DataType> createX509Data(X509DataType value) {
        return new JAXBElement<X509DataType>(_X509Data_QNAME, X509DataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PowerDeliveryResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PowerDeliveryRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PowerDeliveryResType> createPowerDeliveryRes(PowerDeliveryResType value) {
        return new JAXBElement<PowerDeliveryResType>(_PowerDeliveryRes_QNAME, PowerDeliveryResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PreChargeReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PreChargeReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PreChargeReqType> createPreChargeReq(PreChargeReqType value) {
        return new JAXBElement<PreChargeReqType>(_PreChargeReq_QNAME, PreChargeReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "KeyName")
    public JAXBElement<String> createKeyName(String value) {
        return new JAXBElement<String>(_KeyName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignatureType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Signature")
    public JAXBElement<SignatureType> createSignature(SignatureType value) {
        return new JAXBElement<SignatureType>(_Signature_QNAME, SignatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "MgmtData")
    public JAXBElement<String> createMgmtData(String value) {
        return new JAXBElement<String>(_MgmtData_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceDetailResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ServiceDetailRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ServiceDetailResType> createServiceDetailRes(ServiceDetailResType value) {
        return new JAXBElement<ServiceDetailResType>(_ServiceDetailRes_QNAME, ServiceDetailResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceDetailReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "ServiceDetailReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<ServiceDetailReqType> createServiceDetailReq(ServiceDetailReqType value) {
        return new JAXBElement<ServiceDetailReqType>(_ServiceDetailReq_QNAME, ServiceDetailReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignaturePropertiesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignatureProperties")
    public JAXBElement<SignaturePropertiesType> createSignatureProperties(SignaturePropertiesType value) {
        return new JAXBElement<SignaturePropertiesType>(_SignatureProperties_QNAME, SignaturePropertiesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CertificateInstallationResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CertificateInstallationRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CertificateInstallationResType> createCertificateInstallationRes(CertificateInstallationResType value) {
        return new JAXBElement<CertificateInstallationResType>(_CertificateInstallationRes_QNAME, CertificateInstallationResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CertificateInstallationReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CertificateInstallationReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CertificateInstallationReqType> createCertificateInstallationReq(CertificateInstallationReqType value) {
        return new JAXBElement<CertificateInstallationReqType>(_CertificateInstallationReq_QNAME, CertificateInstallationReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransformType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Transform")
    public JAXBElement<TransformType> createTransform(TransformType value) {
        return new JAXBElement<TransformType>(_Transform_QNAME, TransformType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PreChargeResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PreChargeRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PreChargeResType> createPreChargeRes(PreChargeResType value) {
        return new JAXBElement<PreChargeResType>(_PreChargeRes_QNAME, PreChargeResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorizationResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "AuthorizationRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<AuthorizationResType> createAuthorizationRes(AuthorizationResType value) {
        return new JAXBElement<AuthorizationResType>(_AuthorizationRes_QNAME, AuthorizationResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentDetailsReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PaymentDetailsReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PaymentDetailsReqType> createPaymentDetailsReq(PaymentDetailsReqType value) {
        return new JAXBElement<PaymentDetailsReqType>(_PaymentDetailsReq_QNAME, PaymentDetailsReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthorizationReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "AuthorizationReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<AuthorizationReqType> createAuthorizationReq(AuthorizationReqType value) {
        return new JAXBElement<AuthorizationReqType>(_AuthorizationReq_QNAME, AuthorizationReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DCEVStatusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "DC_EVStatus", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVStatus")
    public JAXBElement<DCEVStatusType> createDCEVStatus(DCEVStatusType value) {
        return new JAXBElement<DCEVStatusType>(_DCEVStatus_QNAME, DCEVStatusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentDetailsResType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "PaymentDetailsRes", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<PaymentDetailsResType> createPaymentDetailsRes(PaymentDetailsResType value) {
        return new JAXBElement<PaymentDetailsResType>(_PaymentDetailsRes_QNAME, PaymentDetailsResType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReferenceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Reference")
    public JAXBElement<ReferenceType> createReference(ReferenceType value) {
        return new JAXBElement<ReferenceType>(_Reference_QNAME, ReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SalesTariffEntryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "SalesTariffEntry", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "Entry")
    public JAXBElement<SalesTariffEntryType> createSalesTariffEntry(SalesTariffEntryType value) {
        return new JAXBElement<SalesTariffEntryType>(_SalesTariffEntry_QNAME, SalesTariffEntryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ACEVChargeParameterType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "AC_EVChargeParameter", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgDataTypes", substitutionHeadName = "EVChargeParameter")
    public JAXBElement<ACEVChargeParameterType> createACEVChargeParameter(ACEVChargeParameterType value) {
        return new JAXBElement<ACEVChargeParameterType>(_ACEVChargeParameter_QNAME, ACEVChargeParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "DigestValue")
    public JAXBElement<byte[]> createDigestValue(byte[] value) {
        return new JAXBElement<byte[]>(_DigestValue_QNAME, byte[].class, null, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BodyBaseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "BodyElement")
    public JAXBElement<BodyBaseType> createBodyElement(BodyBaseType value) {
        return new JAXBElement<BodyBaseType>(_BodyElement_QNAME, BodyBaseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CanonicalizationMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "CanonicalizationMethod")
    public JAXBElement<CanonicalizationMethodType> createCanonicalizationMethod(CanonicalizationMethodType value) {
        return new JAXBElement<CanonicalizationMethodType>(_CanonicalizationMethod_QNAME, CanonicalizationMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CertificateUpdateReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgBody", name = "CertificateUpdateReq", substitutionHeadNamespace = "urn:iso:15118:2:2013:MsgBody", substitutionHeadName = "BodyElement")
    public JAXBElement<CertificateUpdateReqType> createCertificateUpdateReq(CertificateUpdateReqType value) {
        return new JAXBElement<CertificateUpdateReqType>(_CertificateUpdateReq_QNAME, CertificateUpdateReqType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EntryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:iso:15118:2:2013:MsgDataTypes", name = "Entry")
    public JAXBElement<EntryType> createEntry(EntryType value) {
        return new JAXBElement<EntryType>(_Entry_QNAME, EntryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignedInfoType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SignedInfo")
    public JAXBElement<SignedInfoType> createSignedInfo(SignedInfoType value) {
        return new JAXBElement<SignedInfoType>(_SignedInfo_QNAME, SignedInfoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ManifestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "Manifest")
    public JAXBElement<ManifestType> createManifest(ManifestType value) {
        return new JAXBElement<ManifestType>(_Manifest_QNAME, ManifestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "XPath", scope = TransformType.class)
    public JAXBElement<String> createTransformTypeXPath(String value) {
        return new JAXBElement<String>(_TransformTypeXPath_QNAME, String.class, TransformType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "SPKISexp", scope = SPKIDataType.class)
    public JAXBElement<byte[]> createSPKIDataTypeSPKISexp(byte[] value) {
        return new JAXBElement<byte[]>(_SPKIDataTypeSPKISexp_QNAME, byte[].class, SPKIDataType.class, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "HMACOutputLength", scope = SignatureMethodType.class)
    public JAXBElement<BigInteger> createSignatureMethodTypeHMACOutputLength(BigInteger value) {
        return new JAXBElement<BigInteger>(_SignatureMethodTypeHMACOutputLength_QNAME, BigInteger.class, SignatureMethodType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link X509IssuerSerialType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509IssuerSerial", scope = X509DataType.class)
    public JAXBElement<X509IssuerSerialType> createX509DataTypeX509IssuerSerial(X509IssuerSerialType value) {
        return new JAXBElement<X509IssuerSerialType>(_X509DataTypeX509IssuerSerial_QNAME, X509IssuerSerialType.class, X509DataType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509CRL", scope = X509DataType.class)
    public JAXBElement<byte[]> createX509DataTypeX509CRL(byte[] value) {
        return new JAXBElement<byte[]>(_X509DataTypeX509CRL_QNAME, byte[].class, X509DataType.class, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509SubjectName", scope = X509DataType.class)
    public JAXBElement<String> createX509DataTypeX509SubjectName(String value) {
        return new JAXBElement<String>(_X509DataTypeX509SubjectName_QNAME, String.class, X509DataType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509SKI", scope = X509DataType.class)
    public JAXBElement<byte[]> createX509DataTypeX509SKI(byte[] value) {
        return new JAXBElement<byte[]>(_X509DataTypeX509SKI_QNAME, byte[].class, X509DataType.class, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "X509Certificate", scope = X509DataType.class)
    public JAXBElement<byte[]> createX509DataTypeX509Certificate(byte[] value) {
        return new JAXBElement<byte[]>(_X509DataTypeX509Certificate_QNAME, byte[].class, X509DataType.class, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "PGPKeyID", scope = PGPDataType.class)
    public JAXBElement<byte[]> createPGPDataTypePGPKeyID(byte[] value) {
        return new JAXBElement<byte[]>(_PGPDataTypePGPKeyID_QNAME, byte[].class, PGPDataType.class, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2000/09/xmldsig#", name = "PGPKeyPacket", scope = PGPDataType.class)
    public JAXBElement<byte[]> createPGPDataTypePGPKeyPacket(byte[] value) {
        return new JAXBElement<byte[]>(_PGPDataTypePGPKeyPacket_QNAME, byte[].class, PGPDataType.class, (value));
    }

}
