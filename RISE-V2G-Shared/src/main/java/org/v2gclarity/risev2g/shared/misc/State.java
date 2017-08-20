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
package org.v2gclarity.risev2g.shared.misc;

import java.security.interfaces.ECPrivateKey;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.MessageHandler;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.messageHandling.SendMessage;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public abstract class State {
	
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private State nextState = null;
	private MessageHandler messageHandler;
	private V2GCommunicationSession commSessionContext;
	private HashMap<String, byte[]> xmlSignatureRefElements;
	private ECPrivateKey signaturePrivateKey;
	
	public State(V2GCommunicationSession commSessionContext) {
		setCommSessionContext(commSessionContext);
		setMessageHandler(commSessionContext.getMessageHandler());
		setXMLSignatureRefElements(new HashMap<String, byte[]>());
	}
	
	public abstract ReactionToIncomingMessage processIncomingMessage(Object message);
	
	public SendMessage getSendMessage(
			BodyBaseType message, 
			V2GMessages nextExpectedMessage) {
		int timeout = getTimeout(message, nextExpectedMessage);
		return getSendMessage(message, nextExpectedMessage, "", timeout);
	}
	
	
	public SendMessage getSendMessage(
			BodyBaseType message, 
			V2GMessages nextExpectedMessage,
			String optionalLoggerInfo) {
		int timeout = getTimeout(message, nextExpectedMessage);
		return getSendMessage(message, nextExpectedMessage, optionalLoggerInfo, timeout);
	}
	
	
	public int getTimeout(BodyBaseType message, V2GMessages nextExpectedMessage) {
		String messageName = message.getClass().getSimpleName().replace("Type", "");
		
		// If the sent message is a response message, 60s sequence timeout is used
		if (messageName.endsWith("Res")) return TimeRestrictions.V2G_SECC_SEQUENCE_TIMEOUT;
		// otherwise the message specific timeout
		else return TimeRestrictions.getV2G_EVCC_Msg_Timeout(nextExpectedMessage);
	}
	
	
	protected SendMessage getSendMessage(
			BodyBaseType message, 
			V2GMessages nextExpectedMessage,
			int timeout) {
		return getSendMessage(message, nextExpectedMessage, "", timeout);
	}
	
	protected SendMessage getSendMessage(
			BodyBaseType message, 
			V2GMessages nextExpectedMessage,
			String optionalLoggerInfo, 
			int timeout) {
		String messageName = message.getClass().getSimpleName().replace("Type", "");
		
		@SuppressWarnings({"unchecked"})
		V2GMessage v2gMessage = getMessageHandler().getV2GMessage(
				getXMLSignatureRefElements(), 
				getSignaturePrivateKey(),
				getCommSessionContext().getMessageHandler().getJaxbElement(message)
		);
		
		getLogger().debug("Preparing to send " + messageName + " " + optionalLoggerInfo);
		
		return new SendMessage(v2gMessage, 
							   getCommSessionContext().getStates().get(nextExpectedMessage), 
							   timeout
							  );
	}
	
	public Logger getLogger() {
		return logger;
	}

	public State getNextState() {
		return nextState;
	}

	/**
	 * If a session is to be terminated, then 'null' is to be provided as parameter since the 
	 * complete session context is removed and the TCP/TLS connection is terminated
	 * @param nextState The next state which shall process incoming messages
	 */
	public void setNextState(State nextState) {
		this.nextState = nextState;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public V2GCommunicationSession getCommSessionContext() {
		return commSessionContext;
	}

	public void setCommSessionContext(V2GCommunicationSession commSessionContext) {
		this.commSessionContext = commSessionContext;
	}

	public HashMap<String, byte[]> getXMLSignatureRefElements() {
		return xmlSignatureRefElements;
	}

	public void setXMLSignatureRefElements(HashMap<String, byte[]> xmlSignatureRefElements) {
		this.xmlSignatureRefElements = xmlSignatureRefElements;
	}

	public ECPrivateKey getSignaturePrivateKey() {
		return signaturePrivateKey;
	}

	public void setSignaturePrivateKey(ECPrivateKey signaturePrivateKey) {
		this.signaturePrivateKey = signaturePrivateKey;
	}
	
}
