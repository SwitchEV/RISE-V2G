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

public class ChangeProcessingState extends ReactionToIncomingMessage {

	private Object payload;
	private State newState;
	
	public ChangeProcessingState(Object payload, State newState) {
		super();
		this.payload = payload;
		this.newState = newState;
	}

	public Object getPayload() {
		return payload;
	}

	public State getNewState() {
		return newState;
	}

}
