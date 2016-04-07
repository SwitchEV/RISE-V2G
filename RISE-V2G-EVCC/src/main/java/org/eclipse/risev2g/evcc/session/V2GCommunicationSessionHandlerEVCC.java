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
package org.eclipse.risev2g.evcc.session;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.evcc.transportLayer.StatefulTransportLayerClient;
import org.eclipse.risev2g.evcc.transportLayer.TCPClient;
import org.eclipse.risev2g.evcc.transportLayer.TLSClient;
import org.eclipse.risev2g.evcc.transportLayer.UDPClient;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.MessageHandler;
import org.eclipse.risev2g.shared.messageHandling.PauseSession;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.misc.TimeRestrictions;
import org.eclipse.risev2g.shared.misc.V2GTPMessage;
import org.eclipse.risev2g.shared.utils.ByteUtils;
import org.eclipse.risev2g.shared.utils.MiscUtils;
import org.eclipse.risev2g.shared.v2gMessages.SECCDiscoveryReq;
import org.eclipse.risev2g.shared.v2gMessages.SECCDiscoveryRes;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.AppProtocolType;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;


public class V2GCommunicationSessionHandlerEVCC implements Observer {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private MessageHandler messageHandler;
	private int seccDiscoveryRequestCounter;
	private int sessionRetryCounter;
	private byte security;
	private V2GCommunicationSessionEVCC v2gCommunicationSessionEVCC;
	private V2GTPMessage v2gTpMessage;
	private Thread transportLayerThread;
	private StatefulTransportLayerClient transportLayerClient;
	
	public V2GCommunicationSessionHandlerEVCC() {
		setMessageHandler(new MessageHandler());
		
		setSecurity(
				(MiscUtils.getPropertyValue("TLSSecurity") != null ? 
						(byte) MiscUtils.getPropertyValue("TLSSecurity") : 
						GlobalValues.V2G_SECURITY_WITHOUT_TLS.getByteValue())
		);
		
		setSessionRetryCounter(0);
		
		if (!initialize()) {
			// TODO ja, was tun?
		};
	}
	
	
	private boolean initialize() {
		byte[] udpResponse = null;
		SECCDiscoveryRes seccDiscoveryRes = null;
		setSessionRetryCounter(getSeccDiscoveryRequestCounter() + 1);
		
		// Create SECCDiscoveryReq and check response
		while (udpResponse == null && 
			   getSeccDiscoveryRequestCounter() < TimeRestrictions.SDP_REQUEST_MAX_COUNTER) {
			udpResponse = sendSECCDiscoveryReq(getSecurity());
			
			if (udpResponse == null) {
				getLogger().warn("Number of SECCDiscoveryReq messages so far: " + getSeccDiscoveryRequestCounter());
			} else {
				setV2gTpMessage(new V2GTPMessage(udpResponse));
				
				if (getMessageHandler().isV2GTPMessageValid(getV2gTpMessage())) {
					seccDiscoveryRes = new SECCDiscoveryRes(getV2gTpMessage().getPayload());
					break;
				}
			}
		}
		
		/*
		 * Establish a new V2GCommunicationSessionEVCC if SECCDiscoveryRes was successful and initiate
		 * the respective TCP client connection
		 */
		if (startNewSession(seccDiscoveryRes)) return true;
		else return false;
	}
	
	
	private boolean startNewSession(SECCDiscoveryRes seccDiscoveryRes) {
		/*
		 * Establish a new V2GCommunicationSessionEVCC if SECCDiscoveryRes was successful and initiate
		 * the respective TCP client connection
		 */
		if (seccDiscoveryRes != null) {
			// Reset SECCDiscoveryReq retry counter
			setSeccDiscoveryRequestCounter(0);
			
			Inet6Address seccAddress;
			
			try {
				// TODO seems to work, but is the needed scope ID really the one of the UDP client?
				seccAddress = Inet6Address.getByAddress(
										InetAddress.getByAddress(seccDiscoveryRes.getSeccIPAddress()).getHostAddress(),
										seccDiscoveryRes.getSeccIPAddress(), 
										UDPClient.getInstance().getUdpClientAddress().getScopeId()
									);
			} catch (UnknownHostException e) {
				getLogger().fatal("SECC address could not be resolved", e);
				return false;
			}
			
			getLogger().info("UDP server responded: SECC reachable at address " + 
							 seccAddress.getHostAddress() + " and port " + 
							 ByteUtils.toIntFromByteArray(seccDiscoveryRes.getSeccPort())); 
			
			if (!startTransportLayerClient(seccDiscoveryRes, seccAddress)) return false;
			
			setV2gCommunicationSessionEVCC(new V2GCommunicationSessionEVCC(getTransportLayerClient()));
			
			/*
			 * Tell the TCP- or TLSClient to notify if 
			 * - a new V2GTPMessage has arrived
			 * - a timeout has occurred while waiting for the respective response message
			 */
			getTransportLayerClient().addObserver(getV2gCommunicationSessionEVCC());
			
			getV2gCommunicationSessionEVCC().addObserver(this);
			
			// Set TLS security flag for communication session
			boolean secureConn = (((Byte) getSecurity()).compareTo((Byte) GlobalValues.V2G_SECURITY_WITH_TLS.getByteValue()) == 0) ? true : false;
			getV2gCommunicationSessionEVCC().setTlsConnection(secureConn);
			
			sendSupportedAppProtocolReq();
		} else {
			getLogger().fatal("Maximum number of SECCDiscoveryReq messages reached");
			return false;
		}
		
		return true;
	}
	
	
	private boolean startTransportLayerClient(SECCDiscoveryRes seccDiscoveryRes, Inet6Address seccAddress) {
		boolean securityAgreement = Byte.compare(seccDiscoveryRes.getSecurity(), getSecurity()) == 0 ? true : false;

		/*
		* Note 8 of ISO/IEC 15118-2 states:
		* "Not supporting TLS in the SECC might lead in general to aborted charging sessions 
		* with particular EVs as it is in the responsibility of the EV to accept sessions 
		* without TLS"
		* 
		* This implementation of an EVCC will only accept TLS connections to the SECC if requested on 
		* EVCC-side. However, this is the place to change the implementation if wanted. It is however 
		* strongly recommended to always choose TLS.
		*/
		if (securityAgreement && isSecureCommunication()) {
			if (TLSClient.getInstance().initialize(
				seccAddress,
				ByteUtils.toIntFromByteArray(seccDiscoveryRes.getSeccPort()))) {
				setTransportLayerClient(TLSClient.getInstance());
			} else {
				getLogger().fatal("TLS client could not be initialized");
				return false;
			}
		} else if (securityAgreement && !isSecureCommunication()) {
			if (TCPClient.getInstance().initialize(
				seccAddress,
				ByteUtils.toIntFromByteArray(seccDiscoveryRes.getSeccPort()))) {
				setTransportLayerClient(TCPClient.getInstance());
			} else {
				getLogger().fatal("TCP client could not be initialized");
				return false;
			}
		} else {
			getLogger().fatal("EVCC and SECC could not agree on security level of transport layer");
			return false;
		}
		
		setTransportLayerThread(new Thread(getTransportLayerClient()));
		getTransportLayerThread().start();
		
		return true;
	}
	
	
	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof V2GCommunicationSessionEVCC && 
			(obj instanceof PauseSession || obj instanceof TerminateSession)) {
			// In case of pausing or terminating a session the transport layer client must be stopped
			getTransportLayerClient().stop();
			getTransportLayerThread().interrupt();
			
			if (obj instanceof PauseSession) {
				/*
				 * If some action is needed by the sessionHandler when pausing, it can be done here.
				 * If TCP/TLS client sends notification, it should always be a TerminateSession instance
				 * (because a failure of the connection to the TCP/TLS server is its only reason for 
				 * notification).
				 */
			} else if (obj instanceof TerminateSession) {
				terminate((TerminateSession) obj);
			}
		} else if (obs instanceof TCPClient || obs instanceof TLSClient) {
			// TCP- and TLSClient already stop themselves and interrupt their threads before notifying
			terminate((TerminateSession) obj);
		} else {
			getLogger().warn("Notification coming from " + obs.getClass().getSimpleName() + 
					 " unknown: " + obj.getClass().getSimpleName());
		}
	}
	
	private void terminate(TerminateSession terminationObject) {
		setV2gCommunicationSessionEVCC(null);
		
		if (!terminationObject.isSuccessfulTermination()) {
			// TODO should there be a retry of the communication session, and if yes, how often?
		}
	}
	
	
	private byte[] sendSECCDiscoveryReq(byte security) {
		/*
		 * The standard in principle allows to set UDP as requested transport protocol, however,
		 * there is no good reason for actually not using TCP (or TLS). Therefore this is not a
		 * configurable option.
		 */
		SECCDiscoveryReq seccDiscoveryReq = 
				new SECCDiscoveryReq(security, GlobalValues.V2G_TRANSPORT_PROTOCOL_TCP.getByteValue());
		
		setV2gTpMessage(
				new V2GTPMessage(GlobalValues.V2GTP_VERSION_1_IS.getByteValue(), 
				GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE.getByteArrayValue(),
				seccDiscoveryReq.getPayload())
		);
		
		getLogger().debug("Preparing to send SECCDiscoveryReq ...");
		setSeccDiscoveryRequestCounter(getSeccDiscoveryRequestCounter() + 1);
		
		return UDPClient.getInstance().send(getV2gTpMessage());
	}
	
	
	/**
	 * All supported versions of the ISO/IEC 15118-2 protocol are listed here.
	 * Currently, only IS version of April 2014 is supported (see [V2G2-098]), more could be provided here.
	 * 
	 * @return A list of supported of AppProtocol entries 
	 */
	private void sendSupportedAppProtocolReq() {
		List<AppProtocolType> supportedAppProtocols = new ArrayList<AppProtocolType>();
		
		AppProtocolType appProtocol1 = new AppProtocolType();
		appProtocol1.setProtocolNamespace(GlobalValues.V2G_CI_MSG_DEF_NAMESPACE.toString());
		appProtocol1.setVersionNumberMajor(2);
		appProtocol1.setVersionNumberMinor(0);
		appProtocol1.setSchemaID((short) 10);
		appProtocol1.setPriority((short) 1);
		
		supportedAppProtocols.add(appProtocol1);
		
		SupportedAppProtocolReq supportedAppProtocolReq = new SupportedAppProtocolReq();
		supportedAppProtocolReq.getAppProtocol().add(appProtocol1);
		
		// Save the list of supported protocols 
		getV2gCommunicationSessionEVCC().setSupportedAppProtocols(supportedAppProtocols);
		
		setV2gTpMessage(
				new V2GTPMessage(
						GlobalValues.V2GTP_VERSION_1_IS.getByteValue(), 
						GlobalValues.V2GTP_PAYLOAD_TYPE_EXI_ENCODED_V2G_MESSAGE.getByteArrayValue(),
						(byte[]) getMessageHandler().suppAppProtocolMsgToExi(supportedAppProtocolReq)
				)
		);
		
		getLogger().debug("Preparing to send SupportedAppProtocolReq ...");
		
		if (isSecureCommunication()) {
			TLSClient.getInstance().send(
				getV2gTpMessage(), 
				TimeRestrictions.getV2G_EVCC_Msg_Timeout(V2GMessages.SUPPORTED_APP_PROTOCOL_RES));
		} else {
			TCPClient.getInstance().send(
				getV2gTpMessage(), 
				TimeRestrictions.getV2G_EVCC_Msg_Timeout(V2GMessages.SUPPORTED_APP_PROTOCOL_RES));
		}
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public int getSeccDiscoveryRequestCounter() {
		return seccDiscoveryRequestCounter;
	}


	public void setSeccDiscoveryRequestCounter(int seccDiscoveryRequestCounter) {
		this.seccDiscoveryRequestCounter = seccDiscoveryRequestCounter;
	}
	
	public byte getSecurity() {
		return security;
	}
	
	public boolean isSecureCommunication() {
		return Byte.compare(getSecurity(), GlobalValues.V2G_SECURITY_WITH_TLS.getByteValue()) == 0 ? true : false;
	}


	public void setSecurity(byte security) {
		this.security = security;
		getLogger().info("Security level " +
						 ((Byte.compare(security, GlobalValues.V2G_SECURITY_WITH_TLS.getByteValue()) == 0) ? "TLS" : "TCP") +
						 " was chosen");
	}


	public V2GCommunicationSessionEVCC getV2gCommunicationSessionEVCC() {
		return v2gCommunicationSessionEVCC;
	}


	public void setV2gCommunicationSessionEVCC(
			V2GCommunicationSessionEVCC v2gCommunicationSessionEVCC) {
		this.v2gCommunicationSessionEVCC = v2gCommunicationSessionEVCC;
	}


	public int getSessionRetryCounter() {
		return sessionRetryCounter;
	}


	public void setSessionRetryCounter(int sessionRetryCounter) {
		this.sessionRetryCounter = sessionRetryCounter;
	}


	public V2GTPMessage getV2gTpMessage() {
		return v2gTpMessage;
	}


	public void setV2gTpMessage(V2GTPMessage v2gTpMessage) {
		this.v2gTpMessage = v2gTpMessage;
	}


	public Thread getTransportLayerThread() {
		return transportLayerThread;
	}


	public void setTransportLayerThread(Thread transportLayerThread) {
		this.transportLayerThread = transportLayerThread;
	}


	public StatefulTransportLayerClient getTransportLayerClient() {
		return transportLayerClient;
	}


	public void setTransportLayerClient(StatefulTransportLayerClient transportLayerClient) {
		this.transportLayerClient = transportLayerClient;
	}
}
