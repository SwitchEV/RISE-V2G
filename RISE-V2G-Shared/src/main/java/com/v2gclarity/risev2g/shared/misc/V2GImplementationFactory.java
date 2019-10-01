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

import com.v2gclarity.risev2g.shared.utils.MiscUtils;

/**
 * This class serves as the base for implementation factory
 * classes used in the SE/EV projects
 * It will look up and instantiate a class based on a
 * configuration property
 */
public abstract class V2GImplementationFactory {

	/**
	 * Builds an object instance from the configuration properties
	 * The configuration should hold the class of the instance that
	 * will be built.
	 * @param propertyName Name of the property that contains the fully qualified class name
	 * @param cls Target class of the build instance
	 * @return
	 */
	protected static <T> T buildFromProperties(String propertyName, Class<T> cls) {
		try {
			String className = MiscUtils.getProperties().getProperty(propertyName);
			if (className == null) {
				return null;
			}
			
			Object instance = Class.forName(className).newInstance();
			
			if (!cls.isInstance(instance)) {
				throw new Exception("Instantiated object does not match the expected type " + cls.getCanonicalName());
			}
			return cls.cast(instance);
		} catch (Exception e) {
			throw new RuntimeException("Could not instantiate implementations class for property " + propertyName, e);
		}
	}

	
}
