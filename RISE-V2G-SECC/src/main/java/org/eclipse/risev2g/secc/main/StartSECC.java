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
