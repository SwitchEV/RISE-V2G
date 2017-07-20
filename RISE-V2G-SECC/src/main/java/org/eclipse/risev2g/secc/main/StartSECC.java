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
package org.eclipse.risev2g.secc.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.secc.session.V2GCommunicationSessionHandlerSECC;
import org.eclipse.risev2g.secc.transportLayer.TCPServer;
import org.eclipse.risev2g.secc.transportLayer.TLSServer;
import org.eclipse.risev2g.secc.transportLayer.UDPServer;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.utils.MiscUtils;

public class StartSECC {
	
	public static void main(String[] args) {
		final Logger logger = LogManager.getLogger(StartSECC.class.getSimpleName());
		MiscUtils.setV2gEntityConfig(GlobalValues.SECC_CONFIG_PROPERTIES_PATH.toString());
		
		UDPServer udpServer = UDPServer.getInstance();
		TCPServer tcpServer = TCPServer.getInstance();
		TLSServer tlsServer = TLSServer.getInstance();
		
		if (!udpServer.initialize() || !tlsServer.initialize() || !tcpServer.initialize()) {
			logger.fatal("Unable to start SECC because UDP, TCP or TLS server could not be initialized");
		} else {
			Thread udpServerThread = new Thread(udpServer);
			udpServerThread.setName("UDPServerThread");
			
			Thread tcpServerThread = new Thread(tcpServer);
			tcpServerThread.setName("TCPServerThread");
			
			Thread tlsServerThread = new Thread(tlsServer);
			tlsServerThread.setName("TLSServerThread");
			
			// All transport layer threads need to be initialized before initializing the SECC session handler.
			V2GCommunicationSessionHandlerSECC sessionHandler = new V2GCommunicationSessionHandlerSECC();
			
			/*
			 * To avoid possible race conditions, the transport layer threads need to be started AFTER the SECC
			 * session handler has been initialized. Otherwise the situation might occur that the UDPServer is 
			 * receiving a UDP client packet and tries to access the MessageHandler object before this object has
			 * been created by the SECC session handler.
			 */
			udpServerThread.start();
			tcpServerThread.start();
			tlsServerThread.start();
		} 
	}
}
