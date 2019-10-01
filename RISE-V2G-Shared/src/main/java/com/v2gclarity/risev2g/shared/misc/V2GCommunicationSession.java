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
package com.v2gclarity.risev2g.shared.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.MessageHandler;
import com.v2gclarity.risev2g.shared.messageHandling.PauseSession;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public abstract class V2GCommunicationSession extends Observable {

	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private HashMap<V2GMessages, State> states;
	private State currentState;
	private State startState;
	private MessageHandler messageHandler;
	private byte[] sessionID;
	private V2GTPMessage v2gTpMessage;
	private V2GMessage v2gMessage;
	private boolean tlsConnection;
	
	public V2GCommunicationSession() {
		setStates(new HashMap<V2GMessages, State>());
		setMessageHandler(MessageHandler.getInstance());
		setSessionID(null);
		setV2gTpMessage(null);
	}

	
	/**
	 * Generates randomly a new session ID (with length of 8 bytes) and takes care that the newly generated 
	 * session ID does not match the store previous session ID and that it is unequal to 0.
	 * @return The byte array representation of the provided session ID
	 */
	public byte[] generateSessionIDRandomly() {
		byte[] sessionID = new byte[8];
		
		while (sessionID == null || ByteUtils.toLongFromByteArray(sessionID) == 0L || Arrays.equals(sessionID, getSessionID())) {
			sessionID = SecurityUtils.generateRandomNumber(8);
		}
	
		return sessionID;
	}
	
	protected void pauseSession(PauseSession pauseObject) {
		getLogger().info("Pausing V2G communication session");
		setChanged();
		notifyObservers(pauseObject);
	}
	
	
	protected void terminateSession(TerminateSession termination) {
		String terminationPrefix = "Terminating V2G communication session, reason: ";
		
		if (termination.isSuccessfulTermination()) {
			getLogger().info(terminationPrefix + termination.getReasonForSessionStop());
		} else {
			getLogger().warn(terminationPrefix + termination.getReasonForSessionStop());
		}
		
		setChanged();
		notifyObservers(termination);
	}
	
	/**
	 * Should be used if no TerminateSession instance has been provided by the respective state 
	 * but some other case causes a session termination
	 * 
	 * @param reason The termination cause
	 * @param successful True, in case of a successful session termination, false otherwise
	 */
	protected void terminateSession(String reason, boolean successful) {
		String terminationPrefix = "Terminating V2G communication session, reason: "; 
		
		TerminateSession termination = new TerminateSession(reason, successful);
		if (successful)	getLogger().debug(terminationPrefix + reason);
		else getLogger().error(terminationPrefix + reason);
		
		setChanged();
		notifyObservers(termination);
	}

	
	public ArrayList<EnergyTransferModeType> getSupportedEnergyTransferModes() {
		@SuppressWarnings("unchecked")
		ArrayList<EnergyTransferModeType> energyTransferModes = 
				(MiscUtils.getPropertyValue("energy.transfermodes.supported") != null) ?
				((ArrayList<EnergyTransferModeType>) MiscUtils.getPropertyValue("energy.transfermodes.supported")) :
				new ArrayList<EnergyTransferModeType>();
		
		return energyTransferModes;
	}
	

	public Logger getLogger() {
		return logger;
	}

	public HashMap<V2GMessages, State> getStates() {
		return states;
	}

	public void setStates(HashMap<V2GMessages, State> states) {
		this.states = states;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State newState) {
		this.currentState = newState;
		if (newState == null) {
			getLogger().error("New state is not provided (null)");
		} else {
			getLogger().debug("New state is " + this.currentState.getClass().getSimpleName());
		}
	}
	
	public State getStartState() {
		return startState;
	}

	public void setStartState(State startState) {
		this.startState = startState;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public byte[] getSessionID() {
		return sessionID;
	}

	public void setSessionID(byte[] sessionID) {
		if (sessionID == null) {
			sessionID = ByteUtils.toByteArrayFromHexString("00");
		} 
		this.sessionID = sessionID;
	}

	public V2GTPMessage getV2gTpMessage() {
		return v2gTpMessage;
	}

	public void setV2gTpMessage(V2GTPMessage v2gTpMessage) {
		this.v2gTpMessage = v2gTpMessage;
	}
	
	
	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}


	public V2GMessage getV2gMessage() {
		return v2gMessage;
	}


	public void setV2gMessage(V2GMessage v2gMessage) {
		this.v2gMessage = v2gMessage;
	}
	
	public boolean isTlsConnection() {
		return tlsConnection;
	}


	public void setTlsConnection(boolean tlsConnection) {
		this.tlsConnection = tlsConnection;
	}
}
