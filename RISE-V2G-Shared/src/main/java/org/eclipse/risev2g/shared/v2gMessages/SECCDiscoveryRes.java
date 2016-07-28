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
package org.eclipse.risev2g.shared.v2gMessages;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class SECCDiscoveryRes {
	
	private byte[] seccIPAddress;
	private byte[] seccPort;
	private byte security;
	private byte transportProtocol;
	private int payloadLength;
	private byte[] payload;
	
	public SECCDiscoveryRes(byte[] seccIPAddress, byte[] seccPort, byte security, byte transportProtocol) {
		setSeccIPAddress(seccIPAddress);
		setSeccPort(seccPort); 
		setSecurity(security);
		setTransportProtocol(transportProtocol);
		setPayloadLength(20);
	}
	
	public SECCDiscoveryRes(byte[] byteArray) {
		setSeccIPAddress(Arrays.copyOfRange(byteArray, 0, 16));
		setSeccPort(Arrays.copyOfRange(byteArray, 16, 18));
		setSecurity((Arrays.copyOfRange(byteArray, 18,19))[0]);
		setTransportProtocol((Arrays.copyOfRange(byteArray, 19,20))[0]);
	}
	

	public byte[] getSeccIPAddress() {
		return seccIPAddress;
	}
	
	public void setSeccIPAddress(byte[] seccIPAddress) {
		this.seccIPAddress = seccIPAddress;
	}
	
	public byte[] getSeccPort() {
		return seccPort;
	}
	
	public void setSeccPort(byte[] seccPort) {
		this.seccPort = seccPort;
	}
	
	public byte getSecurity() {
		return security;
	}
	
	public void setSecurity(byte security) {
		this.security = security;
	}
	
	public byte getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(byte transportProtocol) {
		this.transportProtocol = transportProtocol;
	}

	public byte[] getPayload() {
		// The order of a newly created byte buffer is always big endian in accordance to [V2G2-156]
		ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadLength);
		payloadBuffer.put(seccIPAddress)
			  		 .put(seccPort)
			  		 .put(security)
			  		 .put(transportProtocol); 
		payload = new byte[payloadLength];
		
		// Sets the messageBuffers's position in order for the .get() message to work without 
		// throwing a BufferUnderflowException
		payloadBuffer.position(0);
		
		payloadBuffer.get(payload);
		
		return payload; 
	}
	
	private void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}
	
}
