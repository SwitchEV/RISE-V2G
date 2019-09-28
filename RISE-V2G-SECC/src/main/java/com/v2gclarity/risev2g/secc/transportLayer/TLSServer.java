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
package com.v2gclarity.risev2g.secc.transportLayer;

import java.io.IOException;
import java.net.SocketException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;

public final class TLSServer extends StatefulTransportLayerServer {

	private static final TLSServer uniqueTLSServerInstance = new TLSServer();
	private SSLSocket tlsClientSocket; 
	private SSLServerSocket tlsServerSocket;
	
	private TLSServer() {}
	
	public static TLSServer getInstance() {
		return uniqueTLSServerInstance;
	}
	
	/**
	 * Used to check the correct initialization of a TCP server which is a prerequisite for establishing
	 * a V2G communication session.
	 * @return True if the initialization of the TCP server was successful, false otherwise
	 */
	public boolean initialize() {
		super.initialize();
		
		try {
			/*
			 * Setting the system property for the keystore and truststore via 
			 * - System.setProperty("javax.net.ssl.keyStore", [filePath given as a String])
			 * - System.setProperty("javax.net.ssl.trustStore", [filePath given as a String])
			 * does not work in a JAR file since only getResourceAsStream works there (which on the other
			 * hand only returns an InputStream, not a file resource). Thus use setSSLContext()
			 */
			
			SecurityUtils.setSSLContext(
					GlobalValues.SECC_KEYSTORE_FILEPATH.toString(), 
					GlobalValues.SECC_TRUSTSTORE_FILEPATH.toString(),
					GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());

			SSLServerSocketFactory tlsServerSocketFactory =
				(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			setTlsServerSocket((SSLServerSocket) tlsServerSocketFactory
					.createServerSocket(getServerPort(), 50, getServerAddress()));
			
			/*
			 * The EVCC shall support at least one cipher suite as listed below according to 
			 * the standard. An implementer may decide to choose only one of them:
			 * - TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256
			 * - TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256
			 */
			String[] enabledCipherSuites = {
					"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 
					"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256" // this cipher suite should be avoided, ECDH does not support perfect forward secrecy
			};
			getTlsServerSocket().setEnabledCipherSuites(enabledCipherSuites);
			
			// Set the supported TLS protocol
			String[] enabledProtocols = {"TLSv1.2"};
			getTlsServerSocket().setEnabledProtocols(enabledProtocols);
			
			getLogger().info("TLS server initialized at link-local address " + 
			  		  		  getTlsServerSocket().getInetAddress().getHostAddress() +
			  		  		  " and port " + getTlsServerSocket().getLocalPort());
		} catch (IOException e) {
			getLogger().fatal("IOException while trying to initialize TLS server", e);
			return false;
		} catch (NullPointerException e) {
			getLogger().fatal("NullPointerException while trying to set keystores, resource path to keystore/truststore might be incorrect");
			return false;
		}
		
		return true;
	}
	
	
	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				getLogger().info("Waiting for new TLS client connection ...");
				setTlsClientSocket((SSLSocket) getTlsServerSocket().accept());
				
				getLogger().info("TLS client connection with IP address " + 
						  getTlsClientSocket().getInetAddress().getHostAddress() + " and port " +
						  getTlsClientSocket().getPort());
				
				ConnectionHandler connectionHandler = new ConnectionHandler(tlsClientSocket);
				
				// Notify the V2GCommunicationSessionHandlerSECC about a newly connected TLS client socket
				setChanged();
				notifyObservers(connectionHandler);
			}
		} catch (IOException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while running TLSServer");
		} finally {
			stop();
		}
	}

	@Override
	public void stop() {
        try {
        		getLogger().debug("TLS server will be stopped now");
			getTlsServerSocket().close();
        } catch (SocketException e) {
        		getLogger().debug("TLSServerSocket was still active and has been closed now", e);
		} catch (IOException e) {
			getLogger().error("Error occurred while trying to close TLSServerSocket (IOException)", e);
		}
        
        getLogger().debug("TLS server stopped");
    }

	public SSLSocket getTlsClientSocket() {
		return tlsClientSocket;
	}

	public void setTlsClientSocket(SSLSocket tlsClientSocket) {
		this.tlsClientSocket = tlsClientSocket;
	}

	public SSLServerSocket getTlsServerSocket() {
		return tlsServerSocket;
	}

	public void setTlsServerSocket(SSLServerSocket tlsServerSocket) {
		this.tlsServerSocket = tlsServerSocket;
	}

}
