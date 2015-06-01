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
package org.eclipse.risev2g.shared.enumerations;

public enum CPStates {
	STATE_A("State A"),
	STATE_B("State B"),
	STATE_C("State C"),
	STATE_D("State D");
	
	private final String value;
	
	CPStates(String v) {
        value = v;
    }
	
	
    public String value() {
        return value;
    }
}
