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
package org.eclipse.risev2g.secc.transportLayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.misc.V2GTPMessage;
import org.eclipse.risev2g.shared.utils.MiscUtils;

/**
 * The UDP server is handling the SECCDiscovery messages only. The standard does not
 * foresee any further communication to be done via UDP but TCP. 
 * Therefore, the size of the UPD packet to be received is restricted to 10 bytes 
 * (8 bytes header of V2GTP message + 2 byte SECCDiscoveryReq payload).
 */
public class UDPServer extends Observable implements Runnable {

	/*
	 *  Eager instantiation of the Singleton, since a UDP server is always needed up front. 
	 *  The JVM creates the unique instance when the class is loaded and before any thread tries to 
	 *  access the instance variable -> thread safe.
	 */
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private static final UDPServer uniqueUDPServerInstance = new UDPServer();
	private Inet6Address multicastAddress;
	private MulticastSocket udpServerSocket;
	private byte[] udpClientRequest;
	private DatagramPacket udpClientPacket;
	private Inet6Address udpServerAddress;
    
	private UDPServer() {}
	
	/**
	 * Used to check the correct initialization of a UDP server which is a prerequisite for establishing
	 * a V2G communication session.
	 * @return True if the initialization of the UDP server was successful, false otherwise
	 */
	public boolean initialize() {
		setUdpClientRequest(new byte[10]);
		
		try {
			setUdpServerAddress(MiscUtils.getLinkLocalAddress());
			
			if (getUdpServerAddress() == null) return false;
			
			setMulticastAddress((Inet6Address) Inet6Address.getByName(GlobalValues.SDP_MULTICAST_ADDRESS.toString()));
			setUdpServerSocket(new MulticastSocket(GlobalValues.V2G_UDP_SDP_SERVER_PORT.getShortValue()));
			getUdpServerSocket().setReuseAddress(true);
			
			// Without setting the interface, the server might not react to client requests
			getUdpServerSocket().setInterface(getUdpServerAddress());
			
			getUdpServerSocket().joinGroup(getMulticastAddress());
			
			getLogger().info("UDP server initialized at link-local address " +
							 getUdpServerAddress().getHostAddress() + " and port 15118");
		} catch (UnknownHostException e) {
			getLogger().error("Unknown host exception was thrown!", e);
			return false;
		} catch (IOException e) {
			getLogger().error("MulticastSocket creation failed!", e);
			return false;
		}
		
		return true;
	}
	
	public static UDPServer getInstance() {
		return uniqueUDPServerInstance;
	}


	public void run() {
		while (!Thread.interrupted()) {
	        setUdpClientPacket(new DatagramPacket(udpClientRequest, udpClientRequest.length));
	        
	        try {
				getUdpServerSocket().receive(getUdpClientPacket());
				getLogger().debug("Message received");
				
				// Notify the session handler about a new incoming SECCDiscoveryReq message
				setChanged();
				notifyObservers(getUdpClientPacket());
	        } catch (SocketException e) {
	        	getLogger().error("SocketException", e);
	        } catch (IOException e) {
	        	getLogger().error("IOException", e);
				getUdpServerSocket().close();
			}  
	    }
	}

	
	public void stop() {
		getLogger().debug("UDP server will be stopped now");
		
		try {
			getUdpServerSocket().leaveGroup(multicastAddress);
		} catch (IOException e) {
			getLogger().error("Error occurred while trying to close TCPServerSocket (IOException)", e);
		}
		
		getUdpServerSocket().close();
		getLogger().debug("UDP server stopped (socket closed)");
	}
	
	public boolean send(V2GTPMessage message, Inet6Address udpClientAddress, int udpClientPort) {
		 byte[] v2gTPMessage = message.getMessage();
		// Set up the UDP packet containing the V2GTP message to be sent to the UDP client
		DatagramPacket udpServerPacket = new DatagramPacket(v2gTPMessage, 
															v2gTPMessage.length,
															udpClientAddress,
															udpClientPort);
		
		// Send the response to the UDP client
		try {
			udpServerSocket.send(udpServerPacket);
			getLogger().debug("Message sent");
			
			return true;
		} catch (IOException e) {
			getLogger().error("UDP response failed (IOException) while trying to send message!", e);
			return false;
		}
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Inet6Address getMulticastAddress() {
		return multicastAddress;
	}

	public void setMulticastAddress(Inet6Address multicastAddress) {
		this.multicastAddress = multicastAddress;
	}

	public MulticastSocket getUdpServerSocket() {
		return udpServerSocket;
	}

	public void setUdpServerSocket(MulticastSocket udpServerSocket) {
		this.udpServerSocket = udpServerSocket;
	}

	public byte[] getUdpClientRequest() {
		return udpClientRequest;
	}

	public void setUdpClientRequest(byte[] udpClientRequest) {
		this.udpClientRequest = udpClientRequest;
	}

	public DatagramPacket getUdpClientPacket() {
		return udpClientPacket;
	}

	public void setUdpClientPacket(DatagramPacket udpClientPacket) {
		this.udpClientPacket = udpClientPacket;
	}

	public Inet6Address getUdpServerAddress() {
		return udpServerAddress;
	}
	
	private void setUdpServerAddress(Inet6Address udpServerAddress) {
		this.udpServerAddress = udpServerAddress;
	}
}
