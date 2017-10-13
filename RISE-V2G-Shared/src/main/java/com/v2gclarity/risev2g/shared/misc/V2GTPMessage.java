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
package com.v2gclarity.risev2g.shared.misc;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.v2gclarity.risev2g.shared.utils.ByteUtils;

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
	public V2GTPMessage(byte protocolVersion, byte[] payloadType, byte[] payloadLength, byte[] payload) {
		setProtocolVersion(protocolVersion);
		setInverseProtocolVersion((byte) (protocolVersion ^ 0xFF)); 
		setPayloadType(payloadType);
		setPayloadLength(payloadLength);
		setPayload(payload);
	}
	
	/**
	 * 
	 * 
	 * @param protocolVersion
	 * @param payloadType
	 * @param payload
	 */
	public V2GTPMessage(byte protocolVersion, byte[] payloadType, byte[] payload) {
		setProtocolVersion(protocolVersion);
		setInverseProtocolVersion((byte) (protocolVersion ^ 0xFF)); 
		setPayloadType(payloadType);
		setPayloadLength(ByteUtils.toByteArrayFromInt(payload.length, false));
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
			setInverseProtocolVersion(Arrays.copyOfRange(byteArray, 1, 2)[0]);
			setPayloadType(Arrays.copyOfRange(byteArray, 2, 4));
			setPayloadLength(Arrays.copyOfRange(byteArray, 4, 8));
			// TODO make sure the byteArray is not too long to not generate a Java heap space OutOfMemoryError
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
	
	private void setInverseProtocolVersion(byte inverseProtocolVersion) {
		this.inverseProtocolVersion = inverseProtocolVersion;
	}


	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
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
