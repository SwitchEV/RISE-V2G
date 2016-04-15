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
package org.eclipse.risev2g.shared.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ByteUtils {

	static Logger logger = LogManager.getLogger(MiscUtils.class.getSimpleName());
	
	public static Logger getLogger() {
		return logger;
	}
	
	/**
	 * Returns a hexadecimal string from a given byte array.
	 * 
	 * @param  array The byte array which is to transformed into a hexadecimal string
	 * @return The hexadecimal string
	 */
	public static String toHexString(byte[] array) {
		if (array != null)
			return DatatypeConverter.printHexBinary(array);
		else
			return "";
	}
	

	/**
	 * Returns a byte array from a given hex string.
	 * 
	 * @param  hexString The hexadecimal string representing a byte array. The lexical value space 
	 * 		   			 defined in XML Schema Part 2: Datatypes for xsd:hexBinary is expected: 
	 * 					 "hexBinary has a lexical representation where each binary octet is encoded 
	 * 					 as a character tuple, consisting of two hexadecimal digits ([0-9a-fA-F]) 
	 * 					 representing the octet code. For example, '0FB7' is a hex encoding for the 
	 * 					 16-bit integer 4023 (whose binary representation is 111110110111)."
	 * @return A byte array representing the hexadecimal string
	 */
	public static byte[] toByteArrayFromHexString(String s) {
	    return DatatypeConverter.parseHexBinary(s);
	}
	
	/**
	 * Returns a byte from a given hex string. Be careful to provide only a hex string which represents only
	 * one byte because only the first byte of the newly created byte array will be returned.
	 * 
	 * @param  hexString The hexadecimal string representing a byte
	 * @return One byte representing the hexadecimal string. If the hex string would represent more than
	 *         one byte, then the remaining byteArraySize-firstByte bytes are cut off.
	 */
	public static byte toByteFromHexString(String hexString) {
		byte[] byteArray = DatatypeConverter.parseHexBinary(hexString);
	    
		return byteArray[0];
	}
	
	
	/**
	 * Converts an integer (which in Java is a signed four bytes value) into a byte array
	 * @param intValue The integer value which is to be converted to a byte array
	 * @param shortSize Determines if the integer value shall be represented as a 4-byte or a 2-byte array.
	 * 		  Example: A port number between 49152 and 65535 for UDP client or TCP server is represented
	 * 		  by a (signed) integer value with 4 bytes in Java. The SECCDiscoveryResponse, however, holds
	 * 		  only two bytes for the port, thus the leading 16 bytes consisting of 0s must be cut off. 
	 * @return The byte array corresponding to the given integer value, either with a size of 2 or 4 bytes.
	 */
	public static byte[] toByteArrayFromInt(int intValue, boolean shortSize) {
		byte[] tempArr = ByteBuffer.allocate(4).putInt(intValue).array();
		byte[] retArr;
		
		if (shortSize) {
			retArr = ByteBuffer.allocate(2).put(tempArr, 2, 2).array();
		} else {
			retArr = tempArr;
		}
		
		return retArr;
	}
	
	/**
	 * Converts a long (which in Java is a signed eight bytes value) into a byte array
	 * @param longValue The long value which is to be converted to a byte array
	 * @return The byte array corresponding to the given long value
	 */
	public static byte[] toByteArrayFromLong(long longValue) {
		return ByteBuffer.allocate(8).putLong(longValue).array();
	}
	
	
	/**
	 * Returns a string of 0s and 1s reflecting the byte array which was provided as input.
	 * This method can be used for debugging purposes.
	 * 
	 * @param byteArray The byte array which is to represent as a string of 0s and 1s
	 * @return A string representing the bit values of the byte array
	 */
	public static String toStringFromByteArray(byte[] byteArray) {
		StringBuffer byteArrayString = new StringBuffer();
		
		for (byte b : byteArray) {
			for (int mask = 0x80; mask != 0x00; mask >>= 1) {
				boolean value = ( b & mask ) != 0;
				char valueChar = (value) ? '1' : '0';
				byteArrayString.append(valueChar);
			}
			byteArrayString.append(" ");
		}
		
		return byteArrayString.toString();
	}
	
	/**
	 * Returns a string of 0s and 1s reflecting the byte which was provided as input.
	 * This method can be used for debugging purposes.
	 * 
	 * @param byteValue The byte which is to represent as a string of 0s and 1s
	 * @return A string representing the bit values of the byte
	 */
	public static String toStringFromByte(byte byteValue) {
		StringBuffer byteArrayString = new StringBuffer();

		for (int mask = 0x80; mask != 0x00; mask >>= 1) {
			boolean value = ( byteValue & mask ) != 0;
			char valueChar = (value) ? '1' : '0';
			byteArrayString.append(valueChar);
		}
		
		return byteArrayString.toString();
	}
	
	/**
	 * Returns an integer value out of a byte array. 
	 * 
	 * @param byteArray The byte array to be converted into its decimal representation
	 * @return The integer value representing the byte array
	 */
	public static int toIntFromByteArray(byte[] byteArray) {
		// Allocating a byte buffer holding 4 bytes for the int value
		ByteBuffer bb = ByteBuffer.allocate(4);
		
		/*
		 * The given byte array might hold less than 4 bytes, e.g. the SECC port of the SECC Discovery
		 * Response only holds 2 bytes. Thus, we must account for this and guarantee the Big Endian 
		 * byte order.
		 */
		bb.position(4 - byteArray.length);
		bb.put(byteArray);
		
		// Setting the current position to 0, otherwise getInt() would throw a BufferUnderflowException
		bb.position(0);
		
		return bb.getInt();
	}
	
	/**
	 * Returns a long value out of a byte array.
	 * 
	 * @param byteArray The byte array to be converted into its decimal representation
	 * @return The long value representing the byte array
	 */
	public static long toLongFromByteArray(byte[] byteArray) {
		// Allocating a byte buffer holding 8 bytes for the long value
		ByteBuffer bb = ByteBuffer.allocate(8);
			
		// In case the provided byte array is smaller than 8 bytes (e.g. int has 4 bytes), take care that they are placed at the right-most position
		if (byteArray.length < 8) {
			bb.position(8-byteArray.length);
			bb.put(byteArray);
		} else {
			try {
				bb.put(byteArray);
			} catch (BufferOverflowException e) {
				getLogger().warn("Byte array length is too big (" + byteArray.length + " bytes) to be converted " +
								 "into a long value. Only the right-most 8 bytes (least significant bytes " +
								 "according to Big Endian) are used.", e);
				bb.position(0);
				bb.put(byteArray, byteArray.length - 8, byteArray.length);
			}
		}
		
		// Setting the current position to 0, otherwise getLong() would throw a BufferUnderflowException
		bb.position(0);
		
		return bb.getLong();
	}
	
	
	public OutputStream inputStreamToOutputStream(InputStream input, OutputStream output) {
		byte[] buffer = new byte[1024];
	    int bytesRead;
	    
	    try {
			while ((bytesRead = input.read(buffer)) != -1) {
			    output.write(buffer, 0, bytesRead);
			}
			
			return output;
		} catch (IOException e) {
			getLogger().error("IOException while trying to write InputStream to OutputStream");
			return null;
		}
	}
}
