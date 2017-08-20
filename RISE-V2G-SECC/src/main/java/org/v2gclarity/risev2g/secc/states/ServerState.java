/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-207  V2G Clarity (Dr.-Ing. Marc Mültin) 
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
package org.v2gclarity.risev2g.secc.states;

import org.v2gclarity.risev2g.secc.evseController.IACEVSEController;
import org.v2gclarity.risev2g.secc.evseController.IDCEVSEController;
import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.SendMessage;
import org.v2gclarity.risev2g.shared.misc.State;
import org.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CableCheckResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateUpdateResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingStatusResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.CurrentDemandResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.DiffieHellmanPublickeyType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EMAIDType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EVSEProcessingType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeteringReceiptResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PhysicalValueType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PreChargeResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDetailResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionStopResType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.UnitSymbolType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.WeldingDetectionResType;

public abstract class ServerState extends State {

	public ServerState(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
	}

	public V2GCommunicationSessionSECC getCommSessionContext() {
		return (V2GCommunicationSessionSECC) super.getCommSessionContext();
	}
	
	
	protected boolean isIncomingMessageValid(
			Object incomingMessage, 
			Class<? extends BodyBaseType> expectedMessage,
			BodyBaseType responseMessage) {
		V2GMessage v2gMessage = null;
		ResponseCodeType responseCode = null;
		
		// Check if incoming request is a V2GMessage
		if (incomingMessage instanceof V2GMessage) {
			v2gMessage = (V2GMessage) incomingMessage;
			
			// Check if incoming request is expected
			if (expectedMessage.isAssignableFrom(v2gMessage.getBody().getBodyElement().getValue().getClass())) {
				getLogger().debug(v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName().replace("Type", "") + " received");
				
				// Check for correct session ID
				responseCode = getCommSessionContext().checkSessionID(v2gMessage.getHeader());
			} else {
				getLogger().fatal("Invalid message (" + v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName() + 
						  		  ") at this state (" + this.getClass().getSimpleName() + ")");
				responseCode = ResponseCodeType.FAILED_SEQUENCE_ERROR;
			}
		} else {
			getLogger().fatal("Incoming message is not a V2GMessage");
			responseCode = ResponseCodeType.FAILED_SEQUENCE_ERROR;
		}
		
		switch (responseMessage.getClass().getSimpleName()) {
		case "SessionSetupResType":
			((SessionSetupResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ServiceDiscoveryResType":
			((ServiceDiscoveryResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ServiceDetailResType":
			((ServiceDetailResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PaymentServiceSelectionResType":
			((PaymentServiceSelectionResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PaymentDetailsResType":
			((PaymentDetailsResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CertificateInstallationResType":
			((CertificateInstallationResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CertificateUpdateResType":
			((CertificateUpdateResType) responseMessage).setResponseCode(responseCode);
			break;
		case "AuthorizationResType":
			((AuthorizationResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ChargeParameterDiscoveryResType":
			((ChargeParameterDiscoveryResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CableCheckResType": 
			((CableCheckResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PreChargeResType": 
			((PreChargeResType) responseMessage).setResponseCode(responseCode);
			break;
		case "PowerDeliveryResType":
			((PowerDeliveryResType) responseMessage).setResponseCode(responseCode);
			break;
		case "ChargingStatusResType":
			((ChargingStatusResType) responseMessage).setResponseCode(responseCode);
			break;
		case "CurrentDemandResType":
			((CurrentDemandResType) responseMessage).setResponseCode(responseCode);
			break;
		case "MeteringReceiptResType":
			((MeteringReceiptResType) responseMessage).setResponseCode(responseCode);
			break;
		case "WeldingDetectionResType":
			((WeldingDetectionResType) responseMessage).setResponseCode(responseCode);
			break;
		case "SessionStopResType":
			((SessionStopResType) responseMessage).setResponseCode(responseCode);
			break;
		default:
			getLogger().error("Response message could not be identified");
			return false;
		}
		
		if (responseCode.toString().startsWith("OK")) return true;
		else return false;
	}
	
	public SendMessage getSendMessage(
			BodyBaseType message, 
			V2GMessages nextExpectedMessage,
			ResponseCodeType responseCode) {
		int timeout = getTimeout(message, nextExpectedMessage);
		
		if (!responseCode.value().startsWith("OK")) {
			getLogger().error("Response code '" + responseCode.value() + "' will be sent.");
			getCommSessionContext().setStopV2GCommunicationSession(true);
		}
		
		return getSendMessage(message, nextExpectedMessage, "", timeout);
	}
	
	protected SendMessage getSendMessage(
			SupportedAppProtocolRes message, 
			V2GMessages nextExpectedMessage,
			org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.ResponseCodeType responseCode) {
		String messageName = message.getClass().getSimpleName();
		
		if (!responseCode.value().substring(0, 2).toUpperCase().equals("OK")) {
			getLogger().error("Response code '" + responseCode.value() + "' will be sent.");
			getCommSessionContext().setStopV2GCommunicationSession(true);
		}
		
		getLogger().debug("Preparing to send " + messageName);
		return new SendMessage(message, getCommSessionContext().getStates().get(nextExpectedMessage), TimeRestrictions.V2G_SECC_SEQUENCE_TIMEOUT);
	}

	
	/**
	 * In case a FAILED response code is sent, the mandatory fields still need to be set with minimum required values,
	 * otherwise the EVCC's EXI decoder will raise an error.
	 * 
	 * @param response The respective response message whose mandatory fields are to be set
	 */
	protected void setMandatoryFieldsForFailedRes(BodyBaseType responseMessage, ResponseCodeType responseCode) {
		switch (responseMessage.getClass().getSimpleName()) {
		case "SessionSetupResType":
			SessionSetupResType sessionSetupRes = (SessionSetupResType) responseMessage;
			sessionSetupRes.setEVSEID(getCommSessionContext().getEvseController().getEvseID());
			sessionSetupRes.setResponseCode(responseCode);
			break;
		case "ServiceDiscoveryResType":
			ServiceDiscoveryResType serviceDiscoveryRes = (ServiceDiscoveryResType) responseMessage;
			serviceDiscoveryRes.setChargeService((new WaitForServiceDiscoveryReq(getCommSessionContext())).getChargeService());
			serviceDiscoveryRes.setPaymentOptionList(getCommSessionContext().getPaymentOptions());
			serviceDiscoveryRes.setResponseCode(responseCode);
			break;
		case "ServiceDetailResType":
			ServiceDetailResType serviceDetailRes = (ServiceDetailResType) responseMessage;
			serviceDetailRes.setServiceID(1);
			serviceDetailRes.setResponseCode(responseCode);
			break;
		case "PaymentServiceSelectionResType":
			PaymentServiceSelectionResType paymentServiceSelectionRes = (PaymentServiceSelectionResType) responseMessage;
			paymentServiceSelectionRes.setResponseCode(responseCode);
			break;
		case "PaymentDetailsResType":
			PaymentDetailsResType paymentDetailsRes = (PaymentDetailsResType) responseMessage;
			paymentDetailsRes.setEVSETimeStamp(0L);
			paymentDetailsRes.setGenChallenge(new byte[1]);
			paymentDetailsRes.setResponseCode(responseCode);
			break;
		case "CertificateInstallationResType":
			CertificateInstallationResType certificateInstallationRes = (CertificateInstallationResType) responseMessage;
			CertificateChainType saProvisioningCertificateChain = new CertificateChainType();
			saProvisioningCertificateChain.setCertificate(new byte[1]);
			certificateInstallationRes.setSAProvisioningCertificateChain(saProvisioningCertificateChain);
			
			CertificateChainType contractSignatureCertChain = new CertificateChainType();
			contractSignatureCertChain.setCertificate(new byte[1]);
			contractSignatureCertChain.setId("ID1");
			certificateInstallationRes.setContractSignatureCertChain(contractSignatureCertChain);
			
			ContractSignatureEncryptedPrivateKeyType contractSignatureEncryptedPrivateKey = new ContractSignatureEncryptedPrivateKeyType();
			contractSignatureEncryptedPrivateKey.setValue(new byte[1]);
			contractSignatureEncryptedPrivateKey.setId("ID2");
			certificateInstallationRes.setContractSignatureEncryptedPrivateKey(contractSignatureEncryptedPrivateKey);
			
			DiffieHellmanPublickeyType dhPublicKeyType = new DiffieHellmanPublickeyType();
			dhPublicKeyType.setValue(new byte[1]);
			dhPublicKeyType.setId("ID3");
			certificateInstallationRes.setDHpublickey(dhPublicKeyType);
			
			EMAIDType emaid = new EMAIDType();
			emaid.setValue("DEV2G1234512345");
			emaid.setId("ID4");
			certificateInstallationRes.setEMAID(emaid);
			
			certificateInstallationRes.setResponseCode(responseCode);
			break;
		case "CertificateUpdateResType":
			CertificateUpdateResType certificateUpdateRes = (CertificateUpdateResType) responseMessage;
			CertificateChainType saProvisioningCertificateChain2 = new CertificateChainType();
			saProvisioningCertificateChain2.setCertificate(new byte[1]);
			certificateUpdateRes.setSAProvisioningCertificateChain(saProvisioningCertificateChain2);
			
			CertificateChainType contractSignatureCertChain2 = new CertificateChainType();
			contractSignatureCertChain2.setCertificate(new byte[1]);
			contractSignatureCertChain2.setId("ID1");
			certificateUpdateRes.setContractSignatureCertChain(contractSignatureCertChain2);
			
			ContractSignatureEncryptedPrivateKeyType contractSignatureEncryptedPrivateKey2 = new ContractSignatureEncryptedPrivateKeyType();
			contractSignatureEncryptedPrivateKey2.setValue(new byte[1]);
			contractSignatureEncryptedPrivateKey2.setId("ID2");
			certificateUpdateRes.setContractSignatureEncryptedPrivateKey(contractSignatureEncryptedPrivateKey2);
			
			DiffieHellmanPublickeyType dhPublicKeyType2 = new DiffieHellmanPublickeyType();
			dhPublicKeyType2.setValue(new byte[1]);
			dhPublicKeyType2.setId("ID3");
			certificateUpdateRes.setDHpublickey(dhPublicKeyType2);
			
			EMAIDType emaid2 = new EMAIDType();
			emaid2.setValue("DEV2G1234512345");
			emaid2.setId("ID4");
			certificateUpdateRes.setEMAID(emaid2);
			
			certificateUpdateRes.setRetryCounter((short) 0);  // according to [V2G2-696] and [V2G2-928]
			certificateUpdateRes.setResponseCode(responseCode);
			break;
		case "AuthorizationResType":
			AuthorizationResType authorizationRes = (AuthorizationResType) responseMessage;
			authorizationRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
			authorizationRes.setResponseCode(responseCode);
			break;
		case "ChargeParameterDiscoveryResType":
			ChargeParameterDiscoveryResType chargeParameterDiscoveryRes = (ChargeParameterDiscoveryResType) responseMessage;
			chargeParameterDiscoveryRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
			chargeParameterDiscoveryRes.setEVSEChargeParameter(
						((IACEVSEController) getCommSessionContext().getACEvseController()).getACEVSEChargeParameter());
			chargeParameterDiscoveryRes.setResponseCode(responseCode);
			break;
		case "CableCheckResType": 
			CableCheckResType cableCheckRes = (CableCheckResType) responseMessage;
			cableCheckRes.setEVSEProcessing(EVSEProcessingType.FINISHED);
			cableCheckRes.setDCEVSEStatus(
					((IDCEVSEController) getCommSessionContext().getDCEvseController()).getDCEVSEStatus(EVSENotificationType.NONE)
					);
			cableCheckRes.setResponseCode(responseCode);
			break;
		case "PreChargeResType": 
			PreChargeResType preChargeRes = (PreChargeResType) responseMessage;
			IDCEVSEController evseController = (IDCEVSEController) getCommSessionContext().getDCEvseController();
			
			preChargeRes.setDCEVSEStatus(evseController.getDCEVSEStatus(EVSENotificationType.NONE));
			preChargeRes.setEVSEPresentVoltage(evseController.getPresentVoltage());
			preChargeRes.setResponseCode(responseCode);
			break;
		case "PowerDeliveryResType":
			PowerDeliveryResType powerDeliveryRes = (PowerDeliveryResType) responseMessage;
			(new WaitForPowerDeliveryReq(getCommSessionContext())).setEVSEStatus(powerDeliveryRes);
			powerDeliveryRes.setResponseCode(responseCode);
			break;
		case "ChargingStatusResType":
			ChargingStatusResType chargingStatusRes = (ChargingStatusResType) responseMessage;
			chargingStatusRes.setEVSEID(getCommSessionContext().getACEvseController().getEvseID());
			chargingStatusRes.setSAScheduleTupleID((short) 1);
			chargingStatusRes.setACEVSEStatus(((IACEVSEController) getCommSessionContext().getACEvseController())
						.getACEVSEStatus(EVSENotificationType.NONE)  
						);
			chargingStatusRes.setResponseCode(responseCode);
			break;
		case "CurrentDemandResType":
			CurrentDemandResType currentDemandRes = (CurrentDemandResType) responseMessage;
			IDCEVSEController evseController2 = (IDCEVSEController) getCommSessionContext().getDCEvseController();
			
			PhysicalValueType physicalValueType = new PhysicalValueType();
			physicalValueType.setMultiplier(new Byte("0"));
			physicalValueType.setUnit(UnitSymbolType.V);  // does not matter which unit symbol if FAILED response is sent
			physicalValueType.setValue((short) 1);
			
			currentDemandRes.setDCEVSEStatus(evseController2.getDCEVSEStatus(EVSENotificationType.NONE));
			currentDemandRes.setEVSEPresentVoltage(physicalValueType);
			currentDemandRes.setEVSEPresentCurrent(physicalValueType);
			currentDemandRes.setEVSECurrentLimitAchieved(false);
			currentDemandRes.setEVSEVoltageLimitAchieved(false);
			currentDemandRes.setEVSEPowerLimitAchieved(false);
			currentDemandRes.setEVSEID(evseController2.getEvseID());
			currentDemandRes.setSAScheduleTupleID((short) 1); 
			
			currentDemandRes.setResponseCode(responseCode);
			break;
		case "MeteringReceiptResType":
			MeteringReceiptResType meteringReceiptRes = (MeteringReceiptResType) responseMessage;
			(new WaitForMeteringReceiptReq(getCommSessionContext())).setEVSEStatus(meteringReceiptRes); 	
			meteringReceiptRes.setResponseCode(responseCode);
			break;
		case "WeldingDetectionResType":
			WeldingDetectionResType weldingDetectionRes = (WeldingDetectionResType) responseMessage;
			IDCEVSEController evseController3 = (IDCEVSEController) getCommSessionContext().getDCEvseController();
			
			weldingDetectionRes.setDCEVSEStatus(evseController3.getDCEVSEStatus(EVSENotificationType.NONE));
			weldingDetectionRes.setEVSEPresentVoltage(evseController3.getPresentVoltage());
			weldingDetectionRes.setResponseCode(responseCode);
			break;
		case "SessionStopResType":
			SessionStopResType sessionStopRes = (SessionStopResType) responseMessage;
			sessionStopRes.setResponseCode(responseCode);
			break;
		default:
			getLogger().error("Response message could not be identified");
		}
	}
	
	
	protected BodyBaseType getSequenceErrorResMessage(Object incomingMessage) {
		if (incomingMessage instanceof V2GMessage) {
			V2GMessage v2gMessage = (V2GMessage) incomingMessage;
			String className = v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName();
			BodyBaseType responseMessage = null;
			
			switch (className) {
			case "SessionSetupReqType":
				SessionSetupResType sessionSetupRes = new SessionSetupResType();
				sessionSetupRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = sessionSetupRes;
				break;
			case "ServiceDiscoveryReqType":
				ServiceDiscoveryResType serviceDiscoveryRes = new ServiceDiscoveryResType();
				serviceDiscoveryRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = serviceDiscoveryRes;
				break;
			case "ServiceDetailReqType":
				ServiceDetailResType serviceDetailRes = new ServiceDetailResType();
				serviceDetailRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = serviceDetailRes;
				break;
			case "PaymentServiceSelectionReqType":
				PaymentServiceSelectionResType paymentServiceSelectionRes = new PaymentServiceSelectionResType();
				paymentServiceSelectionRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = paymentServiceSelectionRes;
				break;
			case "PaymentDetailsReqType":
				PaymentDetailsResType paymentDetailsRes = new PaymentDetailsResType();
				paymentDetailsRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = paymentDetailsRes;
				break;
			case "CertificateInstallationReqType":
				CertificateInstallationResType certificateInstallationRes = new CertificateInstallationResType();
				certificateInstallationRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = certificateInstallationRes;
				break;
			case "CertificateUpdateReqType":
				CertificateUpdateResType certificateUpdateRes = new CertificateUpdateResType();
				certificateUpdateRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = certificateUpdateRes;
				break;
			case "AuthorizationReqType":
				AuthorizationResType authorizationRes = new AuthorizationResType();
				authorizationRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = authorizationRes;
				break;
			case "ChargeParameterDiscoveryReqType":
				ChargeParameterDiscoveryResType chargeParameterDiscoveryRes = new ChargeParameterDiscoveryResType();
				chargeParameterDiscoveryRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = chargeParameterDiscoveryRes;
				break;
			case "CableCheckReqType": 
				CableCheckResType cableCheckRes = new CableCheckResType();
				cableCheckRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = cableCheckRes;
				break;
			case "PreChargeReqType": 
				PreChargeResType preChargeRes = new PreChargeResType();
				preChargeRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = preChargeRes;
				break;
			case "PowerDeliveryReqType":
				PowerDeliveryResType powerDeliveryResType = new PowerDeliveryResType();
				powerDeliveryResType.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = powerDeliveryResType;
				break;
			case "ChargingStatusReqType":
				ChargingStatusResType chargingStatusRes = new ChargingStatusResType();
				chargingStatusRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = chargingStatusRes;
				break;
			case "CurrentDemandReqType":
				CurrentDemandResType currentDemandRes = new CurrentDemandResType();
				currentDemandRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = currentDemandRes;
				break;
			case "MeteringReceiptReqType":
				MeteringReceiptResType meteringReceiptRes = new MeteringReceiptResType();
				meteringReceiptRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = meteringReceiptRes;
				break;
			case "WeldingDetectionReqType":
				WeldingDetectionResType weldingDetectionRes = new WeldingDetectionResType();
				weldingDetectionRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = weldingDetectionRes;
				break;
			case "SessionStopReqType":
				SessionStopResType sessionStopRes = new SessionStopResType();
				sessionStopRes.setResponseCode(ResponseCodeType.FAILED_SEQUENCE_ERROR);
				responseMessage = sessionStopRes;
				break;
			default:
				getLogger().error("Response message could not be identified");
			}
			
			setMandatoryFieldsForFailedRes(responseMessage, ResponseCodeType.FAILED_SEQUENCE_ERROR);
			
			return responseMessage;
		} else {
			return null;
		}
	}
	
	
	protected BodyBaseType getSequenceErrorResMessage(
			BodyBaseType currentStateRes, 
			Object incomingMessage) {
			BodyBaseType responseMessage = getSequenceErrorResMessage(incomingMessage);
			
			// Check in case the switch statement did not match a proper ISO 15118 request message
			if (responseMessage != null) {
				return responseMessage;
			} else {
				setMandatoryFieldsForFailedRes(currentStateRes, ResponseCodeType.FAILED_SEQUENCE_ERROR);
				return currentStateRes;
			}
	}
	
	/**
	 * Needed for the ForkState to get the respective response message which can be used to instantiate a 
	 * SendMessage() object in case of a sequence error
	 */
	public abstract BodyBaseType getResponseMessage();
}
