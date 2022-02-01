/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
package com.v2gclarity.risev2g.evcc.states;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.ListIterator;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.v2gclarity.risev2g.evcc.evController.IACEVController;
import com.v2gclarity.risev2g.evcc.evController.IDCEVController;
import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.shared.enumerations.CPStates;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.misc.State;
import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.AuthorizationResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CableCheckReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CableCheckResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateInstallationResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateUpdateResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeProgressType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingSessionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingStatusResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CurrentDemandReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CurrentDemandResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DCEVPowerDeliveryParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EMAIDType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MessageHeaderType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.MeteringReceiptResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentDetailsReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentDetailsResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentServiceSelectionResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PowerDeliveryReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PowerDeliveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PreChargeResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDetailReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDetailResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionSetupResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionStopReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SessionStopResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.WeldingDetectionResType;

/**
 * Some request messages are to be sent from different states which makes it more convenient (having
 * less code and being less error-prone) to keep the creation of those messages in one single class.
 */
public abstract class ClientState extends State {
 
	public ClientState(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	public V2GCommunicationSessionEVCC getCommSessionContext() {
		return (V2GCommunicationSessionEVCC) super.getCommSessionContext();
	}
	
	
	protected boolean isIncomingMessageValid(Object incomingMessage, Class<? extends BodyBaseType> expectedMessage) {
		V2GMessage v2gMessage = null;
		
		if (incomingMessage instanceof V2GMessage) {
			v2gMessage = (V2GMessage) incomingMessage;
			
			if (!expectedMessage.isAssignableFrom(v2gMessage.getBody().getBodyElement().getValue().getClass())) {
				getLogger().fatal("Invalid message (" + v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName() + 
	  							  ") at this state (" + this.getClass().getSimpleName() + ")");
				return false;
			} else {
				getLogger().debug(v2gMessage.getBody().getBodyElement().getValue().getClass().getSimpleName().replace("Type", "") + " received");
				if (!isHeaderOK(v2gMessage.getHeader())) return false;
				if (!isResponseCodeOK(v2gMessage)) return false;
				return true;
			}
		} else {
			getLogger().fatal("Incoming message is not a V2GMessage");
			return false;
		}
	}
	
	
	/**
	 * Performs the following checks:
	 * - is the returned session ID the same as the one saved by the EVCC?
	 * - does the EVCC need to react to a possibly set notification?
	 * - does the EVCC need to check the signature?
	 * 
	 * @param header The header of the V2GMessage
	 */
	private boolean isHeaderOK(MessageHeaderType header) {
		// Check sessionID (only if not at state WaitForSessionSetupRes)
		if (!this.equals(getCommSessionContext().getStates().get(V2GMessages.SESSION_SETUP_RES)) &&
			!Arrays.equals(header.getSessionID(), getCommSessionContext().getSessionID())) {
			getLogger().error("Session ID is invalid: " +
							  "expected " + ByteUtils.toLongFromByteArray(getCommSessionContext().getSessionID()) + 
							  ", received " + ByteUtils.toLongFromByteArray(header.getSessionID()));
			return false;
		}
		
		if (header.getNotification() != null) {
			// TODO react on the several notifications
		}
		
		/*
		 * If a signature is present, it is placed in the header. However, not all messages have a 
		 * signature. Therefore, the signature validation is to be done in the respective state itself.
		 */
		
		return true;
	}
	
	
	private boolean isResponseCodeOK(V2GMessage responseMessage) {
		BodyBaseType bbt = ((V2GMessage) responseMessage).getBody().getBodyElement().getValue();
		ResponseCodeType v2gMessageRCT = null;
		
		switch (bbt.getClass().getSimpleName()) {
		case "SessionSetupResType":
			v2gMessageRCT = ((SessionSetupResType) bbt).getResponseCode();
			break;
		case "ServiceDiscoveryResType":
			v2gMessageRCT = ((ServiceDiscoveryResType) bbt).getResponseCode();
			break;
		case "ServiceDetailResType":
			v2gMessageRCT = ((ServiceDetailResType) bbt).getResponseCode();
			break;
		case "PaymentServiceSelectionResType":
			v2gMessageRCT = ((PaymentServiceSelectionResType) bbt).getResponseCode();
			break;
		case "PaymentDetailsResType":
			v2gMessageRCT = ((PaymentDetailsResType) bbt).getResponseCode();
			break;
		case "CertificateInstallationResType":
			v2gMessageRCT = ((CertificateInstallationResType) bbt).getResponseCode();
			break;
		case "CertificateUpdateResType":
			v2gMessageRCT = ((CertificateUpdateResType) bbt).getResponseCode();
			break;
		case "AuthorizationResType":
			v2gMessageRCT = ((AuthorizationResType) bbt).getResponseCode();
			break;
		case "ChargeParameterDiscoveryResType":
			v2gMessageRCT = ((ChargeParameterDiscoveryResType) bbt).getResponseCode();
			break;
		case "CableCheckResType":
			v2gMessageRCT = ((CableCheckResType) bbt).getResponseCode();
			break;
		case "PreChargeResType":
			v2gMessageRCT = ((PreChargeResType) bbt).getResponseCode();
			break;
		case "PowerDeliveryResType":
			v2gMessageRCT = ((PowerDeliveryResType) bbt).getResponseCode();
			break;
		case "ChargingStatusResType":
			v2gMessageRCT = ((ChargingStatusResType) bbt).getResponseCode();
			break;
		case "CurrentDemandResType":
			v2gMessageRCT = ((CurrentDemandResType) bbt).getResponseCode();
			break;
		case "MeteringReceiptResType":
			v2gMessageRCT = ((MeteringReceiptResType) bbt).getResponseCode();
			break;
		case "WeldingDetectionResType":
			v2gMessageRCT = ((WeldingDetectionResType) bbt).getResponseCode();
			break;
		case "SessionStopResType":
			v2gMessageRCT = ((SessionStopResType) bbt).getResponseCode();
			break;
		default:
			getLogger().error("Response message could not be identified");
			return false;
		}
		
		if (v2gMessageRCT.toString().startsWith("OK")) return true;
		else {
			getLogger().error("Negative response code " + v2gMessageRCT.toString());
			return false;
		}
	}
	
	
	/**
	 * A ServiceDetailReq needs to be generated from several states:
	 * - WaitForServiceDiscoveryRes 
	 * - WaitForServiceDetailRes
	 *
	 * Checks if the list of value added services (VAS) which are to be used contains service IDs. Those
	 * service IDs can be used in a ServiceDetailReq to request more details about the service.
	 * Each time a ServiceDetailReq is created, the respective service ID is deleted from the list.
	 * 
	 * @return A ServiceDetailReq with a service ID whose details are requested, if the list of service IDs
	 * 		  is not empty. Null otherwise.
	 */
	protected ServiceDetailReqType getServiceDetailReq() {
		if (getCommSessionContext().getServiceDetailsToBeRequested().size() > 0) {
			ListIterator<Integer> listIterator = getCommSessionContext().getServiceDetailsToBeRequested().listIterator();
			
			ServiceDetailReqType serviceDetailReq = new ServiceDetailReqType();
			serviceDetailReq.setServiceID(listIterator.next());
			
			listIterator.remove();
			
			return serviceDetailReq;
		}
		
		return null;
	}
	
	
	/**
	 * A ServiceDetailReq needs to be generated from several states:
	 * - WaitForServiceDiscoveryRes 
	 * - WaitForServiceDetailRes
	 */
	protected PaymentServiceSelectionReqType paymentServiceSelectionReq() {
		PaymentServiceSelectionReqType paymentServiceSelectionReq = new PaymentServiceSelectionReqType();
		paymentServiceSelectionReq.setSelectedPaymentOption(getCommSessionContext().getSelectedPaymentOption());
		
		return paymentServiceSelectionReq;
	}
	
	
	/**
	 * An AuthorizationReq needs to be generated from several states:
	 * - WaitForPaymentServiceSelectionRes (no genChallege)
	 * - WaitForPaymentDetailsRes (genChallenge)
	 * - WaitForAuthorizationRes (no genChallenge, EVSE is still processing)
	 *
	 * @return An AuthorizationReq, either empty or with a set genChallenge and ID depending on input parameter
	 */
	protected AuthorizationReqType getAuthorizationReq(byte[] genChallenge) {
		AuthorizationReqType authorizationReq = new AuthorizationReqType();
		
		if (genChallenge != null) {
			authorizationReq.setGenChallenge(genChallenge);
			/*
			 * Experience from the test symposium in San Diego (April 2016):
			 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
			 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
			 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
			 */
			authorizationReq.setId("ID1");
		}
		
		return authorizationReq;
	}
	
	
	/**
	 * A CableCheckReq needs to be generated from several states:
	 * - WaitForChargeParameterDiscoveryRes 
	 * - WaitForCableCheckRes (EVSEProcessing = ONGOING)
	 *
	 * @return A CableCheckReq
	 */
	protected CableCheckReqType getCableCheckReq() {
		CableCheckReqType cableCheckReq = new CableCheckReqType();
		cableCheckReq.setDCEVStatus(((IDCEVController) getCommSessionContext().getEvController()).getDCEVStatus());
		
		return cableCheckReq;
	}
	
	
	/**
	 * A CurrentDemandReq needs to be generated from several states:
	 * - WaitForCurrentDemandRes (the initial CurrentDemandReq message)
	 * - WaitForMeteringReceiptRes 
	 *
	 * @return A CurrentDemandReq message
	 */
	protected CurrentDemandReqType getCurrentDemandReq() {
		IDCEVController evController = (IDCEVController) getCommSessionContext().getEvController();
		
		CurrentDemandReqType currentDemandReq = new CurrentDemandReqType();
		currentDemandReq.setBulkChargingComplete(evController.isBulkChargingComplete());
		currentDemandReq.setChargingComplete(evController.isChargingComplete());
		currentDemandReq.setDCEVStatus(evController.getDCEVStatus());
		currentDemandReq.setEVMaximumCurrentLimit(evController.getMaximumCurrentLimit());
		currentDemandReq.setEVMaximumPowerLimit(evController.getMaximumPowerLimit());
		currentDemandReq.setEVMaximumVoltageLimit(evController.getMaximumVoltageLimit());
		currentDemandReq.setEVTargetCurrent(evController.getTargetCurrent());
		currentDemandReq.setEVTargetVoltage(evController.getTargetVoltage());
		currentDemandReq.setRemainingTimeToBulkSoC(evController.getRemainingTimeToBulkSOC());
		currentDemandReq.setRemainingTimeToFullSoC(evController.getRemainingTimeToFullSOC());
		
		return currentDemandReq;
	}
	
	
	/**
	 * A ChargeParameterDiscoveryReq needs to be generated from several states:
	 * - WaitForAuthorizationRes (the initial ChargeParameterDiscoveryReq)
	 * - WaitForPowerDeliveryRes (in case AC_EVSEStatus requests a renegotiation)
	 * - WaitForChargingStatusRes (in case AC_EVSEStatus requests a renegotiation)
	 *
	 * @return A ChargeParameterDiscoveryReq which itself consists of several complex datatypes.
	 */
	protected ChargeParameterDiscoveryReqType getChargeParameterDiscoveryReq() {
		ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq = new ChargeParameterDiscoveryReqType();
		
		// Optionally limit the number of entries in the SAScheduleTuple by setting MaxEntriesSAScheduleTuple
		
		chargeParameterDiscoveryReq.setRequestedEnergyTransferMode(getRequestedEnergyTransferMode());
		
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC"))
			chargeParameterDiscoveryReq.setEVChargeParameter(((IACEVController) getCommSessionContext().getEvController()).getACEVChargeParamter());
		else
			chargeParameterDiscoveryReq.setEVChargeParameter(((IDCEVController) getCommSessionContext().getEvController()).getDCEVChargeParamter());
		
		return chargeParameterDiscoveryReq;
	}
	
	
	/**
	 * A PaymentServiceSelectionReq needs to be generated from several states:
	 * - WaitForServiceDiscoveryRes
	 * - WaitForServiceDetailRes
	 * 
	 * @return A PaymentServiceSelectionReq
	 */
	protected PaymentServiceSelectionReqType getPaymentServiceSelectionReq() {
		PaymentServiceSelectionReqType paymentServiceSelectionReq = new PaymentServiceSelectionReqType();
		paymentServiceSelectionReq.setSelectedPaymentOption(getCommSessionContext().getSelectedPaymentOption());
		paymentServiceSelectionReq.setSelectedServiceList(getCommSessionContext().getSelectedServices());
		
		return paymentServiceSelectionReq;
	}
	
	
	/**
	 * A PaymentDetailsReq needs to be generated from several states:
	 * - WaitForPaymentServiceSelectionRes
	 * - WaitForCertificateInstallationRes
	 * - WaitForCertificateUpdateRes
	 * 
	 * @return A PaymentDetailsReq
	 */
	protected PaymentDetailsReqType getPaymentDetailsReq() {
		KeyStore evccKeyStore = SecurityUtils.getKeyStore(
				GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		PaymentDetailsReqType paymentDetailsReq = new PaymentDetailsReqType();
		
		EMAIDType emaid = SecurityUtils.getEMAID(GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		
		if (emaid != null) {
			paymentDetailsReq.setEMAID(SecurityUtils.getEMAID(GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()).getValue());
			paymentDetailsReq.setContractSignatureCertChain(SecurityUtils.getCertificateChain(
							evccKeyStore, GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString()));
		}
		
		return paymentDetailsReq;
	}
	
	
	/**
	 * A PowerDeliveryReq needs to be generated from several states:
	 * - WaitForChargeParameterDiscoveryRes 
	 * - WaitForChargingStatusRes 
	 * - WaitForMeteringReceiptRes 
	 * 
	 * @param chargeProgress Indicates whether to START a charging session, RENEGOTIATE charing parameters
	 * 						 or STOP the charging session 
	 * @return A ChargeParameterDiscoveryReq which itself consists of several complex datatypes.
	 */
	protected PowerDeliveryReqType getPowerDeliveryReq(ChargeProgressType chargeProgress) {
		PowerDeliveryReqType powerDeliveryReq = new PowerDeliveryReqType();
		
		if (chargeProgress.equals(ChargeProgressType.START)) {
			// Signal needed state change after sending PowerDeliveryReq in AC charging mode 
			if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC"))
				getCommSessionContext().setChangeToState(CPStates.STATE_C);
			
			ChargingProfileType chargingProfile = getCommSessionContext().getEvController().getChargingProfile();
			powerDeliveryReq.setChargingProfile(chargingProfile);
			
			getCommSessionContext().setChargingProfile(chargingProfile);
		} else if (chargeProgress.equals(ChargeProgressType.STOP)) {
			// Signal needed state change after sending PowerDeliveryReq in AC charging mode 
			if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC"))
				getCommSessionContext().setChangeToState(CPStates.STATE_B);
		}
		
		powerDeliveryReq.setChargeProgress(chargeProgress);
		powerDeliveryReq.setSAScheduleTupleID(getCommSessionContext().getEvController().getChosenSAScheduleTupleID());	
		
		// Set DC_EVPowerDeliveryParameter if in DC charging mode
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
			/*
			 * The MessageHandler method getJAXBElement() cannot be used here because of the difference in the
			 * class name (DCEVPowerDeliveryParameter) and the name in the XSD (DC_EVPowerDeliveryParameter)
			 */
			JAXBElement<DCEVPowerDeliveryParameterType> jaxbDcEvPowerDeliveryParameter = new JAXBElement<>(new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVPowerDeliveryParameter"), 
					DCEVPowerDeliveryParameterType.class, 
					((IDCEVController) getCommSessionContext().getEvController()).getEVPowerDeliveryParameter());
			powerDeliveryReq.setEVPowerDeliveryParameter(jaxbDcEvPowerDeliveryParameter);
		}
		
		return powerDeliveryReq;
	}
	
	
	/**
	 * A SessionStopReq needs to be generated from several states:
	 * - WaitForPowerDeliveryRes
	 * - WaitForWeldingDetectionRes
	 * 
	 * @return A SessionStopReq message
	 */
	protected SessionStopReqType getSessionStopReq(ChargingSessionType chargingSessionType) {
		SessionStopReqType sessionStopReq = new SessionStopReqType();
		sessionStopReq.setChargingSession(chargingSessionType);
		
		return sessionStopReq;
	}
		
		
	protected EnergyTransferModeType getRequestedEnergyTransferMode() {
		EnergyTransferModeType requestedEnergyTransferMode = null;
		
		// Check if an EnergyTransferModeType has been requested in a previously paused session 
		if (getCommSessionContext().isOldSessionJoined()) 
			requestedEnergyTransferMode = (EnergyTransferModeType) MiscUtils.getPropertyValue("energy.transfermode.requested");
			
		if (requestedEnergyTransferMode == null) 
			requestedEnergyTransferMode = getCommSessionContext().getEvController().getRequestedEnergyTransferMode();
		
		// We need to save the requested energy transfer mode in the session variable to be able to store in the properties file during pausing
		getCommSessionContext().setRequestedEnergyTransferMode(requestedEnergyTransferMode);
				
		return requestedEnergyTransferMode;	
	}
	
}
