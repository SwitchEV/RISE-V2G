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
package com.v2gclarity.risev2g.evcc.transportLayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet6Address;
import java.util.Arrays;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import com.v2gclarity.risev2g.shared.misc.V2GTPMessage;
import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;

public abstract class StatefulTransportLayerClient  extends Observable implements Runnable {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private byte[] v2gTPHeader; 
	private byte[] v2gTPPayload;
	private byte[] v2gTPMessage;
	private InputStream inStream;
	private OutputStream outStream;
	private final int MASK = 0x80;
	private int payloadLength;
	private int bytesReadFromInputStream;
	private Inet6Address clientAddress;
	private int clientPort;
	private int timeout;
	private boolean stopAlreadyInitiated;
	
	protected void initialize() {
		getLogger().debug("Initializing client connection ...");
		setClientPort(MiscUtils.getRandomPortNumber());
		setClientAddress(MiscUtils.getLinkLocalAddress());
		setV2gTPHeader(new byte[8]);
		setTimeout(TimeRestrictions.getV2gEvccMsgTimeout(V2GMessages.SUPPORTED_APP_PROTOCOL_RES)); // Needed for the supportedAppProtocol timeout
	}
	
	protected boolean processIncomingMessage() throws IOException {
		/*
		 * Read header (8 bytes) of incoming V2GTPMessage to further allocate a byte array with  
		 * the appropriate length. 
		 */
		try {
			setBytesReadFromInputStream(getInStream().read(getV2gTPHeader()));
		} catch (IOException e) {
			/* 
			 * If there are no bytes buffered on the socket, or all buffered bytes have been consumed by read, 
			 * then all subsequent calls to read will throw an IOException.
			 */
			stopAndNotify("IOExeption occurred while trying to read the header of the incoming message. "
							+ "Maybe timeout occurred?", e);
			return false;
		} catch (NullPointerException e2) {
			stopAndNotify("NullPointerException occurred while trying to read the header of the incoming message", e2);
			return false;
		}
	
		if (getBytesReadFromInputStream() < 0) {
			stopAndNotify("No bytes read from input stream, server socket seems to be closed", null);
			return false;
		}
	
		/*
		 * The payload length is written to the last 4 bytes (v2gTPHeader[4] to v2gTPHeader[7])
		 * of the V2GTP header. The most significant bit of v2gTPHeader[4] should never be set!
		 * If it was set, then this would mean that a V2GTP message of a size of at least 2 GB 
		 * was intended to be transferred ... and this cannot be, no V2G message has this size!
		 * Since the most significant bit should never be set, we do not need to care about
		 * signed integers in Java at this point!
		 */
		if ((getV2gTPHeader()[4] & getMASK()) == getMASK()) {
			stopAndNotify("Payload length of V2GTP message is inappropiately high! There must be " +
						  "an error in the V2GTP message header!", null);
			return false;
		} else {
			setPayloadLength(ByteUtils.toIntFromByteArray(Arrays.copyOfRange(getV2gTPHeader(), 4, 8)));
			setV2gTPPayload(new byte[getPayloadLength()]);
		
			getInStream().read(getV2gTPPayload());
		
			getLogger().debug("Message received");
		
			setV2gTPMessage(new byte[getV2gTPHeader().length + getV2gTPPayload().length]);
			System.arraycopy(getV2gTPHeader(), 0, getV2gTPMessage(), 0, getV2gTPHeader().length);
			System.arraycopy(getV2gTPPayload(), 0, getV2gTPMessage(), getV2gTPHeader().length, getV2gTPPayload().length);
		}
	
		// Block another while-run before the new Socket timeout has been provided by send()
		// TODO is there a more elegant way of blocking (this is rather resource-consuming)?
		setTimeout(-1); 
	
		setChanged();
		notifyObservers(getV2gTPMessage());
		
		return true;
	}
	
	public abstract void send(V2GTPMessage message, int timeout);
	
	/**
	 * If an error occurred in the run()-method, the TCP client will be stopped by closing all streams
	 * and the socket and interrupting the Thread. V2GCommunicationSessionEVCC will be notified as well.
	 * The method's statements will not be executed if a stop of the TCP client has already been
	 * initiated by the V2GCommunicationSessionEVCC (which might induce an error in the run()-method).
	 * 
	 * @param errorMessage An error message explaining the reason for the error
	 * @param e An optional exception
	 */
	protected void stopAndNotify(String errorMessage, Exception e) {
		if (!isStopAlreadyInitiated()) {
			getLogger().error(errorMessage, e);
			stop();
			setStopAlreadyInitiated(true);
			
			// Notify V2GCommunicationSessionEVCC about termination of session
			setChanged();
			notifyObservers(new TerminateSession(errorMessage));
		}
	}
	

	public abstract void stop();
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public byte[] getV2gTPHeader() {
		return v2gTPHeader;
	}

	public void setV2gTPHeader(byte[] v2gTPHeader) {
		this.v2gTPHeader = v2gTPHeader;
	}

	public byte[] getV2gTPPayload() {
		return v2gTPPayload;
	}

	public void setV2gTPPayload(byte[] v2gTPPayload) {
		this.v2gTPPayload = v2gTPPayload;
	}

	public byte[] getV2gTPMessage() {
		return v2gTPMessage;
	}

	public void setV2gTPMessage(byte[] v2gTPMessage) {
		this.v2gTPMessage = v2gTPMessage;
	}
	
	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	public OutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(OutputStream outStream) {
		this.outStream = outStream;
	}

	public int getPayloadLength() {
		return payloadLength;
	}

	public void setPayloadLength(int payloadLength) {
		this.payloadLength = payloadLength;
	}

	public int getBytesReadFromInputStream() {
		return bytesReadFromInputStream;
	}

	public void setBytesReadFromInputStream(int bytesReadFromInputStream) {
		this.bytesReadFromInputStream = bytesReadFromInputStream;
	}

	public int getMASK() {
		return MASK;
	}
	
	public Inet6Address getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(Inet6Address clientAddress) {
		this.clientAddress = clientAddress;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isStopAlreadyInitiated() {
		return stopAlreadyInitiated;
	}

	public void setStopAlreadyInitiated(boolean stopAlreadyInitiated) {
		this.stopAlreadyInitiated = stopAlreadyInitiated;
	}
	
}
