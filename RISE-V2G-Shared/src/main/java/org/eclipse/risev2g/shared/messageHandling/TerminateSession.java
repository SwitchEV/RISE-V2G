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
package org.eclipse.risev2g.shared.messageHandling;

public class TerminateSession extends ReactionToIncomingMessage {

	private String reasonForSessionStop;
	private boolean successfulTermination;
	
	/**
	 * This constructor is to be used if the session stops because of an error which automatically
	 * sets the Boolean parameter successfulTermination to false.
	 * @param reasonForSessionStop The reason for session termination
	 */
	public TerminateSession(String reasonForSessionStop) {
		this(reasonForSessionStop, false);
	}
	
	public TerminateSession(String reasonForSessionStop, boolean successfulTermination) {
		super();
		this.reasonForSessionStop = reasonForSessionStop;
		this.successfulTermination = successfulTermination;
	}

	public String getReasonForSessionStop() {
		return reasonForSessionStop;
	}

	public boolean isSuccessfulTermination() {
		return successfulTermination;
	}

	public void setSuccessfulTermination(boolean successfulTermination) {
		this.successfulTermination = successfulTermination;
	}

}
