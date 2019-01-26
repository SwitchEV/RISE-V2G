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
package com.v2gclarity.risev2g.secc.misc;

import com.v2gclarity.risev2g.secc.backend.DummyBackendInterface;
import com.v2gclarity.risev2g.secc.backend.IBackendInterface;
import com.v2gclarity.risev2g.secc.evseController.DummyACEVSEController;
import com.v2gclarity.risev2g.secc.evseController.DummyDCEVSEController;
import com.v2gclarity.risev2g.secc.evseController.IACEVSEController;
import com.v2gclarity.risev2g.secc.evseController.IDCEVSEController;
import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.misc.V2GImplementationFactory;

/**
 * Implementation factory for the SECC controllers and for the backend interface
 *
 */
public class SECCImplementationFactory extends V2GImplementationFactory {

	
	/**
	 * Creates the backend interface for the SECC application
	 * @param commSessionContext the session the backend will be connected to 
	 * @return
	 */
	public static IBackendInterface createBackendInterface(V2GCommunicationSessionSECC commSessionContext) {
		IBackendInterface instance = buildFromProperties("implementation.secc.backend", IBackendInterface.class, commSessionContext);
		if (instance == null) {
			return new DummyBackendInterface(commSessionContext);
		} else {
			return instance;
		}
	}
	
	/**
	 * Creates the AC EVSE controller for the SECC application
	 * @param commSessionContext the session the backend will be connected to 
	 * @return
	 */
	public static IACEVSEController createACEVSEController(V2GCommunicationSessionSECC commSessionContext) {
		IACEVSEController instance = buildFromProperties("implementation.secc.acevsecontroller", IACEVSEController.class, commSessionContext);
		if (instance == null) {
			return new DummyACEVSEController(commSessionContext);
		} else {
			return instance;
		}
	}
	
	/**
	 * Creates the DC EVSE controller for the SECC application
	 * @param commSessionContext the session the backend will be connected to 
	 * @return
	 */
	public static IDCEVSEController createDCEVSEController(V2GCommunicationSessionSECC commSessionContext) {
		IDCEVSEController instance = buildFromProperties("implementation.secc.dcevsecontroller", IDCEVSEController.class, commSessionContext);
		if (instance == null) {
			return new DummyDCEVSEController(commSessionContext);
		} else {
			return instance;
		}
	}
	
}
