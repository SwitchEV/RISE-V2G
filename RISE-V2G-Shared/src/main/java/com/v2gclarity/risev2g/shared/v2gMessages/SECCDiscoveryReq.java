/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
package com.v2gclarity.risev2g.shared.v2gMessages;

import java.util.Arrays;

public class SECCDiscoveryReq {

	private byte security;
	private byte transportProtocol;
	private byte[] payload = new byte[2];
	
	public SECCDiscoveryReq(byte security, byte transportProtocol) {
		this.security = security;
		this.transportProtocol = transportProtocol;
	}
	
	/*
	 * REMEMBER THAT THIS BYTE ARRAY IS SUPPOSED JUST TO BE THE PAYLOAD WITHOUT THE HEADER OF THE V2GTPMESSAGE!
	 */
	public SECCDiscoveryReq(byte[] byteArray) {
		setSecurity(Arrays.copyOfRange(byteArray, 0, 1)[0]);
		setTransportProtocol(Arrays.copyOfRange(byteArray, 1, 2)[0]);
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
		this.payload[0] = this.security;
		this.payload[1] = this.transportProtocol;
		
		return this.payload;
	}
}
