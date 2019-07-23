/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017  V2G Clarity (Dr.-Ing. Marc Mültin)
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
package com.v2gclarity.risev2g.secc.transportLayer;

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
			getLogger().info("TCP server initialized at link-local address " + 
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
			while (!Thread.currentThread().isInterrupted()) {
				getLogger().info("Waiting for new TCP client connection ...");
				setTcpClientSocket(getTcpServerSocket().accept());
				
				getLogger().info("TCP client connection with IP address " + 
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