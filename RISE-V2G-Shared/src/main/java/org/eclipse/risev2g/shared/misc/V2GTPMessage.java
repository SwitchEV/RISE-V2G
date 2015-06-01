/*******************************************************************************
 *  Copyright (c) 2015 Marc Mültin (Chargepartner GmbH).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Dr.-Ing. Marc Mültin (Chargepartner GmbH) - initial API and implementation and initial documentation
 *******************************************************************************/
package org.eclipse.risev2g.shared.misc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class V2GTPMessage {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private byte protocolVersion;
	private byte inverseProtocolVersion;
	private byte[] payload;
	private byte[] payloadType;
	private byte[] payloadLength;
	private byte[] header = new byte[8];
	private byte[] message;
	
	/**
	 * Constructs a V2G Transfer Protocol message containing the header (which consists of the protocol version,
	 * the inverse protocol version, the payload type and payload length) and the payload.
	 * 
	 * @param protocolVersion The protocol version to be used
	 * @param payloadType 	  The type of the payload (EXI encoded message, SDP request or response)
	 * @param payload		  The payload of the message to be sent
	 */
	public V2GTPMessage(byte protocolVersion, byte[] payloadType, byte[] payload) {
		setProtocolVersion(protocolVersion);
		setPayloadType(payloadType);
		setPayload(payload);
	}
	
	/**
	 * Constructs a V2G Transfer Protocol message containing the header (which consists of the protocol version,
	 * the inverse protocol version, the payload type and payload length) and the payload. 
	 * 
	 * @param byteArray The respective fields of the V2G Transfer Protocol message are set by assigning the
	 * 					respective bytes from the array to the fields.
	 */
	public V2GTPMessage(byte[] byteArray) {
		// Check if this could be a real V2GTPMessage which has 8 bytes of header
		if (byteArray != null && byteArray.length >= 8) {
			setProtocolVersion(Arrays.copyOfRange(byteArray, 0, 1)[0]);
			setPayloadType(Arrays.copyOfRange(byteArray, 2, 4));
			setPayload(Arrays.copyOfRange(byteArray, 8, byteArray.length));
		} else {
			getLogger().error("Received byte array does not match a V2GTPMessage");
		}
	}
	
	public byte getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(byte protocolVersion) {
		this.protocolVersion = protocolVersion;
		
		setInverseProtocolVersion(protocolVersion);
	}

	public byte getInverseProtocolVersion() {
		return inverseProtocolVersion;
	}
	
	private void setInverseProtocolVersion(byte protocolVersion) {
		this.inverseProtocolVersion = (byte) (protocolVersion ^ 0xFF);
	}


	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
		
		// Byte array reflecting the number of bytes of the payload
		setPayloadLength(ByteBuffer.allocate(4).putInt(payload.length).array());
	}
	
	public byte[] getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(byte[] payloadType) {
		this.payloadType = payloadType;
	}

	public byte[] getPayloadLength() {
		return payloadLength;
	}
	
	private void setPayloadLength(byte[] payloadLength) {
		this.payloadLength = payloadLength;
	}
	
	/**
	 * Returns a byte array representation of the V2GTPMessage. This byte array contains the 
	 * protocolVersion, inverseProtocolVersion, payloadType, payloadLength and the payload itself.
	 * <p>
	 * @return byte[] The V2GTPMessage byte array
	 */
	public byte[] getMessage() {
		// Remark: The order of a newly created byte buffer is always big endian (see [V2G2-085] on page 27)
		ByteBuffer messageBuffer = ByteBuffer.allocate(8 + this.getPayload().length);
		messageBuffer.put(this.protocolVersion)
			  		 .put(this.inverseProtocolVersion)
			  		 .put(this.payloadType)
				     .put(this.payloadLength)
					 .put(this.getPayload());
		this.message = new byte[messageBuffer.capacity()];
		
		// Sets the messageBuffers's position in order for the .get() message to work without 
		// throwing a BufferUnderflowException
		messageBuffer.position(0);
		
		messageBuffer.get(this.message);

		return this.message; 
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public byte[] getHeader() {
		return header;
	}

	public void setHeader(byte[] header) {
		this.header = header;
	}

}
