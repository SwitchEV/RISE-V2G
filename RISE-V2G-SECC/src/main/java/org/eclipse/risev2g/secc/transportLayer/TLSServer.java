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
package org.eclipse.risev2g.secc.transportLayer;

import java.io.IOException;
import java.net.SocketException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.utils.SecurityUtils;

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
			 * hand only returns an InputStream, not a file resource). Thus use setSSLFactories()
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
			String[] enabledCipherSuites = {"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256"};
			getTlsServerSocket().setEnabledCipherSuites(enabledCipherSuites);
			
			// Set the supported TLS protocol
			String[] enabledProtocols = {"TLSv1.2"};
			getTlsServerSocket().setEnabledProtocols(enabledProtocols);
			
			getLogger().debug("TLS server initialized at link-local address " + 
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
			while (!Thread.interrupted()) {
				getLogger().debug("Waiting for new TLS client connection ...");
				setTlsClientSocket((SSLSocket) getTlsServerSocket().accept());
				
				getLogger().debug("TLS client connection with IP address " + 
						  getTlsClientSocket().getInetAddress().getHostAddress() + " and port " +
						  getTlsClientSocket().getPort());
				
				ConnectionHandler connectionHandler = new ConnectionHandler(tlsClientSocket);
				
				// Notify the V2GCommunicationSessionHandlerSECC about a newly connected TCP client Socket
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
        
        getLogger().debug("TCP server stopped");
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
