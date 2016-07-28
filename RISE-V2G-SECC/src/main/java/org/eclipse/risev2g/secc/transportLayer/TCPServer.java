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
package org.eclipse.risev2g.secc.transportLayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public final class TCPServer extends StatefulTransportLayerServer {
	
	// Eager instantiation of the Singleton TCPClient.
	private static final TCPServer uniqueTCPServerInstance = new TCPServer();
	private Socket tcpClientSocket; 
	private ServerSocket tcpServerSocket;
	
	private TCPServer() {}
	
	public static TCPServer getInstance() {
		return uniqueTCPServerInstance;
	}

	public boolean initialize() {
		super.initialize();
		
		try {
			setTcpServerSocket(new ServerSocket(getServerPort(), 50, getServerAddress()));
			getLogger().debug("TCP server initialized at link-local address " + 
					  		  getTcpServerSocket().getInetAddress().getHostAddress() +
					  		  " and port " + getTcpServerSocket().getLocalPort());
		} catch (IOException e) {
			getLogger().fatal("IOException while trying to initialize TCP server", e);
			return false;
		}
	
		return true;
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				getLogger().debug("Waiting for new TCP client connection ...");
				setTcpClientSocket(getTcpServerSocket().accept());
				
				getLogger().debug("TCP client connection with IP address " + 
								  getTcpClientSocket().getInetAddress().getHostAddress() + " and port " +
								  getTcpClientSocket().getPort());
				
				ConnectionHandler connectionHandler = new ConnectionHandler(tcpClientSocket);
				
				// Notify the V2GCommunicationSessionHandlerSECC about a newly connected TCP client Socket
				setChanged();
				notifyObservers(connectionHandler);
			}
		} catch (IOException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while running TCP server");
		} finally {
			stop();
		}
	}
	

	@Override
	public void stop() {
        try {
        	getLogger().debug("TCP server will be stopped now");
			getTcpServerSocket().close();
        } catch (SocketException e) {
        	getLogger().debug("TCPServerSocket was still active and has been closed now", e);
		} catch (IOException e) {
			getLogger().error("Error occurred while trying to close TCPServerSocket (IOException)", e);
		}
        
        getLogger().debug("TCP server stopped");
    }
	
	
	public ServerSocket getTcpServerSocket() {
		return tcpServerSocket;
	}

	public void setTcpServerSocket(ServerSocket tcpServerSocket) {
		this.tcpServerSocket = tcpServerSocket;
	}

	public Socket getTcpClientSocket() {
		return tcpClientSocket;
	}

	public void setTcpClientSocket(Socket tcpClientSocket) {
		this.tcpClientSocket = tcpClientSocket;
	}

} 