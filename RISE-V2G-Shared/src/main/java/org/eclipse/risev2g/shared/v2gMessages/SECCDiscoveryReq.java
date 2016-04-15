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
