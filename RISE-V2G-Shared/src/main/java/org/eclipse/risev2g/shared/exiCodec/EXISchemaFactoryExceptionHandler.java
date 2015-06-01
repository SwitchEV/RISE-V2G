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
package org.eclipse.risev2g.shared.exiCodec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openexi.scomp.EXISchemaFactoryErrorHandler;
import org.openexi.scomp.EXISchemaFactoryException;

public class EXISchemaFactoryExceptionHandler implements EXISchemaFactoryErrorHandler {
    
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	
	public EXISchemaFactoryExceptionHandler() {
        super();
    }
	
    public void warning(EXISchemaFactoryException eXISchemaFactoryException) throws EXISchemaFactoryException {
        logger.warn("WARN:");
    	eXISchemaFactoryException.printStackTrace();
    }

    public void error(EXISchemaFactoryException eXISchemaFactoryException) throws EXISchemaFactoryException {
    	logger.warn("ERROR:");
    	eXISchemaFactoryException.printStackTrace();
    }

    public void fatalError(EXISchemaFactoryException eXISchemaFactoryException) throws EXISchemaFactoryException {
    	logger.warn("FATAL:");
    	eXISchemaFactoryException.getStackTrace();
    }
    
}