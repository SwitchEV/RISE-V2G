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
package org.eclipse.risev2g.secc.transportLayer;

import java.net.Inet6Address;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.shared.utils.MiscUtils;

public abstract class StatefulTransportLayerServer extends Observable implements Runnable {
	
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	private int serverPort;
	private Inet6Address serverAddress;
	
	
	protected boolean initialize() {
		setServerPort(MiscUtils.getRandomPortNumber());
		setServerAddress(MiscUtils.getLinkLocalAddress());
		
		return true;
	}
	
	public abstract void stop();
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public Inet6Address getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(Inet6Address serverAddress) {
		this.serverAddress = serverAddress;
	}
}
