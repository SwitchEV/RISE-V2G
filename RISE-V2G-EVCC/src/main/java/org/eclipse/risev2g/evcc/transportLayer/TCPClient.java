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
package org.eclipse.risev2g.evcc.transportLayer;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.eclipse.risev2g.shared.misc.V2GTPMessage;

public class TCPClient extends StatefulTransportLayerClient {
	
	/*
	 *  Lazy instantiation of the Singleton since a TCP connection might not be
	 *  initialized if the SECCDiscovery message exchange failed.
	 *  The volatile keyword ensures that multiple threads handle the uniqueTCPClientInstance
	 *  variable correctly when it is being initialized to the TCPClient instance.
	 */
	private static volatile TCPClient uniqueTCPClientInstance;
	private Socket tcpSocketToServer;
	
	public TCPClient() {} 
	
	/**
	 * Checks for an instance and creates one if there isn't one already.
	 * The synchronized block is only entered once as long as there is no existing instance of the
	 * TCPClient (safes valuable resource).
	 * @return
	 */
	public static TCPClient getInstance() {
		if (uniqueTCPClientInstance == null) {
			synchronized (TCPClient.class) {
				if (uniqueTCPClientInstance == null) {
					uniqueTCPClientInstance = new TCPClient();
				}
			}
		}
		
		return uniqueTCPClientInstance;
	}
	
	
	/**
	 * Initializes the TCP client as soon as a SECCDiscoveryRes message arrived.
	 * 
	 * @param host The address of the SECC's TCP server to connect to
	 * @param port The port of the SECC's TCP server to connect to
	 */
	public boolean initialize(Inet6Address host, int port) {
		super.initialize();
		
		try {
			setTcpSocketToServer(new Socket(host, port));  
			setInStream(getTcpSocketToServer().getInputStream());
			setOutStream(getTcpSocketToServer().getOutputStream());

			getLogger().debug("TCP client connection established \n\t from link-local address " +
							  getClientAddress() + " and port " + getClientPort() + 
							  "\n\t to host " + host.getHostAddress() + " and port " + port);
			
			return true;
		} catch (UnknownHostException e) {
			getLogger().error("TCP client connection failed (UnknownHostException)!", e);
		} catch (IOException e) {
			getLogger().error("TCP client connection failed (IOException)!", e);
		}
		
		return false;
	}
	
	
	@Override
	public void run() {
		while (!Thread.interrupted()) { 
			if (getTimeout() > 0) {
				try {
					getSocketToServer().setSoTimeout(getTimeout());
					
					if (!processIncomingMessage()) break;
					
				} catch (SocketTimeoutException e) {
					stopAndNotify("A timeout occurred while waiting for response message", null);
					break;
				} catch (IOException e2) {
					stopAndNotify("An IOException occurred while trying to read message", e2);
					break;
				}
			}
		}
	}
	
	
	@Override
	public void send(V2GTPMessage message, int timeout) {
		setV2gTPMessage(null);
		
		try {
			getOutStream().write(message.getMessage());
			getOutStream().flush();
			getLogger().debug("Message sent");
			setTimeout(timeout);
		} catch (IOException e) {
			getLogger().error("An undefined IOException occurred while trying to send message", e);
		}
	}
	
	@Override
	public void stop() {
		if (!isStopAlreadyInitiated()) {
			getLogger().debug("Stopping TCP client ...");
			setStopAlreadyInitiated(true);
			
			try {
				getInStream().close();
				getOutStream().close();
				getTcpSocketToServer().close();
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				getLogger().error("Error occurred while trying to close TCP socket to server", e);
			} 
			
			getLogger().debug("TCP client stopped");
		}
	}
	
	public Socket getTcpSocketToServer() {
		return tcpSocketToServer;
	}

	public void setTcpSocketToServer(Socket tcpSocketToServer) {
		this.tcpSocketToServer = tcpSocketToServer;
	}


	public Socket getSocketToServer() {
		return tcpSocketToServer;
	}

	public void setSocketToServer(Socket socketToServer) {
		this.tcpSocketToServer = socketToServer;
	}
}