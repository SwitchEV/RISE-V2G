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
package com.v2gclarity.risev2g.shared.exiCodec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openexi.scomp.EXISchemaFactoryErrorHandler;
import org.openexi.scomp.EXISchemaFactoryException;

public class EXISchemaFactoryExceptionHandler implements EXISchemaFactoryErrorHandler {
    
	private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
	
	public EXISchemaFactoryExceptionHandler() {
        super();
    }
	
    @Override
	public void warning(EXISchemaFactoryException eXISchemaFactoryException) throws EXISchemaFactoryException {
        logger.warn("WARN:");
    	eXISchemaFactoryException.printStackTrace();
    }

    @Override
	public void error(EXISchemaFactoryException eXISchemaFactoryException) throws EXISchemaFactoryException {
    	logger.warn("ERROR:");
    	eXISchemaFactoryException.printStackTrace();
    }

    @Override
	public void fatalError(EXISchemaFactoryException eXISchemaFactoryException) throws EXISchemaFactoryException {
    	logger.warn("FATAL:");
    	eXISchemaFactoryException.printStackTrace();
    }
    
}
