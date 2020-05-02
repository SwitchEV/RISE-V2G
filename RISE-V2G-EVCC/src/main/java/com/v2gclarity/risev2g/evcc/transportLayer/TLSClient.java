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
package com.v2gclarity.risev2g.evcc.transportLayer;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.misc.TimeRestrictions;
import com.v2gclarity.risev2g.shared.misc.V2GTPMessage;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;

public class TLSClient extends StatefulTransportLayerClient {
	
	/*
	 *  Lazy instantiation of the Singleton since a TCP connection might not be
	 *  initialized if the SECCDiscovery message exchange failed.
	 *  The volatile keyword ensures that multiple threads handle the uniqueTCPClientInstance
	 *  variable correctly when it is being initialized to the TCPClient instance.
	 */
	private static volatile TLSClient uniqueTLSClientInstance;
	private SSLSocket tlsSocketToServer;
	
	public TLSClient() {} 
	
	/**
	 * Checks for an instance and creates one if there isn't one already.
	 * The synchronized block is only entered once as long as there is no existing instance of the
	 * TLSClient (safes valuable resource).
	 * @return
	 */
	public static TLSClient getInstance() {
		if (uniqueTLSClientInstance == null) {
			synchronized (TLSClient.class) {
				if (uniqueTLSClientInstance == null) {
					uniqueTLSClientInstance = new TLSClient();
				}
			}
		}
		
		return uniqueTLSClientInstance;
	}
	
	
	/**
	 * Initializes the TLS client as soon as a SECCDiscoveryRes message arrived.
	 * 
	 * @param host The address of the SECC's TLS server to connect to
	 * @param port The port of the SECC's TLS server to connect to
	 */
	public boolean initialize(Inet6Address host, int port) {
		super.initialize();
		
		try {
			/*
			 * Setting the system property for the keystore and truststore via 
			 * - System.setProperty("javax.net.ssl.keyStore", [filePath given as a String])
			 * - System.setProperty("javax.net.ssl.trustStore", [filePath given as a String])
			 * does not work in a JAR file since only getResourceAsStream works there (which on the other
			 * hand only returns an InputStream, not a file resource). Thus use setSSLFactories()
			 */
			SecurityUtils.setSSLContext(
					GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(), 
					GlobalValues.EVCC_TRUSTSTORE_FILEPATH.toString(),
					GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
			
			SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			getLogger().debug("Creating socket to TLS server ...");
			setTlsSocketToServer((SSLSocket) sslSocketFactory.createSocket(host, port));
			getLogger().debug("TLS socket to server created");
			setInStream(getTlsSocketToServer().getInputStream());
			setOutStream(getTlsSocketToServer().getOutputStream());
			
			/*
			 * The EVCC shall support at least one cipher suite as listed below according to 
			 * the standard. An implementer may decide to choose only one of them:
			 * - TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256
			 * - TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256
			 * 
			 * In ISO 15118-2, only the named elliptic curve "secp256r1" is allowed for ECDH(E). The jdk.tls.namedGroups property 
			 * contains a comma-separated list within quotation marks of enabled named groups in preference order. The list of default 
			 * named groups varies depending on what JDK release you are using. Set it on your Java command-line as follows:
			 * 
			 * $ java -Djdk.tls.namedGroups="secp256r1"
			 * 
			 * As it turns out, "secp256r1" is already the default first entry for Java 8 (and higher versions), but you should deactivate 
			 * the other elliptic curves by reducing the list to this one entry only.
			 */
			String[] enabledCipherSuites = {
					"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 
					"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256" // this cipher suite should be avoided, ECDH does not support perfect forward secrecy
			};
			getTlsSocketToServer().setEnabledCipherSuites(enabledCipherSuites);
			
			// Set the supported TLS protocol
			String[] enabledProtocols = {"TLSv1.2"};
			getTlsSocketToServer().setEnabledProtocols(enabledProtocols);
			
			/*
			 * The communication session setup timeout needs to be set here in case there is any problem with the
			 * TLS handshake.
			 * The timeout value will be overwritten with every new message being sent
			 */
			getTlsSocketToServer().setSoTimeout(TimeRestrictions.V2G_EVCC_COMMUNICATION_SETUP_TIMEOUT);
			
			getLogger().debug("Starting TLS handshake ...");
			getTlsSocketToServer().startHandshake();
			getLogger().debug("TLS handshake finished");
			
			Certificate[] seccCertificates = getTlsSocketToServer().getSession().getPeerCertificates();
			X509Certificate seccLeafCertificate = (X509Certificate) seccCertificates[0];
			
			// Check domain component of SECC certificate
			if (!SecurityUtils.verifyDomainComponent(seccLeafCertificate, "CPO")) {
				getLogger().error("TLS client connection failed. \n\t" + 
								  "Reason: Domain component of SECC certificate not valid, expected 'DC=CPO'. \n\t" +
								  "Distinuished name of SECC certificate: " + seccLeafCertificate.getSubjectX500Principal().getName());
				return false;
			}
			
			getLogger().info("TLS client connection established \n\t from link-local address " +
							  getClientAddress() + " and port " + getClientPort() + 
							  "\n\t to host " + host.getHostAddress() + " and port " + port);
			
			return true;
		} catch (UnknownHostException e) {
			getLogger().error("TLS client connection failed (UnknownHostException)!", e);
		} catch (SSLHandshakeException e) {
			getLogger().error("TLS client connection failed (SSLHandshakeException)", e);
		} catch (SocketTimeoutException e) {
			getLogger().fatal("TLS client connection failed (SocketTimeoutException) due to session setup timeout", e);
		} catch (IOException e) {
			getLogger().error("TLS client connection failed (IOException)!", e);
		} catch (NullPointerException e) {
			getLogger().fatal("NullPointerException while trying to set keystores, resource path to keystore/truststore might be incorrect");
		} 
		
		return false;
	}
	
	
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) { 
			if (getTimeout() >= 0) {
				try {
					getTlsSocketToServer().setSoTimeout(getTimeout());
					
					if (!processIncomingMessage()) break;
					
				} catch (SocketTimeoutException e) {
					stopAndNotify("A timeout occurred while waiting for response message", null);
					break;
				} catch (IOException e2) {
					stopAndNotify("An IOException occurred while trying to read message", e2);
					break;
				}
			} else {
				stopAndNotify("Timeout value is negative: " + getTimeout(), null);
				break;
			}
		}
		
		stop();
	}
	
	
	@Override
	public void send(V2GTPMessage message, int timeout) {
		setV2gTPMessage(null);
		
		try {
			getOutStream().write(message.getMessage());
			getOutStream().flush();
			getLogger().debug("Message sent");
			setTimeout(timeout);
		} catch (SSLHandshakeException e1) {
			stopAndNotify("An SSLHandshakeException occurred", e1);
		} catch (IOException e2) {
			stopAndNotify("An undefined IOException occurred while trying to send message", e2);
		}
	}
	
	@Override
	public void stop() {
		if (!isStopAlreadyInitiated()) {
			getLogger().debug("Stopping TLS client ...");
			setStopAlreadyInitiated(true);
			
			try {
				getInStream().close();
				getOutStream().close();
				getTlsSocketToServer().close();
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				getLogger().error("Error occurred while trying to close TCP socket to server", e);
			}
			
			getLogger().debug("TLS client stopped");
		}
	}
	
	
	public SSLSocket getTlsSocketToServer() {
		return tlsSocketToServer;
	}

	public void setTlsSocketToServer(SSLSocket tlsSocketToServer) {
		this.tlsSocketToServer = tlsSocketToServer;
	}

}