/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2017  V2G Clarity (Dr.-Ing. Marc Mültin)
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
package com.v2gclarity.risev2g.shared.utils;

			import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SupportedEnergyTransferModeType;


public final class MiscUtils {

	static Logger logger = LogManager.getLogger(MiscUtils.class.getSimpleName());
	static V2GMessages[] messageTypes = V2GMessages.values();
	static Properties v2gEntityConfig;
	
	public static Logger getLogger() {
		return logger;
	}
	
	
	/**
	 * Determines the link-local IPv6 address which is configured on the network interface provided
	 * in the properties file.
	 * @return The link-local address given as a String
	 */
	public static Inet6Address getLinkLocalAddress() {
		String networkInterfaceConfig = getPropertyValue("network.interface").toString();
		
		NetworkInterface nif = null;
		
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				nif = NetworkInterface.getByIndex(Integer.parseInt(networkInterfaceConfig));
			} else {
				nif = NetworkInterface.getByName(networkInterfaceConfig);
			}
			Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
			
			while (inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = inetAddresses.nextElement();
				
				if (inetAddress.getClass() == Inet6Address.class && inetAddress.isLinkLocalAddress()) {
					return (Inet6Address) inetAddress;
				}
			}
			
			getLogger().fatal("No IPv6 link-local address found on the network interface '" +
					nif.getDisplayName() + "' configured in the properties file");
		} catch (SocketException e) {
			getLogger().fatal("SocketException while trying to get network interface for configured name " +
							  networkInterfaceConfig + "'", e); 
		} catch (NullPointerException | NumberFormatException e2) {
			getLogger().fatal("No network interface for configured network interface index '" + 
							  networkInterfaceConfig + "' found");
		}
		
		return null;
	}
	
	
	/**
	 * Is used by the UDP client as well as by the TCP/TLS server whose ports may be in the range
	 * of 49152 and 65535.
	 * @return A port number given as an integer value.
	 */
	public static int getRandomPortNumber() {
		return (int) Math.round(Math.random() * (65535-49152)) + 49152;
	}
	
	
	public static byte[] getMacAddress() {
		String networkInterfaceConfig = getPropertyValue("network.interface").toString();
		NetworkInterface nif = null;
		byte[] macAddress = null;
		
		try {
			if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
				nif = NetworkInterface.getByIndex(Integer.parseInt(networkInterfaceConfig));
			} else {
				nif = NetworkInterface.getByName(networkInterfaceConfig);
			}
			macAddress = nif.getHardwareAddress();
		} catch (SocketException e) {
			getLogger().error("Failed to retrieve local mac address (SocketException)", e);
		}
		
		return macAddress;
	}
	
	
	/**
	 * This is a more sophisticated method compared to the getProperty(String propertyName) method
	 * of Java's Properties class. Specific knowledge about the possible values which might be
	 * mapped to the respective key in the respective properties file of EV or EVSE is incorporated
	 * in this method. The return value differs depending on the key. Therefore, the return value
	 * is given as an Object, which again must be casted to the matching type when using this method.
	 * 
	 * @param propertyName The key string written in the respective properties file of each V2G entity (EV or EVSE)
	 * @return An Object holding the data structure fitting for the key (e.g. an Enum value, a Boolean, 
	 * 			a collection, ...)
	 */
	public static Object getPropertyValue(String propertyName) {
		Object returnValue = null;
		String propertyValue = "";
		
		try {
			propertyValue = getV2gEntityConfig().getProperty(propertyName).replaceAll("\\s", "");
		} catch (NullPointerException e) {
			getLogger().warn("No entry found in the properties file for property '" + propertyName + "'", e);
			return null;
		}
		
		switch (propertyName) {
		case "network.interface": // EV + EVSE property
			returnValue = propertyValue;
			break;
		case "session.id": // EV property
			try {
				returnValue = Long.parseLong(propertyValue);
			} catch (NumberFormatException e) {
				getLogger().warn("SessionID '" + propertyValue + "' not supported. " +
							     "Setting default value to 0.", e);
				getV2gEntityConfig().setProperty("session.id", "0");
				returnValue = 0L;
			}
			break;
		case "energy.transfermodes.supported": // EVSE property
			String energyTransferMode = "";
			SupportedEnergyTransferModeType supportedEnergyTransferModeType = new SupportedEnergyTransferModeType();
			if (!propertyValue.equals("")) {
				String[] supportedEnergyTranserModes = propertyValue.split(",");
				for (String tmp : supportedEnergyTranserModes) {
					energyTransferMode = tmp;
					try {
						supportedEnergyTransferModeType.getEnergyTransferMode().add(EnergyTransferModeType.fromValue(energyTransferMode));
					} catch (IllegalArgumentException e){
						getLogger().warn("EnergyTransferModeType '" + energyTransferMode + "' not supported");
					}
				}
			}
			returnValue = supportedEnergyTransferModeType.getEnergyTransferMode();
			break;
		case "energy.transfermode.requested": // EV property
			try {
				if (!propertyValue.equals("")) returnValue = EnergyTransferModeType.fromValue(propertyValue);
				else return null;
			} catch (IllegalArgumentException e) {
				getLogger().warn("EnergyTransferModeType '" + propertyValue + "' not supported");
				return null;
			}
			break;
		case "tls": // EV property (with this code, TLS is always supported on EVSE side)
			if (Boolean.parseBoolean(propertyValue)) returnValue = GlobalValues.V2G_SECURITY_WITH_TLS.getByteValue();
			else returnValue = GlobalValues.V2G_SECURITY_WITHOUT_TLS.getByteValue();
			break;
		case "contract.certificate.update.timespan": // EV property
			try {
				returnValue = Integer.parseInt(propertyValue);
			} catch (NumberFormatException e) {
				getLogger().warn("ContractCertificateUpdateTimespan '" + propertyValue + "' not supported. " +
							     "Setting default value to 14.", e);
				getV2gEntityConfig().setProperty("contract.certificate.update.timespan", "14");
				returnValue = 14;
			}
			break;
		case "authentication.mode": // EV property
			try {
				if (!propertyValue.equals("")) returnValue = PaymentOptionType.fromValue(propertyValue);
				else return null;
			} catch (IllegalArgumentException e) {
				getLogger().warn("PaymentOptionType '" + propertyValue + "' not supported");
				return null;
			}
			break;
		case "authentication.modes.supported": // EVSE property
			// The EVCC needs only one selected option, whereas the SECC can offer Contract AND ExternalPayment
			String option = "";
			ArrayList<PaymentOptionType> paymentOptionsList = new ArrayList<PaymentOptionType>();
			if (!propertyValue.equals("")) {
				String[] paymentOptions = propertyValue.split(",");	
				for (String tmp : paymentOptions) {
					option = tmp;
					try {
						paymentOptionsList.add(PaymentOptionType.fromValue(option));
					} catch (IllegalArgumentException e) {
						getLogger().warn("PaymentOptionType '" + option + "' not supported");
					}
				}
			}
			returnValue = paymentOptionsList;
			break;
		case "charging.free": // EVSE property
			returnValue = Boolean.parseBoolean(propertyValue);
			break;
		case "environment.private": // EVSE property
			returnValue = Boolean.parseBoolean(propertyValue);
			break;
		case "exi.messages.showxml": // EV + EVSE property
			if (Boolean.parseBoolean(propertyValue)) returnValue = true;
			else returnValue = false;
			break;
		case "exi.messages.showhex": // EV + EVSE property
			if (Boolean.parseBoolean(propertyValue)) returnValue = true;
			else returnValue = false;
			break;
		case "signature.verification.showlog": // EV + EVSE property
			if (Boolean.parseBoolean(propertyValue)) returnValue = true;
			else returnValue = false;
			break;
		case "exi.codec": // EV + EVSE property
			if (propertyValue.equals("open_exi")) returnValue = "open_exi";
			else returnValue = "exificient";
			break;
		case "voltage.accuracy": // EV property
			try {
				returnValue = Integer.parseInt(propertyValue);
			} catch (NumberFormatException e) {
				getLogger().warn("Voltage accuracy '" + propertyValue + "' not supported. " +
							     "Setting default value to 5.", e);
				getV2gEntityConfig().setProperty("voltage.accuracy", "5");
				returnValue = 5;
			}
			break;
		default:
			getLogger().error("No property with name '" + propertyName + "' found");
		}
		
		return returnValue;
	}


	/**
	 * Provides the Properties object created upon initialization of the EVCC/SECC instance 
	 * by loading a given .properties file. With the Properties object, one can retrieve the 
	 * (key, value)-pairs which are each given as Strings.
	 * 
	 * @return The Properties object containing the (key, value)-pairs of the respective properties
	 * 			file for the respective V2G entity (EVCC or SECC)
	 */
	public static Properties getV2gEntityConfig() {
		return v2gEntityConfig;
	}


	/**
	 * Instantiates the properties object with the location of the properties file given as an argument.
	 * The properties file itself must reside outside the JAR file, at the same level as the JAR file itself,
	 * because it needs to be editable (JAR file is read-only). 
	 * Therefore, the file is not loaded with getResoruceAsStream(), but with a FileInputStream.
	 * 
	 * @param propertiesFileLocation The location of the properties file
	 * @return True, if the properties file could be loaded successfully.
	 */
	public static boolean setV2gEntityConfig(String propertiesFileLocation) {
		Properties properties = new Properties();
		
		try {
			FileInputStream config = new FileInputStream(propertiesFileLocation);
			properties.load(config);
			v2gEntityConfig = properties;
			config.close();
			return true;
		} catch (FileNotFoundException e) {
			getLogger().error("Properties file location '" + propertiesFileLocation + "' not found (FileNotFoundException)."
							+ "Error occurred while trying to set config properties file.");
			return false;
		} catch (IOException e2) {
			getLogger().error("Unable to load properties file at location '" + propertiesFileLocation + "' (IOException)"
							+ "Error occurred while trying to set config properties file.");
			return false;
		}
	}
}
