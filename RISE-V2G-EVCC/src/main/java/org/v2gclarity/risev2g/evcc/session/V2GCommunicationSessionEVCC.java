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
package org.v2gclarity.risev2g.evcc.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.v2gclarity.risev2g.evcc.evController.DummyEVController;
import org.v2gclarity.risev2g.evcc.evController.IEVController;
import org.v2gclarity.risev2g.evcc.states.WaitForAuthorizationRes;
import org.v2gclarity.risev2g.evcc.states.WaitForCableCheckRes;
import org.v2gclarity.risev2g.evcc.states.WaitForCertificateInstallationRes;
import org.v2gclarity.risev2g.evcc.states.WaitForCertificateUpdateRes;
import org.v2gclarity.risev2g.evcc.states.WaitForChargeParameterDiscoveryRes;
import org.v2gclarity.risev2g.evcc.states.WaitForChargingStatusRes;
import org.v2gclarity.risev2g.evcc.states.WaitForCurrentDemandRes;
import org.v2gclarity.risev2g.evcc.states.WaitForMeteringReceiptRes;
import org.v2gclarity.risev2g.evcc.states.WaitForPaymentDetailsRes;
import org.v2gclarity.risev2g.evcc.states.WaitForPaymentServiceSelectionRes;
import org.v2gclarity.risev2g.evcc.states.WaitForPowerDeliveryRes;
import org.v2gclarity.risev2g.evcc.states.WaitForPreChargeRes;
import org.v2gclarity.risev2g.evcc.states.WaitForServiceDetailRes;
import org.v2gclarity.risev2g.evcc.states.WaitForServiceDiscoveryRes;
import org.v2gclarity.risev2g.evcc.states.WaitForSessionSetupRes;
import org.v2gclarity.risev2g.evcc.states.WaitForSessionStopRes;
import org.v2gclarity.risev2g.evcc.states.WaitForSupportedAppProtocolRes;
import org.v2gclarity.risev2g.evcc.states.WaitForWeldingDetectionRes;
import org.v2gclarity.risev2g.evcc.transportLayer.StatefulTransportLayerClient;
import org.v2gclarity.risev2g.evcc.transportLayer.TCPClient;
import org.v2gclarity.risev2g.evcc.transportLayer.TLSClient;
import org.v2gclarity.risev2g.shared.enumerations.CPStates;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ChangeProcessingState;
import org.v2gclarity.risev2g.shared.messageHandling.PauseSession;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.messageHandling.SendMessage;
import org.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import org.v2gclarity.risev2g.shared.misc.V2GCommunicationSession;
import org.v2gclarity.risev2g.shared.misc.V2GTPMessage;
import org.v2gclarity.risev2g.shared.utils.SecurityUtils.ContractCertificateStatus;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.AppProtocolType;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargeParameterDiscoveryReqType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ChargingProfileType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SelectedServiceListType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.SelectedServiceType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceListType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

// The state machine
public class V2GCommunicationSessionEVCC extends V2GCommunicationSession implements Observer {
	
	private long v2gEVCCCommunicationSetupTimer;
	private String evseID;
	private PaymentOptionType selectedPaymentOption;
	private ReactionToIncomingMessage reactionToIncomingMessage;
	/* 
	 * chargeParameterDiscoveryReq is saved in the session because it might need to be resent in case 
	 * the EVSEProcessing parameter of the respective response message is set to "Ongoing"
	 * (saves some processing time)
	 */
	private ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq;
	private boolean stopChargingRequested;
	private boolean renegotiationRequested;
	private boolean pausingV2GCommSession;
	private ChargingProfileType chargingProfile;
	private ServiceListType offeredServices;
	private SelectedServiceListType selectedServices; 
	private ArrayList<Short> serviceDetailsToBeRequested;
	private EnergyTransferModeType requestedEnergyTransferMode;
	private long evseScheduleReceived; // The timestamp of receiving the SAScheduleList from the EVSE, is used as a reference
	private List<AppProtocolType> supportedAppProtocols;
	private AppProtocolType chosenAppProtocol;
	private boolean oldSessionJoined;
	private IEVController evController;
	private long evseTimeStamp;
	private SAScheduleListType saSchedules;
	private long saSchedulesReceived;
	private CPStates changeToState; // signals a needed state change (checked when sending the request message)
	private StatefulTransportLayerClient transportLayerClient;
	private ContractCertificateStatus contractCertStatus;
	
	public V2GCommunicationSessionEVCC(StatefulTransportLayerClient transportLayerClient) {
		setTransportLayerClient(transportLayerClient);
		
		getStates().put(V2GMessages.SUPPORTED_APP_PROTOCOL_RES, new WaitForSupportedAppProtocolRes(this));
		getStates().put(V2GMessages.SESSION_SETUP_RES, new WaitForSessionSetupRes(this));
		getStates().put(V2GMessages.SERVICE_DISCOVERY_RES, new WaitForServiceDiscoveryRes(this));
		getStates().put(V2GMessages.SERVICE_DETAIL_RES, new WaitForServiceDetailRes(this));
		getStates().put(V2GMessages.PAYMENT_SERVICE_SELECTION_RES, new WaitForPaymentServiceSelectionRes(this));
		getStates().put(V2GMessages.CERTIFICATE_INSTALLATION_RES, new WaitForCertificateInstallationRes(this));
		getStates().put(V2GMessages.CERTIFICATE_UPDATE_RES, new WaitForCertificateUpdateRes(this));
		getStates().put(V2GMessages.PAYMENT_DETAILS_RES, new WaitForPaymentDetailsRes(this));
		getStates().put(V2GMessages.AUTHORIZATION_RES, new WaitForAuthorizationRes(this));
		getStates().put(V2GMessages.CHARGE_PARAMETER_DISCOVERY_RES, new WaitForChargeParameterDiscoveryRes(this));
		getStates().put(V2GMessages.CABLE_CHECK_RES, new WaitForCableCheckRes(this));
		getStates().put(V2GMessages.PRE_CHARGE_RES, new WaitForPreChargeRes(this));
		getStates().put(V2GMessages.POWER_DELIVERY_RES, new WaitForPowerDeliveryRes(this));
		getStates().put(V2GMessages.CHARGING_STATUS_RES, new WaitForChargingStatusRes(this));
		getStates().put(V2GMessages.CURRENT_DEMAND_RES, new WaitForCurrentDemandRes(this));
		getStates().put(V2GMessages.METERING_RECEIPT_RES, new WaitForMeteringReceiptRes(this));
		getStates().put(V2GMessages.WELDING_DETECTION_RES, new WaitForWeldingDetectionRes(this));
		getStates().put(V2GMessages.SESSION_STOP_RES, new WaitForSessionStopRes(this));
		
		setStartState(getStates().get(V2GMessages.SUPPORTED_APP_PROTOCOL_RES));
		setCurrentState(getStartState());
		
		// configure which EV controller implementation to use
		// TODO the EV controller needs to run as a separate Thread (to receive notifications from the EV and to avoid blocking calls to the controller)
		setEvController(new DummyEVController(this));
		
		/*
		 * Is needed for measuring the time span between transition to state B (plug-in) and receipt 
		 * of a SessionSetupRes (see V2G_EVCC_COMMUNICATION_SETUP_TIMEOUT in TimeRestrictions.java)
		 * TODO check if this timing requirement is still up to date
		 */
		setV2gEVCCCommunicationSetupTimer(System.currentTimeMillis());
		
		// Set default value for contract certificate status to UNKNOWN
		setContractCertStatus(ContractCertificateStatus.UNKNOWN);
			
		getLogger().debug("\n*******************************************" +
						  "\n* New V2G communication session initialized" +
						  "\n*******************************************");
	}
	
	
	@Override
	public void update(Observable obs, Object obj) {
		if ((obs instanceof TCPClient || obs instanceof TLSClient) && obj instanceof byte[]) {
			setV2gTpMessage(new V2GTPMessage((byte[]) obj));
			
			if (getMessageHandler().isV2GTPMessageValid(getV2gTpMessage())) {
				/*
				 * We need to decide which schema to use for decoding the EXI encoded message. Only 
				 * the supportedAppProtocolReq/Res message uses a different schema
				 */
				if (getCurrentState().equals(getStates().get(V2GMessages.SUPPORTED_APP_PROTOCOL_RES))) {
					obj = (SupportedAppProtocolRes) getMessageHandler().exiToSuppAppProtocolMsg(getV2gTpMessage().getPayload());
				} else {
					obj = (V2GMessage) getMessageHandler().exiToV2gMsg(getV2gTpMessage().getPayload());
				}
				
				processReaction(getCurrentState().processIncomingMessage(obj));
			} else {
				terminateSession("Received incoming message is not a valid V2GTPMessage", false);
			}
		} else if ((obs instanceof TCPClient || obs instanceof TLSClient) && obj == null) {
			terminateSession("Transport layer has notified an error", false);
		} else {
			getLogger().warn("Notification received, but sending entity or received object not identifiable");
		}
	}
	
	
	private void processReaction(ReactionToIncomingMessage reactionToIncomingMessage) {
		if (reactionToIncomingMessage instanceof SendMessage) {
			send((SendMessage) reactionToIncomingMessage);
		} else if (reactionToIncomingMessage instanceof TerminateSession) {
			deleteSessionProperties();
			terminateSession((TerminateSession) reactionToIncomingMessage);
		} else if (reactionToIncomingMessage instanceof PauseSession) {
			saveSessionProperties();
			pauseSession((PauseSession) reactionToIncomingMessage);
		} else if (reactionToIncomingMessage instanceof ChangeProcessingState) {
			setCurrentState(((ChangeProcessingState) reactionToIncomingMessage).getNewState());
			processReaction(
					getCurrentState().processIncomingMessage(reactionToIncomingMessage)
			); // TODO ist das korrekt?! bspw. wenn Renegotiation angefragt wird von EVSE?
		} else {
			terminateSession("Reaction to incoming message is undefined", false);
		}
	}
	
	
	public void send(SendMessage sendingParams) {
		// Only EXI encoded messages starting from SessionSetupReq will be sent here
		setV2gTpMessage(new V2GTPMessage(GlobalValues.V2GTP_VERSION_1_IS.getByteValue(), 
						GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue(),
						(byte[]) getMessageHandler().v2gMsgToExi(sendingParams.getPayload()))
					   );

		getTransportLayerClient().send(getV2gTpMessage(), sendingParams.getTimeout());
		
		// Check for necessary CP state change (see [V2G2-847])
		if (getChangeToState() != null) {
			if (getEvController().setCPState(getChangeToState())) setChangeToState(null);
			else terminateSession("State change to " + getChangeToState().toString() + " not successful", false);
		}
		
		if (sendingParams.getNextState() != null) {
			setCurrentState(sendingParams.getNextState());
		} else {
			terminateSession("State machine interrupted, no new state provided", true);
		}
	}
	
	
	private void saveSessionProperties() {
		// TODO save respective parameters to properties file
	}
	
	
	private void deleteSessionProperties() {
		// TODO delete the respective parameters from properties file
	}

	
	private void setV2gEVCCCommunicationSetupTimer(
			long v2gEVCCCommunicationSetupTimer) {
		this.v2gEVCCCommunicationSetupTimer = v2gEVCCCommunicationSetupTimer;
	}

	
	public long getV2gEVCCCommunicationSetupTimer() {
		return v2gEVCCCommunicationSetupTimer;
	}

	public String getEvseID() {
		return evseID;
	}

	public void setEvseID(String evseID) {
		this.evseID = evseID;
	}

	public PaymentOptionType getSelectedPaymentOption() {
		return selectedPaymentOption;
	}

	public void setSelectedPaymentOption(PaymentOptionType selectedPaymentOption) {
		this.selectedPaymentOption = selectedPaymentOption;
	}

	public ChargeParameterDiscoveryReqType getChargeParameterDiscoveryReq() {
		return chargeParameterDiscoveryReq;
	}

	public void setChargeParameterDiscoveryReq(
			ChargeParameterDiscoveryReqType chargeParameterDiscoveryReq) {
		this.chargeParameterDiscoveryReq = chargeParameterDiscoveryReq;
	}

	public ReactionToIncomingMessage getReactionToIncomingMessage() {
		return reactionToIncomingMessage;
	}

	public void setReactionToIncomingMessage(ReactionToIncomingMessage reactionToIncomingMessage) {
		this.reactionToIncomingMessage = reactionToIncomingMessage;
	}

	public boolean isStopChargingRequested() {
		return stopChargingRequested;
	}

	public void setStopChargingRequested(boolean stopChargingRequested) {
		this.stopChargingRequested = stopChargingRequested;
	}

	public boolean isRenegotiationRequested() {
		return renegotiationRequested;
	}

	public void setRenegotiationRequested(boolean renegotiationRequested) {
		this.renegotiationRequested = renegotiationRequested;
	}

	public boolean isPausingV2GCommSession() {
		return pausingV2GCommSession;
	}

	public void setPausingV2GCommSession(boolean pausingV2GCommSession) {
		this.pausingV2GCommSession = pausingV2GCommSession;
	}

	public long getEvseScheduleReceived() {
		return evseScheduleReceived;
	}

	public void setEvseScheduleReceived(long evseScheduleReceived) {
		this.evseScheduleReceived = evseScheduleReceived;
	}

	public ChargingProfileType getChargingProfile() {
		return chargingProfile;
	}

	public void setChargingProfile(ChargingProfileType chargingProfile) {
		this.chargingProfile = chargingProfile;
	}


	public List<AppProtocolType> getSupportedAppProtocols() {
		return supportedAppProtocols;
	}

	public void setSupportedAppProtocols(List<AppProtocolType> supportedAppProtocols) {
		this.supportedAppProtocols = supportedAppProtocols;
	}

	public AppProtocolType getChosenAppProtocol() {
		return chosenAppProtocol;
	}

	public void setChosenAppProtocol(AppProtocolType chosenAppProtocol) {
		this.chosenAppProtocol = chosenAppProtocol;
	}

	public boolean isOldSessionJoined() {
		return oldSessionJoined;
	}

	public void setOldSessionJoined(boolean oldSessionJoined) {
		this.oldSessionJoined = oldSessionJoined;
	}

	public IEVController getEvController() {
		return evController;
	}

	public void setEvController(IEVController evController) {
		this.evController = evController;
	}


	public long getEvseTimeStamp() {
		return evseTimeStamp;
	}


	public void setEvseTimeStamp(long evseTimeStamp) {
		this.evseTimeStamp = evseTimeStamp;
	}


	public EnergyTransferModeType getRequestedEnergyTransferMode() {
		return requestedEnergyTransferMode;
	}


	public void setRequestedEnergyTransferMode(
			EnergyTransferModeType requestedEnergyTransferMode) {
		this.requestedEnergyTransferMode = requestedEnergyTransferMode;
	}


	public SAScheduleListType getSaSchedules() {
		return saSchedules;
	}


	public void setSaSchedules(SAScheduleListType saSchedules) {
		this.saSchedules = saSchedules;
		this.saSchedulesReceived = System.currentTimeMillis();
	}


	public long getSaSchedulesReceived() {
		return saSchedulesReceived;
	}


	public CPStates getChangeToState() {
		return changeToState;
	}


	public void setChangeToState(CPStates changeToState) {
		this.changeToState = changeToState;
	}


	public StatefulTransportLayerClient getTransportLayerClient() {
		return transportLayerClient;
	}


	public void setTransportLayerClient(StatefulTransportLayerClient transportLayerClient) {
		this.transportLayerClient = transportLayerClient;
	}


	public SelectedServiceListType getSelectedServices() {
		if (selectedServices == null) setSelectedServices(new SelectedServiceListType());
		return selectedServices;
	}


	public void setSelectedServices(SelectedServiceListType selectedServices) {
		this.selectedServices = selectedServices;
	}


	public ServiceListType getOfferedServices() {
		if (offeredServices == null) setOfferedServices(new ServiceListType());
		return offeredServices;
	}


	public void setOfferedServices(ServiceListType offeredServices) {
		this.offeredServices = offeredServices;
	}


	public ArrayList<Short> getServiceDetailsToBeRequested() {
		if (serviceDetailsToBeRequested == null) {
			serviceDetailsToBeRequested = new ArrayList<Short>();
		}
		
		return serviceDetailsToBeRequested;
	}


	public void setServiceDetailsToBeRequested(
			ArrayList<Short> serviceDetailsToBeRequested) {
		this.serviceDetailsToBeRequested = serviceDetailsToBeRequested;
	}


	/**
	 * Checks if the respective service for installing or updating a certificate is offered by the SECC and
	 * has been selected by the EVCC.
	 * 
	 * @param parameterSetID 1 for installing a certificate, 2 for updating a certificate
	 * @return True, if the respective certificate service is available, false otherwise
	 */
	public boolean isCertificateServiceAvailable(short parameterSetID) {
		for (SelectedServiceType service : getSelectedServices().getSelectedService()) {
			if (service.getServiceID() == 2 && // ServiceID 2 refers to the 'Certificate' service
				service.getParameterSetID() != null && 
				service.getParameterSetID() == parameterSetID) 
				return true;
		}

		return false;
	}


	public ContractCertificateStatus getContractCertStatus() {
		return contractCertStatus;
	}


	public void setContractCertStatus(ContractCertificateStatus contractCertStatus) {
		this.contractCertStatus = contractCertStatus;
	}

}
