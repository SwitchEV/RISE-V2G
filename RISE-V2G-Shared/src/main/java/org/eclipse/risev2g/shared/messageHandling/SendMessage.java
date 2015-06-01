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
