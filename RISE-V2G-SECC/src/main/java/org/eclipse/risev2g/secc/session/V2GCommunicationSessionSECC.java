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
package org.eclipse.risev2g.secc.session;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.risev2g.secc.backend.DummyBackendInterface;
import org.eclipse.risev2g.secc.backend.IBackendInterface;
import org.eclipse.risev2g.secc.evseController.DummyACEVSEController;
import org.eclipse.risev2g.secc.evseController.DummyDCEVSEController;
import org.eclipse.risev2g.secc.evseController.IACEVSEController;
import org.eclipse.risev2g.secc.evseController.IDCEVSEController;
import org.eclipse.risev2g.secc.evseController.IEVSEController;
import org.eclipse.risev2g.secc.states.ForkState;
import org.eclipse.risev2g.secc.states.WaitForAuthorizationReq;
import org.eclipse.risev2g.secc.states.WaitForCableCheckReq;
import org.eclipse.risev2g.secc.states.WaitForCertificateInstallationReq;
import org.eclipse.risev2g.secc.states.WaitForCertificateUpdateReq;
import org.eclipse.risev2g.secc.states.WaitForChargeParameterDiscoveryReq;
import org.eclipse.risev2g.secc.states.WaitForChargingStatusReq;
import org.eclipse.risev2g.secc.states.WaitForCurrentDemandReq;
import org.eclipse.risev2g.secc.states.WaitForMeteringReceiptReq;
import org.eclipse.risev2g.secc.states.WaitForPaymentDetailsReq;
import org.eclipse.risev2g.secc.states.WaitForPaymentServiceSelectionReq;
import org.eclipse.risev2g.secc.states.WaitForPowerDeliveryReq;
import org.eclipse.risev2g.secc.states.WaitForPreChargeReq;
import org.eclipse.risev2g.secc.states.WaitForServiceDetailReq;
import org.eclipse.risev2g.secc.states.WaitForServiceDiscoveryReq;
import org.eclipse.risev2g.secc.states.WaitForSessionSetupReq;
import org.eclipse.risev2g.secc.states.WaitForSessionStopReq;
import org.eclipse.risev2g.secc.states.WaitForSupportedAppProtocolReq;
import org.eclipse.risev2g.secc.states.WaitForWeldingDetectionReq;
import org.eclipse.risev2g.secc.transportLayer.ConnectionHandler;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ChangeProcessingState;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.SendMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.misc.V2GCommunicationSession;
import org.eclipse.risev2g.shared.misc.V2GTPMessage;
import org.eclipse.risev2g.shared.utils.ByteUtils;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MessageHeaderType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PMaxScheduleType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SAScheduleListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class V2GCommunicationSessionSECC extends V2GCommunicationSession implements Observer {
	
	private short schemaID;
	private ACEVSEStatusType acEVSEStatus;
	private boolean stopV2GCommunicationSession;
	private boolean pauseV2GCommunicationSession;
	private PMaxScheduleType pMaxSchedule;
	private short chosenSAScheduleTuple;
	private IACEVSEController acEvseController;
	private IDCEVSEController dcEvseController;
	private IEVSEController evseController;
	private IBackendInterface backendInterface;
	private boolean oldSessionJoined;
	private byte[] incomingV2GTPMessage;
	private ConnectionHandler connectionHandler;
	private ArrayList<ServiceType> offeredServices;
	private byte[] genChallenge;
	private SAScheduleListType saSchedules;
	private EnergyTransferModeType requestedEnergyTransferMode;
	private PaymentOptionType selectedPaymentOption;
	private CertificateChainType contractSignatureCertChain;
	private MeterInfoType sentMeterInfo;
	
	public V2GCommunicationSessionSECC(ConnectionHandler connectionHandler) {
		setConnectionHandler(connectionHandler);
		
		// Tell the respective ConnectionHandler to notify if a new V2GTPMessage has arrived (see update()-method)
		connectionHandler.addObserver(this);
		
		getStates().put(V2GMessages.FORK, new ForkState(this));
		getStates().put(V2GMessages.SUPPORTED_APP_PROTOCOL_REQ, new WaitForSupportedAppProtocolReq(this));
		getStates().put(V2GMessages.SESSION_SETUP_REQ, new WaitForSessionSetupReq(this));
		getStates().put(V2GMessages.SERVICE_DISCOVERY_REQ, new WaitForServiceDiscoveryReq(this));
		getStates().put(V2GMessages.SERVICE_DETAIL_REQ, new WaitForServiceDetailReq(this));
		getStates().put(V2GMessages.PAYMENT_SERVICE_SELECTION_REQ, new WaitForPaymentServiceSelectionReq(this));
		getStates().put(V2GMessages.CERTIFICATE_INSTALLATION_REQ, new WaitForCertificateInstallationReq(this));
		getStates().put(V2GMessages.CERTIFICATE_UPDATE_REQ, new WaitForCertificateUpdateReq(this));
		getStates().put(V2GMessages.PAYMENT_DETAILS_REQ, new WaitForPaymentDetailsReq(this));
		getStates().put(V2GMessages.AUTHORIZATION_REQ, new WaitForAuthorizationReq(this));
		getStates().put(V2GMessages.CHARGE_PARAMETER_DISCOVERY_REQ, new WaitForChargeParameterDiscoveryReq(this));
		getStates().put(V2GMessages.CABLE_CHECK_REQ, new WaitForCableCheckReq(this));
		getStates().put(V2GMessages.PRE_CHARGE_REQ, new WaitForPreChargeReq(this));
		getStates().put(V2GMessages.POWER_DELIVERY_REQ, new WaitForPowerDeliveryReq(this));
		getStates().put(V2GMessages.CHARGING_STATUS_REQ, new WaitForChargingStatusReq(this));
		getStates().put(V2GMessages.CURRENT_DEMAND_REQ, new WaitForCurrentDemandReq(this));
		getStates().put(V2GMessages.METERING_RECEIPT_REQ, new WaitForMeteringReceiptReq(this));
		getStates().put(V2GMessages.WELDING_DETECTION_REQ, new WaitForWeldingDetectionReq(this));
		getStates().put(V2GMessages.SESSION_STOP_REQ, new WaitForSessionStopReq(this));
		
		setStartState(getStates().get(V2GMessages.SUPPORTED_APP_PROTOCOL_REQ));
		setCurrentState(getStartState());
		
		// Configure which EVSE controller implementation to use
		setACEvseController(new DummyACEVSEController(this));
		setDCEvseController(new DummyDCEVSEController(this));
		
		// Configures which backend interface implementation to use for retrieving SASchedules
		setBackendInterface(new DummyBackendInterface(this));

		// ACEVSE notification
		setAcEVSEStatus(new ACEVSEStatusType());
		getACEVSEStatus().setEVSENotification(EVSENotificationType.NONE);
		getACEVSEStatus().setNotificationMaxDelay(0);
		getACEVSEStatus().setRCD(false);
		
		setStopV2GCommunicationSession(false);
		setPauseV2GCommunicationSession(false);
		
		setOfferedServices(new ArrayList<ServiceType>());
		
		getLogger().debug("\n*******************************************" +
						  "\n* New V2G communication session initialized" +
						  "\n*******************************************");
	}
	
	
	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof ConnectionHandler && obj instanceof byte[]) {
			processIncomingMessage((byte[]) obj);
		} else if (obs instanceof ConnectionHandler && obj == null) {
			terminateSession("ConnectionHandler has notified an error", false);
		}
	}
	
	
	public void processIncomingMessage(Object incomingMessage) {
		setV2gTpMessage(new V2GTPMessage((byte[]) incomingMessage)); 
		
		if (getMessageHandler().isV2GTPMessageValid(getV2gTpMessage()) &&
			Arrays.equals(getV2gTpMessage().getPayloadType(), GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue())) {
			/*
			 * Decide which schema to use for decoding the EXI encoded message. 
			 * Only the SupportedAppProtocolReq/Res message uses a different schema
			 */
			if (getCurrentState().equals(getStates().get(V2GMessages.SUPPORTED_APP_PROTOCOL_REQ))) {
				incomingMessage = (SupportedAppProtocolReq) getMessageHandler().exiToSuppAppProtocolMsg(getV2gTpMessage().getPayload());
			} else {
				incomingMessage = (V2GMessage) getMessageHandler().exiToV2gMsg(getV2gTpMessage().getPayload());
			}
			
			processReaction(getCurrentState().processIncomingMessage(incomingMessage));
		} else {
			terminateSession("Received incoming message is not a valid V2GTPMessage", false);
		}
	}
	
	
	private void processReaction(ReactionToIncomingMessage reactionToIncomingMessage) {
		// Check the outcome of the processIncomingMessage() of the respective state
		if (reactionToIncomingMessage instanceof SendMessage) {
			send((SendMessage) reactionToIncomingMessage);
			if (isStopV2GCommunicationSession()) {
				terminateSession("EVCC indicated request to stop the session", true);
			}
		} else if (reactionToIncomingMessage instanceof ChangeProcessingState) {
			setCurrentState(((ChangeProcessingState) reactionToIncomingMessage).getNewState());
			processReaction(
					getCurrentState().processIncomingMessage(
							((ChangeProcessingState) reactionToIncomingMessage).getPayload()));
		} else if (reactionToIncomingMessage instanceof TerminateSession) {
			/*
			 * TODO is this really needed? if sth. goes wrong, a negative response code will be send by
			 * the respective state anyway, the reaction to this negative response code should only
			 * instantiate a TerminateSession object!
			 */
			terminateSession(((TerminateSession) reactionToIncomingMessage));
		} else {
			terminateSession("Reaction to incoming message is undefined", false);
		}
	}
	
	
	/**
	 * Returns a response code according to 8.4.2
	 * @param header The header encapsulated in the EVCC request message
	 * @return The corresponding response code
	 */
	public ResponseCodeType checkSessionID(MessageHeaderType header) {
		if (getCurrentState().equals(getStates().get(V2GMessages.SESSION_SETUP_REQ)) && 
				ByteUtils.toLongFromByteArray(header.getSessionID()) == 0L) {
			// EV wants to start a totally new charging session
			setSessionID(generateSessionIDRandomly());
			setOldSessionJoined(false);
			return ResponseCodeType.OK_NEW_SESSION_ESTABLISHED;
		} else if (getCurrentState().equals(getStates().get(V2GMessages.SESSION_SETUP_REQ)) && 
				   header.getSessionID() == getSessionID()) {
			// A charging pause has taken place and the EV wants to resume the old charging session
			setOldSessionJoined(true);
			return ResponseCodeType.OK_OLD_SESSION_JOINED;
		} else if (getCurrentState().equals(getStates().get(V2GMessages.SESSION_SETUP_REQ)) && 
				ByteUtils.toLongFromByteArray(header.getSessionID()) != 0L &&
				   header.getSessionID() != getSessionID()) {
			// Avoid a "FAILED_..." response code by generating a new SessionID in the response
			setSessionID(generateSessionIDRandomly());
			setOldSessionJoined(false);
			return ResponseCodeType.OK_NEW_SESSION_ESTABLISHED;
		} else if (Arrays.equals(header.getSessionID(), getSessionID())) {
			// This should be the routine during a running charging session after a session setup
			setOldSessionJoined(false);
			return ResponseCodeType.OK;
		} else {
			// EV sends a SessionID DURING the already running charging session which does not match
			setOldSessionJoined(false);
			return ResponseCodeType.FAILED_UNKNOWN_SESSION;
		}
	}
	
	
	public void send(SendMessage sendMessage) {
		// Only EXI encoded messages will be sent here. Decide whether V2GMessage or SupportedAppProtocolRes
		byte[] payload = null;
		
		if (sendMessage.getPayload() instanceof V2GMessage) {
			payload = (byte[]) getMessageHandler().v2gMsgToExi(sendMessage.getPayload());
		} else {
			payload = (byte[]) getMessageHandler().suppAppProtocolMsgToExi(sendMessage.getPayload());
		}
			
		setV2gTpMessage(
				new V2GTPMessage(GlobalValues.V2GTP_VERSION_1_IS.getByteValue(), 
				GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue(),
				payload)
			);
		
		getConnectionHandler().send(getV2gTpMessage());
		
		if (sendMessage.getNextState() != null) {
			setCurrentState(sendMessage.getNextState());
		} else {
			getLogger().info("State machine has come to an end, no new state provided");
		}
	}
	
	
	public short getSchemaID() {
		return schemaID;
	}
	
	public void setSchemaID(short schemaID) {
		this.schemaID = schemaID;
	}
	
	public ACEVSEStatusType getACEVSEStatus() {
		return acEVSEStatus;
	}

	public boolean isStopV2GCommunicationSession() {
		return stopV2GCommunicationSession;
	}

	public void setStopV2GCommunicationSession(boolean stopV2GCommunicationSession) {
		this.stopV2GCommunicationSession = stopV2GCommunicationSession;
	}

	public boolean isPauseV2GCommunicationSession() {
		return pauseV2GCommunicationSession;
	}


	public void setPauseV2GCommunicationSession(boolean pauseV2GCommunicationSession) {
		this.pauseV2GCommunicationSession = pauseV2GCommunicationSession;
	}


	public PMaxScheduleType getPMaxSchedule() {
		return pMaxSchedule;
	}

	public void setPMaxSchedule(PMaxScheduleType newPMaxSchedule) {
		this.pMaxSchedule = newPMaxSchedule;
	}

	public short getChosenSAScheduleTuple() {
		return chosenSAScheduleTuple;
	}

	public void setChosenSAScheduleTuple(short saScheduleTupleID) {
		this.chosenSAScheduleTuple = saScheduleTupleID;
	}

	public IBackendInterface getBackendInterface() {
		return backendInterface;
	}


	public void setBackendInterface(IBackendInterface backendInterface) {
		this.backendInterface = backendInterface;
	}


	public boolean isOldSessionJoined() {
		return oldSessionJoined;
	}

	public void setOldSessionJoined(boolean oldSessionJoined) {
		this.oldSessionJoined = oldSessionJoined;
	}

	public byte[] getIncomingV2GTPMessage() {
		return incomingV2GTPMessage;
	}

	public void setIncomingV2GTPMessage(byte[] incomingV2GTPMessage) {
		this.incomingV2GTPMessage = incomingV2GTPMessage;
	}

	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public void setConnectionHandler(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}
	
	public ACEVSEStatusType getAcEVSEStatus() {
		return acEVSEStatus;
	}

	public void setAcEVSEStatus(ACEVSEStatusType acEVSEStatus) {
		this.acEVSEStatus = acEVSEStatus;
	}


	public ArrayList<ServiceType> getOfferedServices() {
		return offeredServices;
	}


	public void setOfferedServices(ArrayList<ServiceType> offeredServices) {
		this.offeredServices = offeredServices;
	}


	public byte[] getGenChallenge() {
		return genChallenge;
	}


	public void setGenChallenge(byte[] genChallenge) {
		this.genChallenge = genChallenge;
	}


	public SAScheduleListType getSaSchedules() {
		return saSchedules;
	}


	public void setSaSchedules(SAScheduleListType saSchedules) {
		this.saSchedules = saSchedules;
	}


	public EnergyTransferModeType getRequestedEnergyTransferMode() {
		return requestedEnergyTransferMode;
	}


	public void setRequestedEnergyTransferMode(
			EnergyTransferModeType requestedEnergyTransferMode) {
		this.requestedEnergyTransferMode = requestedEnergyTransferMode;
	}


	public CertificateChainType getContractSignatureCertChain() {
		return contractSignatureCertChain;
	}


	public void setContractSignatureCertChain(CertificateChainType contractSignatureCertChain) {
		this.contractSignatureCertChain = contractSignatureCertChain;
	}


	public MeterInfoType getSentMeterInfo() {
		return sentMeterInfo;
	}


	public void setSentMeterInfo(MeterInfoType sentMeterInfo) {
		this.sentMeterInfo = sentMeterInfo;
	}


	public IACEVSEController getACEvseController() {
		return acEvseController;
	}


	public void setACEvseController(IACEVSEController acEvseController) {
		this.acEvseController = acEvseController;
	}


	public IDCEVSEController getDCEvseController() {
		return dcEvseController;
	}


	public void setDCEvseController(IDCEVSEController dcEvseController) {
		this.dcEvseController = dcEvseController;
	}


	public IEVSEController getEvseController() {
		if (getRequestedEnergyTransferMode() != null) {
			if (getRequestedEnergyTransferMode().toString().startsWith("AC")) 
				return acEvseController;
			else if (getRequestedEnergyTransferMode().toString().startsWith("DC")) 
				return dcEvseController;
			else {
				getLogger().error("RequestedEnergyTransferMode '" + getRequestedEnergyTransferMode().toString() + 
								   "is neither of type AC nor DC");
				return null;
			}
		} else return acEvseController; // just AC controller as default
	}


	public void setEvseController(IEVSEController evseController) {
		this.evseController = evseController;
	}


	public PaymentOptionType getSelectedPaymentOption() {
		return selectedPaymentOption;
	}


	public void setSelectedPaymentOption(PaymentOptionType selectedPaymentOption) {
		this.selectedPaymentOption = selectedPaymentOption;
	}
}
