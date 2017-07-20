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
package org.eclipse.risev2g.shared.messageHandling;

import org.eclipse.risev2g.shared.misc.State;
import org.eclipse.risev2g.shared.misc.TimeRestrictions;

/**
 * There are four possible outcomes of processing an incoming message at the respective state
 * 1. The server generates a response message to a processed request message
 * 2. The client generates a new request message following a processed response message
 * 3. Either side switches to another state which shall process the incoming message
 * 4. The message cannot be processed at this state -> terminate session
 */
public class SendMessage extends ReactionToIncomingMessage {

	private Object payload;
	private boolean supportedAppProtocolHandshake;
	private State nextState;
	private int timeout;

	public SendMessage(Object payload, State newState) {
		this(payload, false, newState, TimeRestrictions.V2G_SECC_SEQUENCE_TIMEOUT);
	}
	
	public SendMessage(Object payload, State newState, int timeout) {
		this(payload, false, newState, timeout);
	}
	
	/**
	 * Similar to the constructor which has a timeout, except that the server has only one timeout, 
	 * namely the V2G_Message_Sequence_Timeout, which is message independent and does not need 
	 * to be provided additionally. 
	 * 
	 * @param payload
	 * @param supportedAppProtocolHandshake
	 * @param nextState
	 * @param stopV2GCommunicationSession
	 */
	public SendMessage(Object payload, boolean supportedAppProtocolHandshake, State nextState) {
		this(payload, supportedAppProtocolHandshake, nextState, TimeRestrictions.V2G_SECC_SEQUENCE_TIMEOUT);
	}
	
	public SendMessage(
			Object payload, 
			boolean supportedAppProtocolHandshake, 
			State nextState,
			int timeout) {
		super();
		this.payload = payload;
		this.supportedAppProtocolHandshake = supportedAppProtocolHandshake;
		this.nextState = nextState;
		this.timeout = timeout;
	}
	
	
	public Object getPayload() {
		return payload;
	}

	public boolean isSupportedAppProtocolHandshake() {
		return supportedAppProtocolHandshake;
	}

	public State getNextState() {
		return nextState;
	}

	public int getTimeout() {
		return timeout;
	}
}
