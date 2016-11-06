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

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.secc.transportLayer.ConnectionHandler;
import org.eclipse.risev2g.secc.transportLayer.TCPServer;
import org.eclipse.risev2g.secc.transportLayer.TLSServer;
import org.eclipse.risev2g.secc.transportLayer.UDPServer;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.messageHandling.MessageHandler;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.misc.V2GTPMessage;
import org.eclipse.risev2g.shared.utils.ByteUtils;
import org.eclipse.risev2g.shared.v2gMessages.SECCDiscoveryReq;
import org.eclipse.risev2g.shared.v2gMessages.SECCDiscoveryRes;

public class V2GCommunicationSessionHandlerSECC implements Observer {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private HashMap<String, V2GCommunicationSessionSECC> v2gCommunicationSessions;
	/*
	 * Keeps a list of all ConnectionHandlers and their respective running Threads.
	 * The V2GCommunicationSessionHandlerSECC needs a ConnectionHandler (with its TCP/TLS client socket)
	 * in order to associate it with a V2GCommunicationSessionSECC. Handing over a Thread instead brings
	 * up the problem that you can't access the Threads runnable object (ConnectionHandler).
	 */
	private static HashMap<ConnectionHandler, Thread> connectionHandlerMap;
	private MessageHandler messageHandler;
	private V2GTPMessage v2gTpMessage;
	private byte security; 
	
	public V2GCommunicationSessionHandlerSECC() {
		// Tell the respective transport layer Observables to notify this session handler
		UDPServer.getInstance().addObserver(this);
		TCPServer.getInstance().addObserver(this);
		TLSServer.getInstance().addObserver(this);
		
		// Maps IP addresses of the clients given as a String to V2GCommunicationSessionSECC objects
		setV2gCommunicationSessions(new HashMap<String, V2GCommunicationSessionSECC>());
		
		// Maps ConnectionHandlers to their respective running threads
		setConnectionHandlerMap(new HashMap<ConnectionHandler, Thread>());
		
		setMessageHandler(new MessageHandler());
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof UDPServer && obj instanceof DatagramPacket) {
			processSECCDiscoveryReq((DatagramPacket) obj);
		} else if ((obs instanceof TCPServer || obs instanceof TLSServer) && obj instanceof ConnectionHandler) {
			String ipAddress = ((ConnectionHandler) obj).getAddress();
			
			if (getV2gCommunicationSessions().containsKey(ipAddress)) {
				/*
				 * Assign the new ConnectionHandler to the respective existing V2GCommunicationSessionSECC.
				 * This way the V2GCommunicationSessionSECC knows to which socket to write to when
				 * sending messages and from which socket to read from when receiving messages.
				 * 
				 * This if-clause is executed as soon as an EV resumes a previously paused charging 
				 * session. Before pausing, the TCP/TLS socket has been closed, but the charging session 
				 * data object (V2GCommunicationSessionSECC) needed to be kept alive in order to later
				 * on continue a charging session with the saved data.

				 * Important!
				 * The connectionHandler thread must not be started (will start reading the incoming bytes)
				 * before the V2GCommunicationSessionSECC object is instantiated, otherwise it may lead to 
				 * race conditions. 
				 */
				V2GCommunicationSessionSECC continuedSession = getV2gCommunicationSessions().get(ipAddress);
				continuedSession.setConnectionHandler((ConnectionHandler) obj);
				continuedSession.setTlsConnection((obs instanceof TLSServer) ? true : false);
				((ConnectionHandler) obj).addObserver(getV2gCommunicationSessions().get(ipAddress));
				
				manageConnectionHandlers((ConnectionHandler) obj);
			} else { 
				V2GCommunicationSessionSECC newSession = new V2GCommunicationSessionSECC((ConnectionHandler) obj);
				newSession.setTlsConnection((obs instanceof TLSServer) ? true : false);
				newSession.addObserver(this);
				getV2gCommunicationSessions().put(ipAddress, newSession);
				
				manageConnectionHandlers((ConnectionHandler) obj);
			}
		} else if (obs instanceof V2GCommunicationSessionSECC && obj instanceof TerminateSession) {
			// Remove the V2GCommunicationSessionSECC instance from the hashmap
			String ipAddress = ((V2GCommunicationSessionSECC) obs).getConnectionHandler().getAddress();
			getV2gCommunicationSessions().remove(ipAddress);
			
			stopConnectionHandler(((V2GCommunicationSessionSECC) obs).getConnectionHandler());
		} else {
			getLogger().warn("Notification received, but sending entity or received object not identifiable");
		}
	}

	
	private void manageConnectionHandlers(ConnectionHandler connectionHandler) {
		Thread connectionHandlerThread = new Thread(connectionHandler);
		connectionHandlerThread.setDaemon(true);
		connectionHandlerThread.setName("ConnectionThread " + connectionHandler.getAddress());
		connectionHandlerThread.start();
		
		getConnectionHandlerMap().put(connectionHandler, connectionHandlerThread);
	}
	
	private void processSECCDiscoveryReq(DatagramPacket udpClientPacket) {
		setV2gTpMessage(new V2GTPMessage(udpClientPacket.getData()));
		
		try {
			if (getMessageHandler().isV2GTPMessageValid(getV2gTpMessage()) &&
			    Arrays.equals(getV2gTpMessage().getPayloadType(), GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_REQUEST_MESSAGE.getByteArrayValue())) {
				
				SECCDiscoveryReq seccDiscoveryReq = new SECCDiscoveryReq(getV2gTpMessage().getPayload());
				setSecurity(seccDiscoveryReq.getSecurity());
				getLogger().debug("SECCDiscoveryReq received");
				
				/*
				 * The TCP and TLS server ports are created upon initialization of the TCP/TLS server and will 
				 * remain the same for every connected EV. Only TCP or TLS are allowed as transport 
				 * protocols for further communication beyond the SECCDiscoveryReq/-Res handshake (not UDP).
				 * 
				 * One might implement further decision rules for dealing with the security level (TCP or TLS)
				 * requested by the EVCC (see also Table 3 and 4 of ISO/IEC 15118-2). For now, the requested
				 * security level of the EVCC will always be accepted.
				 */
				byte[] seccAddress = (isSecureCommunication()) ? TLSServer.getInstance().getServerAddress().getAddress() : TCPServer.getInstance().getServerAddress().getAddress();
				int seccPort = (isSecureCommunication()) ? TLSServer.getInstance().getServerPort() : TCPServer.getInstance().getServerPort();
						
				SECCDiscoveryRes seccDiscoveryRes = new SECCDiscoveryRes(
															seccAddress,
															ByteUtils.toByteArrayFromInt(seccPort, true),
															getSecurity(),
															GlobalValues.V2G_TRANSPORT_PROTOCOL_TCP.getByteValue()
														);
				
				setV2gTpMessage(new V2GTPMessage(GlobalValues.V2GTP_VERSION_1_IS.getByteValue(), 
												 GlobalValues.V2GTP_PAYLOAD_TYPE_SDP_RESPONSE_MESSAGE.getByteArrayValue(),
												 seccDiscoveryRes.getPayload()));
				
				getLogger().debug("Preparing to send SECCDiscoveryRes ...");
				
				// The SECCDiscoveryRes must be sent via UDP before the requested TCP/TLS server can be used
				UDPServer.getInstance().send(getV2gTpMessage(), (Inet6Address) udpClientPacket.getAddress(), udpClientPacket.getPort());
			} else {
				getLogger().warn("Incoming DatagramPacket could not be identified as a SECCDiscoveryReq");
			}
		} catch (NullPointerException e) {
			getLogger().error("NullPointerException occurred while processing SECCDiscoveryReq", e);
		}
	}
	
	/**
	 * Stops (interrupts) the respective thread running the provided ConnectionHandler and tries
	 * to close its socket. 
	 * @param connectionHandler The ConnectionHandler whose socket is to be closed and whose thread
	 * 							   is to be interrupted.
	 */
	public void stopConnectionHandler(ConnectionHandler connectionHandler) {
		if (getConnectionHandlerMap().containsKey(connectionHandler)) {
			// Close the socket
			connectionHandler.stop();
			
			// Interrupt session thread
			Thread connectionThread = getConnectionHandlerMap().get(connectionHandler);
			connectionThread.interrupt();
			
			// Remove HashMap entry
			getConnectionHandlerMap().remove(connectionHandler);
			
			getLogger().debug("Thread '" + connectionThread.getName() + 
							  "' has been interrupted and removed" );
		} else {
			String address = connectionHandler.getAddress();
			int port = connectionHandler.getPort(); 

			getLogger().warn("No active connection to socket with IP address " +
							 address + " and port " + port + " found.");
		}
	}
	
	public boolean isSecureCommunication() {
		return Byte.compare(getSecurity(), GlobalValues.V2G_SECURITY_WITH_TLS.getByteValue()) == 0 ? true : false;
	}
	
	public void removeV2GCommunicationSession(InetAddress requesterAddress) {
		getV2gCommunicationSessions().remove(getV2gCommunicationSessions().get(requesterAddress));
	}

	public HashMap<String, V2GCommunicationSessionSECC> getV2gCommunicationSessions() {
		return v2gCommunicationSessions;
	}

	public void setV2gCommunicationSessions(
			HashMap<String, V2GCommunicationSessionSECC> v2gCommunicationSessions) {
		this.v2gCommunicationSessions = v2gCommunicationSessions;
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

	public V2GTPMessage getV2gTpMessage() {
		return v2gTpMessage;
	}

	public void setV2gTpMessage(V2GTPMessage v2gTpMessage) {
		this.v2gTpMessage = v2gTpMessage;
	}

	public static HashMap<ConnectionHandler, Thread> getConnectionHandlerMap() {
		return connectionHandlerMap;
	}

	public static void setConnectionHandlerMap(HashMap<ConnectionHandler, Thread> connectionHandlerMap) {
		V2GCommunicationSessionHandlerSECC.connectionHandlerMap = connectionHandlerMap;
	}

	public byte getSecurity() {
		return security;
	}

	public void setSecurity(byte security) {
		this.security = security;
	}
}
