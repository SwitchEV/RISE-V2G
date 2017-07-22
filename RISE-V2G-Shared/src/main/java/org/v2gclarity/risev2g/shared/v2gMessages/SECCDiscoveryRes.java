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
package org.v2gclarity.risev2g.shared.v2gMessages;

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
