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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;
import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.exiCodec.ExiCodec;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CanonicalizationMethodType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DiffieHellmanPublickeyType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DigestMethodType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EMAIDType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ListOfRootCertificateIDsType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ReferenceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureMethodType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignedInfoType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SubCertificatesType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.TransformType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.TransformsType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.X509IssuerSerialType;

import java.util.Base64;

public final class SecurityUtils {
	/*
	 * Add VM (virtual machine) argument "-Djavax.net.debug=ssl" if you want more detailed debugging output
	 */
	
	static Logger logger = LogManager.getLogger(SecurityUtils.class.getSimpleName());
	static ExiCodec exiCodec;
	static boolean showSignatureVerificationLog = ((boolean) MiscUtils.getPropertyValue("SignatureVerificationLog")); 
	
	public static enum ContractCertificateStatus {
		UPDATE_NEEDED,
		INSTALLATION_NEEDED,
		OK,
		UNKNOWN // is used as default for communication session context
	}
	
	public static Logger getLogger() {
		return logger;
	}
	
	
	/**
	 * Returns the standard JKS keystore which holds the respective credentials (private key and 
	 * certificate chain) for the EVCC or SECC (whoever calls this method).
	 * 
	 * @param keyStorePath The relative path and file name of the keystore 
	 * @param keyStorePassword The password which protects the keystore
	 * @return The respective keystore
	 */
	public static KeyStore getKeyStore(String keyStorePath, String keyStorePassword) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(keyStorePath);
			return getKeyStore(fis, keyStorePassword, "jks");
		} catch (FileNotFoundException e) {
			getLogger().error("FileNotFoundException occurred while trying to access keystore at location '" +
							  keyStorePath + "'");
			return null;
		}
	}
	
	/**
	 * Returns the standard JKS truststore which holds the respective trusted certificates for the EVCC 
	 * or SECC (whoever calls this method).
	 * 
	 * @param trustStorePath The relative path and file name of the truststore
	 * @param trustStorePassword The password which protects the truststore
	 * @return The respective truststore
	 */
	public static KeyStore getTrustStore(String trustStorePath, String trustStorePassword) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(trustStorePath);
			return getKeyStore(fis, trustStorePassword, "jks");
		} catch (FileNotFoundException e) {
			getLogger().error("FileNotFoundException occurred while trying to access keystore at location '" +
							  trustStorePath + "'");
			return null;
		}
	}
	
	
	/**
	 * Returns a PKCS#12 container which holds the respective credentials (private key and certificate chain)
	 * 
	 * @param pkcs12Path The relative path and file name of the PKCS#12 container
	 * @param password The password which protects the PKCS#12 container
	 * @return The respective keystore
	 */
	public static KeyStore getPKCS12KeyStore(String pkcs12Path, String password) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(pkcs12Path);
			return getKeyStore(fis, password, "pkcs12");
		} catch (FileNotFoundException e) {
			getLogger().error("FileNotFoundException occurred while trying to access PKCS#12 container at " +
							  "location '" + pkcs12Path + "'");
			return null;
		}
	}


	/**
	 * Returns a standard keystore which holds the respective credentials (private key and certificate chain).
	 * 
	 * @param keyStoreIS The input stream of the keystore
	 * @param keyStorePassword The password which protects the keystore
	 * @param keyStoreType The type of the keystore, either "jks" or "pkcs12"
	 * @return The respective keystore
	 */
	private static KeyStore getKeyStore(InputStream keyStoreIS, String keyStorePassword, String keyStoreType) {
		KeyStore keyStore = null;
		
		try {
			keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(keyStoreIS, keyStorePassword.toCharArray());
			keyStoreIS.close();
			return keyStore;
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | 
				IOException | NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to load keystore", e);
		} 
		
		return null;
	}
	
	
	/**
	 * Checks whether the given certificate is currently valid. 
	 * 
	 * @param certificate The X509Certificiate to be checked for validity
	 * @return True, if the current date lies within the notBefore and notAfter attribute of the 
	 * 		   certificate, false otherwise
	 */
	public static boolean isCertificateValid(X509Certificate certificate) {
		try {
			certificate.checkValidity();		
			return true;
		} catch (CertificateExpiredException e) {
			X500Principal subject = certificate.getSubjectX500Principal();
			
			getLogger().warn("Certificate with distinguished name '" + subject.getName().toString() + 
							 "' already expired (not after " + certificate.getNotAfter().toString() + ")");
		} catch (CertificateNotYetValidException e) {
			X500Principal subject = certificate.getSubjectX500Principal();
			getLogger().warn("Certificate with distinguished name '" + subject.getName().toString() + 
							 "' not yet valid (not before " + certificate.getNotBefore().toString() + ")");
		} 
		
		return false;
	}
	
	
	/**
	 * 
	 * [V2G2-925] states:
	 * A leaf certificate shall be treated as invalid, if the trust anchor at the end of the chain does not
	 * match the specific root certificate required for a certain use, or if the required Domain
	 * Component value is not present.
	 * 
	 * Domain Component restrictions:
	 * - SECC certificate: "CPO" (verification by EVCC) 
	 * - provisioning certificate (signer certificate of a contract certificate: "CPS" (verification by EVCC)
	 * - OEM Provisioning Certificate: "OEM" (verification by provisioning service (not EVCC or SECC))
	 * 
	 * @param certificate The X509Certificiate to be checked for validity
	 * @param domainComponent The domain component to be checked for in the distinguished name of the certificate
	 * @return True, if the current date lies within the notBefore and notAfter attribute of the 
	 * 		   certificate and the given domain component is present in the distinguished name, false otherwise
	 */
	public static boolean isCertificateValid(X509Certificate certificate, String domainComponent) {
		if (isCertificateValid(certificate)) {
			String dn = certificate.getSubjectX500Principal().getName();
			LdapName ln;
			
			try {
				ln = new LdapName(dn);
				
				for (Rdn rdn : ln.getRdns()) {
				    if (rdn.getType().equalsIgnoreCase("DC") && rdn.getValue().equals(domainComponent)) {
				        return true;
				    }
				}
			} catch (InvalidNameException e) {
				getLogger().warn("InvalidNameException occurred while trying to check domain component of certificate", e);
			}

			return false;
		} else return false;
	}
	
	
	/**
	 * Checks how many days a given certificate is still valid. 
	 * If the certificate is not valid any more, a negative number will be returned according to the number
	 * of days the certificate is already expired.
	 * 
	 * @param certificate The X509Certificiate to be checked for validity period
	 * @return The number of days the given certificate is still valid, a negative number if already expired.
	 */
	public static short getValidityPeriod(X509Certificate certificate) {
		Date today = Calendar.getInstance().getTime();
		Date certificateExpirationDate = certificate.getNotAfter();
		long diff = certificateExpirationDate.getTime() - today.getTime();
		
		return (short) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	

	/**
	 * Checks whether each certificate in the given certificate chain is currently valid. 

	 * @param certChain The certificate chain to iterate over to check for validity
	 * @return True, if the current date lies within the notBefore and notAfter attribute of each 
	 * 		   certificate contained in the provided certificate chain, false otherwise
	 */
	public static boolean isCertificateChainValid(CertificateChainType certChain) {
		if (certChain == null) {
			getLogger().error("Certificate chain is NULL");
			return false;
		}
		
		if (!isCertificateValid(getCertificate(certChain.getCertificate()))) 
			return false;
		
		SubCertificatesType subCertificates = certChain.getSubCertificates();
		for (byte[] cert : subCertificates.getCertificate()) {
			if (!isCertificateValid(getCertificate(cert))) return false;
		}
		
		return true;
	}
	
	/**
	 * Checks whether each certificate in the given certificate chain is currently valid and if a given
	 * domain component (DC) in the distinguished name is set.
	 *
	 * @param certChain The certificate chain to iterate over to check for validity
	 * @param domainComponent The domain component 
	 * @return True, if the domain component is correctly set and if the current date lies within the notBefore 
	 * 		   and notAfter attribute of each certificate contained in the provided certificate chain, 
	 * 		   false otherwise
	 */
	public static boolean isCertificateChainValid(CertificateChainType certChain, String domainComponent) {
		if (isCertificateChainValid(certChain)) {
			if (isCertificateValid(getCertificate(certChain.getCertificate()), domainComponent)) return true;
			else return false;
		} else return false;
	}
	
	
	/**
	 * Verifies that the given certificate was signed using the private key that corresponds to the 
	 * public key of the provided certificate.
	 * 
	 * @param certificate The X509Certificate which is to be checked
	 * @param issuingCertificate The X.509 certificate which holds the public key corresponding to the private 
	 * 		  key with which the given certificate should have been signed
	 * @return True, if the verification was successful, false otherwise
	 */
	public static boolean isCertificateVerified(X509Certificate certificate, X509Certificate issuingCertificate) {
		X500Principal subject = certificate.getSubjectX500Principal();
		X500Principal expectedIssuerSubject = certificate.getIssuerX500Principal();
		X500Principal issuerSubject = issuingCertificate.getSubjectX500Principal();
		PublicKey publicKeyForSignature = issuingCertificate.getPublicKey();
		
		try {
			certificate.verify(publicKeyForSignature);
			return true;
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | 
				 NoSuchProviderException | SignatureException e) {
			getLogger().warn("\n"
						   + "\tSignature verification of certificate having distinguished name \n" 
						   + "\t'" + subject.getName() + "'\n" 
						   + "\twith certificate having distinguished name (the issuer) \n" 
						   + "\t'" + issuerSubject.getName() + "'\n"
						   + "\tfailed. Expected issuer has distinguished name \n"
						   + "\t'" + expectedIssuerSubject.getName() + "' (" + e.getClass().getSimpleName() + ")", e);
		} 
		
		return false;
	}
	
	
	/**
	 * Verifies for each certificate in the given certificate chain that it was signed using the private key 
	 * that corresponds to the public key of a certificate contained in the certificate chain or the truststore.
	 * 
	 * @param trustStoreFileName The relative path and file name of the truststore 
	 * @param certChain The certificate chain holding the leaf certificate and zero or more intermediate 
	 * 		  certificates (sub CAs) 
	 * @return True, if the verification was successful, false otherwise
	 */
	public static boolean isCertificateChainVerified(String trustStoreFileName, CertificateChainType certChain) {
		X509Certificate issuingCertificate = null; 
		
		if (certChain != null) {
			X509Certificate leafCertificate = getCertificate(certChain.getCertificate());
			if (leafCertificate != null) {
				SubCertificatesType subCertificates = certChain.getSubCertificates();
				if (subCertificates != null) {
					// Sub certificates must be in the right order (leaf -> SubCA2 -> SubCA1 -> RootCA)
					issuingCertificate = getCertificate(subCertificates.getCertificate().get(0));
					if (!isCertificateVerified(leafCertificate, issuingCertificate)) return false;
					
					for (int i=0; i < subCertificates.getCertificate().size(); i++) {
						if ((i+1) < subCertificates.getCertificate().size()) {
							issuingCertificate = getCertificate(subCertificates.getCertificate().get(i+1));
							if (!isCertificateVerified(getCertificate(subCertificates.getCertificate().get(i)), issuingCertificate)) 
								return false;
						} else {
							if (isCertificateTrusted(trustStoreFileName, getCertificate(subCertificates.getCertificate().get(i)))) return true;
							else return false;
						}
					}
				} else {
					if (!isCertificateTrusted(trustStoreFileName, leafCertificate)) return false;
				}
			} else {
				getLogger().error("No leaf certificate available in provided certificate chain, " + 
								  "therefore no verification possible");
				return false;
			}
		} else {
			getLogger().error("Provided certificate chain is null, could therefore not be verified");
			return false;
		}
		
		return false;
	}
	
	
	/**
	 * Iterates over the certificates stored in the truststore to check if one of the respective public
	 * keys of the certificates is the corresponding key to the private key with which the provided 
	 * certificate has been signed.
	 * 
	 * @param trustStoreFilename The relative path and file name of the truststore
	 * @param certificate The certificate whose signature needs to be signed
	 * @return True, if the provided certificate has been signed by one of the certificates in the 
	 * 		   truststore, false otherwise
	 */
	public static boolean isCertificateTrusted(String trustStoreFilename, X509Certificate certificate) {
		/*
		 * Use one of the root certificates in the truststore to verify the signature of the
		 * last certificate in the chain
		 */
		KeyStore trustStore = SecurityUtils.getTrustStore(trustStoreFilename, GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		X500Principal expectedIssuer = certificate.getIssuerX500Principal();
		
		try {
			Enumeration<String> aliases = trustStore.aliases();
			while (aliases.hasMoreElements()) {
				X509Certificate rootCA = (X509Certificate) trustStore.getCertificate(aliases.nextElement());
				if (rootCA.getSubjectX500Principal().getName().equals(expectedIssuer.getName()) &&
					isCertificateVerified(certificate, rootCA)) return true;
			}
		} catch (KeyStoreException | NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to verify trust " +
							  " status of certificate with distinguished name '" + 
							  certificate.getSubjectX500Principal().getName() + "' with truststore at " +
							  "location '" + trustStoreFilename + "'", e);
		}
		
		return false;
	}
	
	
	/**
	 * Returns the leaf certificate from a given certificate chain.
	 * 
	 * @param certChain The certificate chain given as an array of Certificate instances
	 * @return The leaf certificate (begin not a CA)
	 */
	public static X509Certificate getLeafCertificate(Certificate[] certChain) {
		for (Certificate cert : certChain) {
			X509Certificate x509Cert = (X509Certificate) cert;
			// Check whether the pathLen constraint is set which indicates if this certificate is a CA
			if (x509Cert.getBasicConstraints() == -1) return x509Cert;
		}
		
		getLogger().warn("No leaf certificate found in given certificate chain");
		return null;
	}
	
	
	/**
	 * Returns the intermediate certificates (sub CAs) from a given certificate chain.
	 * 
	 * @param certChain The certificate chain given as an array of Certificate instances
	 * @return The sub certificates given as a list of byte arrays contained in a SubCertiticatesType instance
	 */
	public static SubCertificatesType getSubCertificates(Certificate[] certChain) {
		SubCertificatesType subCertificates = new SubCertificatesType();
		
		for (Certificate cert : certChain) {
			X509Certificate x509Cert = (X509Certificate) cert;
			// Check whether the pathLen constraint is set which indicates if this certificate is a CA
			if (x509Cert.getBasicConstraints() != -1)
				try {
					subCertificates.getCertificate().add(x509Cert.getEncoded());
				} catch (CertificateEncodingException e) {
					X500Principal subject = x509Cert.getIssuerX500Principal();
					getLogger().error("A CertificateEncodingException occurred while trying to get certificate " +
									  "with distinguished name '" + subject.getName().toString() + "'", e);
				}
		}
		
		if (subCertificates.getCertificate().size() == 0) {
			getLogger().warn("No intermediate CAs found in given certificate array");
		}
		
		return subCertificates;
	}
	
	
	/**
	 * Returns the list of X509IssuerSerialType instances of the root CAs contained in the truststore.
	 * 
	 * @param trustStoreFileName The relative path and file name of the truststore
	 * @param trustStorePassword The password which protects the truststore
	 * @return The list of X509IssuerSerialType instances of the root CAs
	 */
	public static ListOfRootCertificateIDsType getListOfRootCertificateIDs(
			String trustStoreFileName,
			String trustStorePassword) {
		KeyStore evccTrustStore = getTrustStore(trustStoreFileName, trustStorePassword);
		ListOfRootCertificateIDsType rootCertificateIDs = new ListOfRootCertificateIDsType();
		
		X509Certificate cert = null;
		try {
			Enumeration<String> aliases = evccTrustStore.aliases();
			while (aliases.hasMoreElements()) {
				cert = (X509Certificate) evccTrustStore.getCertificate(aliases.nextElement());
				X509IssuerSerialType serialType = new X509IssuerSerialType();
				serialType.setX509IssuerName(cert.getIssuerX500Principal().getName());
				serialType.setX509SerialNumber(cert.getSerialNumber());
				rootCertificateIDs.getRootCertificateID().add(serialType);
			}
		} catch (KeyStoreException | NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to get list of " +
							  "root certificate IDs from truststore at location '" + trustStoreFileName + "'", e);
		}
		
		return rootCertificateIDs;
	}
	
	
	/**
	 * Returns an instance of a X.509 certificate created from its raw byte array
	 * 
	 * @param certificate The byte array representing a X.509 certificate
	 * @return The X.509 certificate
	 */
	public static X509Certificate getCertificate(byte[] certificate) {
		X509Certificate cert = null;
		
		try {
			InputStream in = new ByteArrayInputStream(certificate);
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) certFactory.generateCertificate(in);
		} catch (CertificateException e) {
			getLogger().error("CertificateException occurred when trying to create X.509 certificate from byte array", e);
		}
		
		return cert;
	}
	
	
	/**
	 * Returns the mobility operator Sub-CA 2 certificate (MOSubCA2 certificate) which can verify the signature of the  
	 * contract certificate from the given keystore. The public key of the MOSub2Certificate is then used to verify 
	 * the signature of sales tariffs.
	 * 
	 * @param keyStoreFileName The relative path and file name of the keystore
	 * @return The X.509 mobility operator Sub-CA2 certificate (a certificate from a Sub-CA)
	 */
	public static X509Certificate getMOSubCA2Certificate(String keyStoreFileName) {
		KeyStore keystore = getKeyStore(keyStoreFileName, GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		X509Certificate moSubCA2Certificate = null;
		
		try {
			Certificate[] certChain = keystore.getCertificateChain(GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString());
			X509Certificate contractCertificate = getLeafCertificate(certChain);
			SubCertificatesType subCertificates = getSubCertificates(certChain); 
			
			for (byte[] certificate : subCertificates.getCertificate()) {
				X509Certificate x509Cert = getCertificate(certificate);
				if (contractCertificate.getIssuerX500Principal().getName().equals(
					x509Cert.getSubjectX500Principal().getName())) {
					moSubCA2Certificate = x509Cert;
					break;
				}
			}
		} catch (KeyStoreException e) {
			getLogger().error("KeyStoreException occurred while trying to get MOSubCA2 certificate");
		}
		
		return moSubCA2Certificate;
	}

	
	/**
	 * Returns the ECPublicKey instance from its encoded raw bytes. 
	 * The first byte has the fixed value 0x04 indicating the uncompressed form.
	 * Therefore, the byte array must be of form: [0x04, x coord of point (32 bytes), y coord of point (32 bytes)]
	 * 
	 * @param publicKeyBytes The byte array representing the encoded raw bytes of the public key
	 * @return The ECPublicKey instance
	 */
	public static ECPublicKey getPublicKey(byte[] publicKeyBytes) {
		// First we separate x and y of coordinates into separate variables
	    byte[] x = new byte[32];
	    byte[] y = new byte[32];
	    System.arraycopy(publicKeyBytes, 1, x, 0, 32);
	    System.arraycopy(publicKeyBytes, 33, y, 0, 32);
	    
	    try {
			KeyFactory kf = KeyFactory.getInstance("EC");
			
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
			parameters.init(new ECGenParameterSpec("secp256r1"));
			ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
			
			ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(new ECPoint(new BigInteger(x), new BigInteger(y)), ecParameterSpec);
			ECPublicKey ecPublicKey = (ECPublicKey) kf.generatePublic(ecPublicKeySpec);
			return ecPublicKey;
	    } catch (NoSuchAlgorithmException | InvalidParameterSpecException | InvalidKeySpecException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred when trying to get public key from raw bytes", e);
	        return null;
		}
	}
	
	
	/**
	 * Returns the public key part of an elliptic curve Diffie-Hellman keypair
	 * 
	 * @param ecdhKeyPair The elliptic curve Diffie-Hellman keypair
	 * @return The respective public key
	 */
	public static DiffieHellmanPublickeyType getDHPublicKey(KeyPair ecdhKeyPair) {
		DiffieHellmanPublickeyType dhPublicKey = new DiffieHellmanPublickeyType();
		/*
		 * Experience from the test symposium in San Diego (April 2016):
		 * The Id element of the signature is not restricted in size by the standard itself. But on embedded 
		 * systems, the memory is very limited which is why we should not use long IDs for the signature reference
		 * element. A good size would be 3 characters max (like the example in the ISO 15118-2 annex J)
		 */
		dhPublicKey.setId("id1"); // dhPublicKey
		dhPublicKey.setValue(getUncompressedSubjectPublicKey((ECPublicKey) ecdhKeyPair.getPublic()));
		
		return dhPublicKey;
	}
	
	
	/**
	 * Returns the ECPrivateKey instance from its raw bytes. Note that you must provide the "s" value of the 
	 * private key, not e.g. the byte array from reading a PKCS#8 key file.
	 * 
	 * @param privateKeyBytes The byte array (the "s" value) of the private key
	 * @return The ECPrivateKey instance
	 */
	public static ECPrivateKey getPrivateKey(byte[] privateKeyBytes) {
		try {
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
			parameters.init(new ECGenParameterSpec("secp256r1"));
			
			ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
			ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(new BigInteger(privateKeyBytes), ecParameterSpec);
			
			ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(ecPrivateKeySpec);

			return privateKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidParameterSpecException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred when trying to get private key from raw bytes", e);
			return null;
		}
	}
	
	
	/**
	 * Searches the given keystore for the private key. It is assumed that the given keystore holds
	 * only one private key entry whose alias is not known before, which is the case during certificate
	 * installation when the SECC uses a PKCS#12 container encapsulating the 
	 * contract certificate, its private key and an optional chain of intermediate CAs.
	 * 
	 * @param keyStore The PKCS#12 keystore 
	 * @return The private key contained in the given keystore as an ECPrivateKey
	 */
	public static ECPrivateKey getPrivateKey(KeyStore keyStore) {
		ECPrivateKey privateKey = null;
		
		try {
			Enumeration<String> aliases = keyStore.aliases();
			// Only one certificate chain (and therefore alias) should be available
			while (aliases.hasMoreElements()) {
				privateKey = (ECPrivateKey) keyStore.getKey(
						aliases.nextElement(), 
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString().toCharArray());
			}
		} catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | 
				 NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to get private " +
							  "key from keystore", e);
		}
		
		return privateKey;
	}
	
	
	/**
	 * Reads the private key from an encrypted PKCS#8 file and returns it as an ECPrivateKey instance.
	 * 
	 * @param A PKCS#8 (.key) file containing the private key with value "s"
	 * @return The private key as an ECPrivateKey instance
	 */
	public static ECPrivateKey getPrivateKey(String keyFilePath) {
		Path fileLocation = Paths.get(keyFilePath);
		byte[] pkcs8ByteArray;
		
		try {
			pkcs8ByteArray = Files.readAllBytes(fileLocation);
			
			// The DER encoded private key is encrypted in PKCS#8. So we need to decrypt it first
			PBEKeySpec pbeKeySpec = new PBEKeySpec(GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString().toCharArray());
		    EncryptedPrivateKeyInfo encryptedPrivKeyInfo = new EncryptedPrivateKeyInfo(pkcs8ByteArray);
		    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedPrivKeyInfo.getAlgName());
		    Key secret = secretKeyFactory.generateSecret(pbeKeySpec);
		    PKCS8EncodedKeySpec pkcs8PrivKeySpec = encryptedPrivKeyInfo.getKeySpec(secret);
			
			ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(pkcs8PrivKeySpec);

			return privateKey;
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to access private key at " +
					  "location '" + keyFilePath + "'");
			return null;
		} 
	}
	
	
	/**
	 * Searches the given keystore for the private key which corresponds to the provided alias.
	 * Example: In case of the EVCC and during certificate installation, the private key of the
	 * OEM provisioning certificate is needed. During certificate update, the private key of the 
	 * existing contract certificate is needed.
	 * 
	 * @param keyStore The keystore of EVCC or SECC
	 * @param alias The alias of a specific private key entry
	 * @return The private key corresponding to the respective alias in the given keystore
	 */
	public static ECPrivateKey getPrivateKey(KeyStore keyStore, String alias) {
		ECPrivateKey privateKey = null;
		
		try {
			privateKey = (ECPrivateKey) keyStore.getKey(
						alias, 
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString().toCharArray());
		} catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
			getLogger().error("The private key from keystore with alias '" + alias + 
							  "' could not be retrieved (" + e.getClass().getSimpleName() + ")", e);
		}
		
		return privateKey;
	}

	
	/**
	 * Returns the SecretKey instance from its raw bytes
	 * 
	 * @param key The byte array representing the symmetric SecretKey instance
	 * @return The SecretKey instance
	 */
	public static SecretKey getSecretKey(byte[] key) {
		SecretKey secretKey = new SecretKeySpec(key, 0, key.length, "DiffieHellman");
		
		return secretKey;
	}
	
	
	/**
	 * Returns the certificate chain from a PKCS#12 container holding credentials such as private key,
	 * leaf certificate and zero or more intermediate certificates.
	 * 
	 * @param pkcs12Resource The PKCS#12 container
	 * @return The certificate chain
	 */
	public static CertificateChainType getCertificateChain(String pkcs12Resource) {
		CertificateChainType certChain = new CertificateChainType();
		
		/*
		 * For testing purposes, the respective PKCS12 container file has already been put in the 
		 * resources folder. However, when implementing a real interface to a secondary actor's backend, 
		 * the retrieval of a certificate must be done via some other online mechanism.
		 */
		KeyStore contractCertificateKeystore = getPKCS12KeyStore(pkcs12Resource, GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		
		if (contractCertificateKeystore == null) {
			getLogger().error("Unable to access certificate chain because no PKCS#12 container found at " +
							  "location '" + pkcs12Resource + "'");
			return null;
		}
		
		try {
			Enumeration<String> aliases = contractCertificateKeystore.aliases();
			Certificate[] tempCertChain = null;
			// Only one certificate chain (and therefore alias) should be available
			while (aliases.hasMoreElements()) {
				tempCertChain = contractCertificateKeystore.getCertificateChain(aliases.nextElement());
				certChain.setCertificate(getLeafCertificate(tempCertChain).getEncoded());
				certChain.setSubCertificates(getSubCertificates(tempCertChain));
			}
		} catch (KeyStoreException | CertificateEncodingException | NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while  trying to get " +
							  "certificate chain from resource '" + pkcs12Resource + "'", e);
		}
		
		return certChain;
	}
	
	
	/**
	 * Returns the SignedInfo element of the V2GMessage header, based on the provided HashMap which holds
	 * the reference IDs (URIs) and the corresponding SHA-256 digests.
	 * 
	 * @param xmlSignatureRefElements A HashMap of Strings (reflecting the reference IDs) and digest values
	 * @return The SignedInfoType instance
	 */
	public static SignedInfoType getSignedInfo(HashMap<String, byte[]> xmlSignatureRefElements) {
		/*
		 * According to requirement [V2G2-771] in ISO/IEC 15118-2 the following message elements of the 
		 * XML signature framework shall not be used:
		 * - Id (attribute in SignedInfo)
 		 * - ##any in SignedInfo – CanonicalizationMethod
 		 * - HMACOutputLength in SignedInfo – SignatureMethod
 		 * - ##other in SignedInfo – SignatureMethod
 		 * - Type (attribute in SignedInfo-Reference)
 		 * - ##other in SignedInfo – Reference – Transforms – Transform
 		 * - XPath in SignedInfo – Reference – Transforms – Transform
 		 * - ##other in SignedInfo – Reference – DigestMethod
 		 * - Id (attribute in SignatureValue)
 		 * - Object (in Signature)
 		 * - KeyInfo
		 */
		DigestMethodType digestMethod = new DigestMethodType();
		digestMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
		
		TransformType transform = new TransformType();
		transform.setAlgorithm("http://www.w3.org/TR/canonical-exi/");
		TransformsType transforms = new TransformsType();
		transforms.getTransform().add(transform);
		
		List<ReferenceType> references = new ArrayList<ReferenceType>();
		xmlSignatureRefElements.forEach( (k,v) -> {
			ReferenceType reference = new ReferenceType();
			reference.setDigestMethod(digestMethod);
			reference.setDigestValue(v);
			reference.setTransforms(transforms);
			reference.setURI("#" + k);
			
			references.add(reference);
		});
		
		CanonicalizationMethodType canonicalizationMethod = new CanonicalizationMethodType();
		canonicalizationMethod.setAlgorithm("http://www.w3.org/TR/canonical-exi/");
		
		SignatureMethodType signatureMethod = new SignatureMethodType(); 
		signatureMethod.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256");
		
		SignedInfoType signedInfo = new SignedInfoType();
		signedInfo.setCanonicalizationMethod(canonicalizationMethod);
		signedInfo.setSignatureMethod(signatureMethod);
		signedInfo.getReference().addAll(references);
		
		return signedInfo;
	}
	
	
	/**
	 * Saves the newly received contract certificate chain, provided by CertificateInstallationRes or 
	 * CertificateUpdateRes.
	 * 
	 * @param keyStorePassword The password which protects the EVCC keystore
	 * @param contractCertChain The certificate chain belonging to the contract certificate
	 * @param contractCertPrivateKey The private key corresponding to the public key of the leaf certificate 
	 * 								 stored in the certificate chain
	 * @return True, if the contract certificate chain and private key could be saved, false otherwise
	 */
	public static boolean saveContractCertificateChain(
			String keyStorePassword, 
			CertificateChainType contractCertChain,
			ECPrivateKey contractCertPrivateKey) {
		KeyStore keyStore = getKeyStore(GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(), keyStorePassword);

		try {
			if (isPrivateKeyValid(contractCertPrivateKey, contractCertChain)) {
				keyStore.setKeyEntry(
						GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString(), 
						contractCertPrivateKey, 
						keyStorePassword.toCharArray(), 
						getCertificateChain(contractCertChain)); 
				
				// Save the keystore persistently
				FileOutputStream fos = new FileOutputStream("evccKeystore.jks");
				keyStore.store(fos, GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString().toCharArray());
				fos.close();
				
				X509Certificate contractCert = getCertificate(contractCertChain.getCertificate());
				
				getLogger().info("Contract certificate with distinguished name '" + 
								 contractCert.getSubjectX500Principal().getName() + "' saved. " + 
								 "Valid until " + contractCert.getNotAfter()
								 ); 
			} else {
				getLogger().error("Private key for contract certificate is not valid");
				return false;
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to save contract " +
							  "certificate chain", e);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Checks if the private key is a valid key (according to requirement [V2G2-823]) for the received contract 
	 * certificate before saving it to the keystore.
	 * @param privateKey The private key corresponding to the contract certificate
	 * @param contractCertChain The received contract certificate chain 
	 * @return True, if the private key is a valid key, false otherwise.
	 */
	private static boolean isPrivateKeyValid(ECPrivateKey privateKey, CertificateChainType contractCertChain) {
		AlgorithmParameters parameters;
		
		try {
			parameters = AlgorithmParameters.getInstance("EC");
			parameters.init(new ECGenParameterSpec("secp256r1"));
			
			ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
			
			// Now we need to check if the private key is correct (see requirement [V2G2-823]) 
			BigInteger order = ecParameterSpec.getOrder();
			ECPoint basePoint = ecParameterSpec.getGenerator();
			BigInteger privateKeyValue = privateKey.getS();
			X509Certificate contractCert = getCertificate(contractCertChain.getCertificate());
			ECPublicKey publicKey = (ECPublicKey) contractCert.getPublicKey();
			
			// 1. check
			if (privateKeyValue.compareTo(order) != -1) {
				getLogger().error("Validation of private key failed: its value is not strictly smaller than the "
								+ "order of the base point");
				return false;
			}
			
			// 2. check
			/*
			 * TODO: 
			 * No idea how to check for 
			 * "multiplication of the base point with this value must generate a key matching the public key of 
			 * the contract certificate"
			 * "this value" = value of private key
			 * -> some more expert knowledge on the arithmetic of elliptic curves is needed to tackle this!
			 */
			
		} catch (NoSuchAlgorithmException | InvalidParameterSpecException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred when trying to get private key from raw bytes", e);
			return false;
		}
		
		return true;
	}
	

	/**
	 * Gets the contract certificate from the EVCC keystore.
	 * 
	 * @return The contract certificate if present, null otherwise
	 */
	public static X509Certificate getContractCertificate() {
		X509Certificate contractCertificate = null;
		
		KeyStore evccKeyStore = getKeyStore(
				GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(), 
				GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString()
			);
		 
		try {
			contractCertificate = (X509Certificate) evccKeyStore.getCertificate(GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString());
		} catch (KeyStoreException e) {
			getLogger().error("KeyStoreException occurred while trying to get contract certificate from keystore", e);
		}
		
		return contractCertificate;
	}
	
	
	/**
	 * A convenience function which checks if a contract certificate installation is needed.
	 * Normally not needed because of function getContractCertificateStatus().
	 * 
	 * @return True, if no contract certificate is store or if the stored certificate is not valid, false otherwise
	 */
	public static boolean isContractCertificateInstallationNeeded() {
		X509Certificate contractCert = getContractCertificate();
		
		if (contractCert == null) {
			getLogger().info("No contract certificate stored");
			return true;
		} else if (contractCert != null && !isCertificateValid(contractCert)) {
			getLogger().info("Stored contract certificate with distinguished name '" + 
							 contractCert.getSubjectX500Principal().getName() + "' is not valid");
			return true;
		} else return false;
	}
	
	
	/**
	 * A convenience function which checks if a contract certificate update is needed.
	 * Normally not needed because of function getContractCertificateStatus().
	 * 
	 * @return True, if contract certificate is still valid but about to expire, false otherwise.
	 * 		   The expiration period is given in GlobalValues.CERTIFICATE_EXPIRES_SOON_PERIOD.
	 */
	public static boolean isContractCertificateUpdateNeeded() {
		X509Certificate contractCert = getContractCertificate();
		short validityOfContractCert = getValidityPeriod(contractCert);
		
		if (validityOfContractCert < 0) {
			getLogger().warn("Contract certificate with distinguished name '" + 
							 contractCert.getSubjectX500Principal().getName() + "' is not valid any more, expired " + 
							 Math.abs(validityOfContractCert) + " days ago");
			return false;
		} else if (validityOfContractCert <= GlobalValues.CERTIFICATE_EXPIRES_SOON_PERIOD.getShortValue()) {
			getLogger().info("Contract certificate with distinguished name '" + 
							 contractCert.getSubjectX500Principal().getName() + "' is about to expire in " + 
							 validityOfContractCert + " days");
			return true;
		} else return false;
	}
	
	
	/**
	 * Checks whether a contract certificate 
	 * - is stored
	 * - in case it is stored, if it is valid
	 * - in case it is valid, if it expires soon
	 * 
	 * This method is intended to reduce cryptographic computation overhead by checking both, if installation or
	 * update is needed, at the same time. When executing either method by itself (isContractCertificateUpdateNeeded() and
	 * isContractCertificateInstallationNeeded()), each time the certificate is read anew from the Java keystore
	 * holding the contract certificate. With this method the contract certificate is read just once from the keystore.
	 * 
	 * @return An enumeration value ContractCertificateStatus (either UPDATE_NEEDED, INSTALLATION_NEEDED, or OK)
	 */
	public static ContractCertificateStatus getContractCertificateStatus() {
		X509Certificate contractCert = getContractCertificate();
		
		if (contractCert == null) {
			getLogger().info("No contract certificate stored");
			return ContractCertificateStatus.INSTALLATION_NEEDED;
		} else if (contractCert != null && !isCertificateValid(contractCert)) {
			getLogger().info("Stored contract certificate with distinguished name '" + 
							 contractCert.getSubjectX500Principal().getName() + "' is not valid");
			return ContractCertificateStatus.INSTALLATION_NEEDED;
		} else {
			short validityOfContractCert = getValidityPeriod(contractCert);
			// Checking for a negative value of validityOfContractCert is not needed because the method
			// isCertificateValid() already checks for that
			if (validityOfContractCert <= GlobalValues.CERTIFICATE_EXPIRES_SOON_PERIOD.getShortValue()) {
				getLogger().info("Contract certificate with distinguished name '" + 
							 	 contractCert.getSubjectX500Principal().getName() + "' is about to expire in " + 
							 	 validityOfContractCert + " days");
				return ContractCertificateStatus.UPDATE_NEEDED;
			}
			return ContractCertificateStatus.OK;
		}
	}
	
	
	/**
	 * Returns a list of certificates from the given CertificateChainType with the leaf certificate 
	 * being the first element and potential subcertificates (intermediate CA certificatess) 
	 * in the array of certificates.
	 * 
	 * @param certChainType The CertificateChainType instance which holds a leaf certificate and
	 * 						possible intermediate certificates to verify the leaf certificate up to 
	 * 						some root certificate.
	 * @return An array of Certificates
	 */
	public static Certificate[] getCertificateChain(CertificateChainType certChainType) {
		List<byte[]> subCertificates = certChainType.getSubCertificates().getCertificate();
		Certificate[] certChain = new Certificate[subCertificates.size() + 1];
		
		certChain[0] = getCertificate(certChainType.getCertificate());
		
		for (int i = 0; i < subCertificates.size(); i++) {
			certChain[i+1] = getCertificate(subCertificates.get(i));
		}
		
		return certChain;
	}
	
	
	
	/**
	 * Generates an elliptic curve key pair using the named curve "secp256r1". 
	 * This function is mainly used for the ECDH procedure.
	 * 
	 * To use ECC (elliptic curve cryptography), SECC as well as EVCC must agree on all the elements 
	 * defining the elliptic curve, that is, the "domain parameters" of the scheme. Such domain 
	 * parameters are predefined by standardization bodies and are commonly known as "standard curves" 
	 * or "named curves"; a named curve can be referenced either by name or by the unique object 
	 * identifier defined in the standard documents. For the ISO/IEC 15118-2 document, the named curve 
	 * "secp256r1" (SECG notation, see http://www.secg.org/sec2-v2.pdf) is used.
	 * See [V2G2-818] in ISO/IEC 15118-2 for further information.
	 * 
	 * @return An elliptic curve key pair according to the named curve 'secp256r1'
	 */
	public static KeyPair getECKeyPair() {
		KeyPair keyPair = null;
		
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
			ECGenParameterSpec ecParameterSpec = new ECGenParameterSpec("secp256r1");
			keyPairGenerator.initialize(ecParameterSpec, new SecureRandom());
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to generate ECDH key pair", e);
		} 
		
		return keyPair;
	}
	
	
	/**
	 * The shared secret is computed using the domain parameters of the named curve "secp256r1", the private key 
	 * part of the ephemeral key pair, and the OEM provisioning certiicate’s public key (in case of certificate
	 * installation) or the contract certificate's public key (in case of certificate update).
	 * The shared secret is used as input to a key derivation function. 
	 * A key derivation function (KDF) is a deterministic algorithm to derive a key of a given
	 * size from some secret value. If two parties use the same shared secret value and the same KDF, 
	 * they should always derive exactly the same key.
	 * 
	 * @param privateKey The private key of an EC key pair generated from the named curve "secp256r1".
	 * 					
	 * 					The mobility operator (MO) provides his ephemeral private key when using this function for  
	 * 					generating the shared secret to encrypt the private key of the contract certificate.
	 * 					
	 * 					The EVCC provides the private key belonging to his OEM provisioning certificate's public key
	 *					when using this function for generating the shared secret to decrypt the encrypted private key 
	 *					of the newly to be installed contract certificate. 
	 * @param publicKey The public key of an EC key pair generated from the named curve "secp256r1"
	 * 
	 * 					The mobility operator (MO) provides the static OEM provisioning certificate's (in case of 
	 * 					CertificateInstallation) or old contract certificate's (in case of CertificateUpdate)
	 * 					public key when using this function for generating the shared secret to encrypt the private 
	 * 					key of the contract certificate.
	 * 					
	 * 					The EVCC provides the ephemeral public key of the MO (coming with the CertificateInstallationRes
	 * 					or CertificateUpdateRes, respectively) when using this function for generating the shared secret
	 * 					to decrypt the encrypted private key of the newly to be installed contract certificate. 
	 * @return The computed shared secret of the elliptic curve Diffie-Hellman key exchange protocol
	 */
	public static byte[] generateSharedSecret(ECPrivateKey privateKey, ECPublicKey publicKey) {
	    try {
	        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
	        keyAgreement.init(privateKey, new SecureRandom());
	        keyAgreement.doPhase(publicKey, true);

	        return keyAgreement.generateSecret();
	    } catch (InvalidKeyException | NoSuchAlgorithmException e) {
	        getLogger().error(e.getClass().getSimpleName() + " occurred while trying to generate the shared secret (ECDH)", e);
	        return null;
	    } 
	}
	
	
	/**
	 * The key derivation function (KDF). See [V2G2-818] in ISO/IEC 15118-2 for further information.
	 * 
	 * @param sharedSecret The shared secret derived from the ECDH algorithm
	 */
	public static SecretKey generateSessionKey(byte[] sharedSecret) {
	    MessageDigest md = null;
	    /*
	     * TODO it is unclear to me what should be the content of suppPubInfo or suppPrivInfo 
	     * according to page 49 of http://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-56Ar2.pdf
	     * Requirement [V2G2-818] is not clear about that.
	     */
	    byte[] suppPubInfo = null;
	    byte[] suppPrivInfo = null;
	    
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			getLogger().error("Message digest algorithm SHA-256 not supported");
			return null;
		}
        
        ByteArrayOutputStream baosOtherInfo = new ByteArrayOutputStream();
        try {
            baosOtherInfo.write(ByteUtils.toByteArrayFromHexString("01")); // algorithm ID
            baosOtherInfo.write(ByteUtils.toByteArrayFromHexString("55")); // partyUInfo
            baosOtherInfo.write(ByteUtils.toByteArrayFromHexString("56")); // partyVInfo
            if (suppPubInfo != null) baosOtherInfo.write(suppPubInfo); 
            if (suppPrivInfo != null) baosOtherInfo.write(suppPrivInfo);
        } catch (IOException e) {
            getLogger().error("IOException occurred while trying to write OtherInfo for session key generation", e);
        }
        
        byte[] otherInfo = baosOtherInfo.toByteArray();
        
        // A symmetric encryption key of exactly 128 bits shall be derived.
		byte[] sessionKeyAsByteArray = concatKDF(md, sharedSecret, 128, otherInfo);
		
		SecretKey sessionKey = null;
		try {
			sessionKey = new SecretKeySpec(sessionKeyAsByteArray, "AES");
		} catch (IllegalArgumentException e) {
			getLogger().error("IllegalArgumentException occurred while trying to generate session key", e);
		}
		
		return sessionKey;
    }
	
	
	/**
	 * Implementation of Concatenation Key Derivation Function 
	 * http://csrc.nist.gov/publications/nistpubs/800-56A/SP800-56A_Revision1_Mar08-2007.pdf
	 *
	 * Author: NimbusDS  Lai Xin Chu and Vladimir Dzhuvinov
	 * 
	 * See https://code.google.com/p/openinfocard/source/browse/trunk/testsrc/org/xmldap/crypto/ConcatKeyDerivationFunction.java?r=770
	 */
	private static byte[] concatKDF(MessageDigest md, byte[] z, int keyDataLen, byte[] otherInfo) {
		final long MAX_HASH_INPUTLEN = Long.MAX_VALUE;
		final long UNSIGNED_INT_MAX_VALUE = 4294967295L;
		keyDataLen = keyDataLen/8;
        byte[] key = new byte[keyDataLen];
        
        int hashLen = md.getDigestLength();
        int reps = keyDataLen / hashLen;
        
        if (reps > UNSIGNED_INT_MAX_VALUE) {
        	getLogger().error("Key derivation failed");
        	return null;
        }
        
        int counter = 1;
        byte[] counterInBytes = ByteUtils.intToFourBytes(counter);
        
        if ((counterInBytes.length + z.length + otherInfo.length) * 8 > MAX_HASH_INPUTLEN) {
        	getLogger().error("Key derivation failed");
        	return null;
        }
        
        for (int i = 0; i <= reps; i++) {
            md.reset();
            md.update(ByteUtils.intToFourBytes(i+1));
            md.update(z);
            md.update(otherInfo);
            
            byte[] hash = md.digest();
            if (i < reps) {
                System.arraycopy(hash, 0, key, hashLen * i, hashLen);
            } else {
                if (keyDataLen % hashLen == 0) {
                    System.arraycopy(hash, 0, key, hashLen * i, hashLen);
                } else {
                    System.arraycopy(hash, 0, key, hashLen * i, keyDataLen % hashLen);
                }
            }
        }
        
        return key;
    }

	
    private static ContractSignatureEncryptedPrivateKeyType getContractSignatureEncryptedPrivateKey(
    		SecretKey sessionKey, ECPrivateKey contractCertPrivateKey) {
    	ContractSignatureEncryptedPrivateKeyType encryptedPrivateKey = new ContractSignatureEncryptedPrivateKeyType();
    	encryptedPrivateKey.setValue(encryptPrivateKey(sessionKey, contractCertPrivateKey));
		
		return encryptedPrivateKey;
    }
    
    
    /**
     * Encrypts the private key of the contract certificate which is to be sent to the EVCC. First, the
     * shared secret based on the ECDH parameters is calculated, then the symmetric session key with which
     * the private key of the contract certificate is to be encrypted.
     * 
     * @param certificateECPublicKey The public key of either the OEM provisioning certificate (in case of 
     * 								 CertificateInstallation) or the to be updated contract certificate
     * 								 (in case of CertificateUpdate)
     * @param ecKeyPair The EC keypair
     * @param contractCertPrivateKey The private key of the contract certificate
     * @return The encrypted private key of the to be installed contract certificate
     */
	public static ContractSignatureEncryptedPrivateKeyType encryptContractCertPrivateKey(
			ECPublicKey certificateECPublicKey, 
			KeyPair ecKeyPair,
			ECPrivateKey contractCertPrivateKey) {
		// Generate the shared secret by using the public key of either OEMProvCert or ContractCert
		byte[] sharedSecret = generateSharedSecret((ECPrivateKey) ecKeyPair.getPrivate(), certificateECPublicKey);
		
		if (sharedSecret == null) {
			getLogger().error("Shared secret could not be generated");
			return null;
		}
		
		// The session key is generated using the computed shared secret
		SecretKey sessionKey = generateSessionKey(sharedSecret);
		
		// Finally, the private key of the contract certificate is encrypted using the session key
		ContractSignatureEncryptedPrivateKeyType encryptedContractCertPrivateKey = 
				getContractSignatureEncryptedPrivateKey(sessionKey, contractCertPrivateKey);
		
		return encryptedContractCertPrivateKey;
	}
	
    
	/**
	 * Applies the algorithm AES-CBC-128 according to NIST Special Publication 800-38A.
	 * The initialization vector IV shall be randomly generated before encryption and shall have a 
	 * length of 128 bit and never be reused.
	 * The IV shall be transmitted in the 16 most significant bytes of the 
	 * ContractSignatureEncryptedPrivateKey field.
	 * 
	 * @param sessionKey The symmetric session key with which the private key will be encrypted
	 * @param contractCertPrivateKey The private key which is to be encrypted
	 * @return The encrypted private key of the contract certificate given as a byte array
	 */
	private static byte[] encryptPrivateKey(SecretKey sessionKey, ECPrivateKey contractCertPrivateKey) {
		try {
			/*
			 * Padding of the plain text (private key) is not required as its length (256 bit) is a 
			 * multiple of the block size (128 bit) of the used encryption algorithm (AES)
			 */
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			IvParameterSpec ivParamSpec = new IvParameterSpec(generateRandomNumber(16));
			cipher.init(Cipher.ENCRYPT_MODE, sessionKey, ivParamSpec);
			
			/*
			 * Not the complete ECPrivateKey container, but the private value s represents the 256 bit 
			 * private key which must be encoded. 
			 * The private key is stored as an ASN.1 integer which may need to have zero padding 
			 * in the most significant bits removed (if 33 bytes)
			 */
			byte[] encryptedKey;
			if (contractCertPrivateKey.getS().toByteArray().length == 33) {
				byte[] temp = new byte[32];
				System.arraycopy(contractCertPrivateKey.getS().toByteArray(), 1, temp, 0, contractCertPrivateKey.getS().toByteArray().length-1);
				encryptedKey = cipher.doFinal(temp);
			} else {
				encryptedKey = cipher.doFinal(contractCertPrivateKey.getS().toByteArray());
			}
			
			/*
			 * The IV must be transmitted in the 16 most significant bytes of the
			 * ContractSignatureEncryptedPrivateKey
			 */
			byte[] encryptedKeyWithIV = new byte[ivParamSpec.getIV().length + encryptedKey.length];
			System.arraycopy(ivParamSpec.getIV(), 0, encryptedKeyWithIV, 0, ivParamSpec.getIV().length);
			System.arraycopy(encryptedKey, 0, encryptedKeyWithIV, ivParamSpec.getIV().length, encryptedKey.length);
			
			return encryptedKeyWithIV;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
				 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to encrypt private key." +
							  "\nSession key (" + sessionKey.getEncoded().length + " bytes): " +
							  ByteUtils.toHexString(sessionKey.getEncoded()) +
							  "\nContract certificate private key (" + contractCertPrivateKey.getS().toByteArray().length + " bytes): " +
							  ByteUtils.toHexString(contractCertPrivateKey.getS().toByteArray()), e);
		} 
		
		return null;
	}
	
	
	/**
	 * Decrypts the encrypted private key of the contract certificate which is to be installed.
	 * 
	 * @param dhPublicKey The ECDH public key received the the respective response message
	 * 		  (either CertificateInstallationRes or CertificateUpdateRes)
	 * @param contractSignatureEncryptedPrivateKey The encrypted private key of the contract certificate
	 * @param certificateECPrivateKey The private key of either OEMProvisioningCertificate (in case of
	 * 		  receipt of CertificateInstallationRes) or the existing ContractCertificate which is to be
	 * 		  updated (in case of receipt of CertificateUpdateRes).
	 * @return The decrypted private key of the contract certificate which is to be installed
	 */
	public static ECPrivateKey decryptContractCertPrivateKey(
			byte[] dhPublicKey,
			byte[] contractSignatureEncryptedPrivateKey,
			ECPrivateKey certificateECPrivateKey) {
		// Generate shared secret
		ECPublicKey publicKey = (ECPublicKey) getPublicKey(dhPublicKey);
		byte[] sharedSecret = generateSharedSecret(certificateECPrivateKey, publicKey);
		if (sharedSecret == null) {
			getLogger().error("Shared secret could not be generated");
			return null;
		}
		
		// Generate the session key ...
		SecretKey sessionKey = generateSessionKey(sharedSecret);
		if (sessionKey == null) {
			getLogger().error("Session key secret could not be generated");
			return null;
		}
		
		// ... to decrypt the contract certificate private key
		ECPrivateKey contractCertPrivateKey = decryptPrivateKey(sessionKey, contractSignatureEncryptedPrivateKey);
		if (contractCertPrivateKey == null) {
			getLogger().error("Contract certificate private key secret could not be decrypted");
			return null;
		}
		
		return contractCertPrivateKey;
	}
	
	
	/**
	 * The private key corresponding to the contract certificate is to be decrypted by 
	 * the receiver (EVCC) using the session key derived in the ECDH protocol.
	 * Applies the algorithm AES-CBC-128 according to NIST Special Publication 800-38A.
	 * The initialization vector IV shall be read from the 16 most significant bytes of the 
	 * ContractSignatureEncryptedPrivateKey field.
	 * 
	 * @param sessionKey The symmetric session key with which the encrypted private key is to be decrypted
	 * @param encryptedKeyWithIV The encrypted private key of the contract certificate given as a byte array
	 * 							 whose first 16 byte hold the initialization vector
	 * @return The decrypted private key of the contract certificate
	 */
	private static ECPrivateKey decryptPrivateKey(SecretKey sessionKey, byte[] encryptedKeyWithIV) {
		byte[] initVector = new byte[16];
		byte[] encryptedKey = null;
		
		try {
			// Get the first 16 bytes of the encrypted private key which hold the IV
			
			encryptedKey = new byte[encryptedKeyWithIV.length - 16];
			System.arraycopy(encryptedKeyWithIV, 0, initVector, 0, 16);
			System.arraycopy(encryptedKeyWithIV, 16, encryptedKey, 0, encryptedKeyWithIV.length - 16);
			
			IvParameterSpec ivParamSpec = new IvParameterSpec(initVector);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			
			/*
			 * You must have the Java Cryptography Extension (JCE) Unlimited Strength 
			 * Jurisdiction Policy Files 8 installed, otherwise this cipher.init call will yield a
			 * "java.security.InvalidKeyException: Illegal key size"
			 */
			cipher.init(Cipher.DECRYPT_MODE, sessionKey, ivParamSpec);
			byte[] decrypted = cipher.doFinal(encryptedKey);

			return getPrivateKey(decrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
				NegativeArraySizeException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to decrypt private key" +
					  "\nSession key (" + (sessionKey != null ? sessionKey.getEncoded().length : 0) + " bytes): " +
					  ByteUtils.toHexString(sessionKey.getEncoded()) +
					  "\nEncrypted key (" + (encryptedKey != null ? encryptedKey.length : 0) + " bytes): " +
					  ByteUtils.toHexString(encryptedKey) +
					  "\nEncrypted key with IV (" + (encryptedKeyWithIV != null ? encryptedKeyWithIV.length : 0) + " bytes): " +
					  ByteUtils.toHexString(encryptedKey), e);
		} 
		
		return null;
	}
	
	
	/**
	 * Useful for debugging purposes when verifying a signature and trying to figure out where it went wrong if
	 * a signature verification failed.
	 * 
	 * @return
	 */
//	public static byte[] decryptSignature(byte[] signature, ECPublicKey publicKey) {
//		
//	}
	
	
	/**
	 * Returns the EMAID (e-mobility account identifier) from the contract certificate.
	 * 
	 * @param contractCertificateChain The certificate chain holding the contract certificate
	 * @return The EMAID
	 */
	public static EMAIDType getEMAID(CertificateChainType contractCertificateChain) {
		X509Certificate contractCertificate = getCertificate(contractCertificateChain.getCertificate());
		return getEMAIDFromDistinguishedName(contractCertificate.getSubjectX500Principal().getName());
	}
	
	
	/**
	 * Returns the EMAID (e-mobility account identifier) from the contract certificate.
	 * 
	 * @param keyStorePassword The password which protects the keystore holding the contract certificate
	 * @return The EMAID
	 */
	public static EMAIDType getEMAID(String keyStorePassword) {
		KeyStore keyStore = getKeyStore(GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(), keyStorePassword);
		
		try {
			X509Certificate contractCertificate = 
					(X509Certificate) keyStore.getCertificate(GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString());
			
			if (contractCertificate == null) {
				getLogger().error("No contract certificate with alias '" +
									 GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString() + "' found");
				return null;
			}
			
			return getEMAIDFromDistinguishedName(contractCertificate.getSubjectX500Principal().getName());
		} catch (KeyStoreException e) {
			getLogger().error("KeyStoreException occurred while trying to get EMAID from keystore", e);
			return null;
		}
	}
	
	
	/**
	 * Reads the EMAID (e-mobility account identifier) from the distinguished name (DN) of a certificate. 
	 * 
	 * @param distinguishedName The distinguished name whose 'CN' component holds the EMAID
	 * @return The EMAID
	 */
	private static EMAIDType getEMAIDFromDistinguishedName(String distinguishedName) {	
		EMAIDType emaid = new EMAIDType();
		
		LdapName ln = null;
		try {
			ln = new LdapName(distinguishedName);
		} catch (InvalidNameException e) {
			getLogger().error("InvalidNameException occurred while trying to get EMAID from distinguished name", e);
		}

		for(Rdn rdn : ln.getRdns()) {
		    if (rdn.getType().equalsIgnoreCase("CN")) {
		    	// Optional hyphens used for better human readability must be omitted here
		    	emaid.setId(rdn.getValue().toString().replace("-", ""));
		    	emaid.setValue(rdn.getValue().toString().replace("-", ""));
		        break;
		    }
		}
		
		return emaid;
	}
	
	
	/**
	 * Searches a given keystore either for a contract certificate chain or OEM provisioning certificate
	 * chain, determined by the alias (the alias is associated with the certificate chain and the private
	 * key). 
	 * However, it may be the case that more than once contract certificate is installed in the EV, 
	 * in which case an OEM specific implementation would need to interact at this point with a HMI in
	 * order to enable the user to select the certificate which is to be used for contract based charging.
	 * 
	 * @param evccKeyStore The keystore to check for the respective certificate chain
	 * @param alias The alias associated with a key entry and certificate chain
	 * @return The respective certificate chain if present, null otherwise
	 */
	public static CertificateChainType getCertificateChain(KeyStore evccKeyStore, String alias) {
		CertificateChainType certChain = new CertificateChainType();
		SubCertificatesType subCertificates = new SubCertificatesType();
		
		try {
			Certificate[] certChainArray = evccKeyStore.getCertificateChain(alias);
			
			if (certChainArray == null) {
				getLogger().info("No certificate chain found for alias '" + alias + "'");
				return null;
			}
			
			certChain.setCertificate(certChainArray[0].getEncoded());

			for (int i = 1; i < certChainArray.length; i++) {
				subCertificates.getCertificate().add(certChainArray[i].getEncoded());
			}
			
			certChain.setSubCertificates(subCertificates);
			
			return certChain;
		} catch (KeyStoreException | CertificateEncodingException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to get certificate chain", e);
			return null;
		}
	}
	
	
	/**
	 * Returns a random number of a given length of bytes.
	 * 
	 * @param lengthOfBytes The number of bytes which hold the generated random number
	 * @return A random number given as a byte array
	 */
	public static byte[] generateRandomNumber(int lengthOfBytes) {
		// TODO how to assure that the entropy of the genChallenge is at least 120 bits according to [V2G2-826]?
		
		SecureRandom random = new SecureRandom();
		byte[] randomNumber = new byte[lengthOfBytes];
		random.nextBytes(randomNumber);
		
		return randomNumber;
	}
	
	
	/**
	 * Generates a digest for a complete message or field (which ever is handed over as first parameter).
	 * During digest (SHA-256) generation, the parameter is converted to a JAXBElement and then EXI encoded 
	 * using the respective EXI schema-informed grammar. If the digest for the signature is to be generated,  
	 * the second parameter is to be set to true, for all other messages or fields the second parameter 
	 * needs to be set to false.
	 * 
	 * @param messageOrField The message or field for which a digest is to be generated
	 * @param digestForSignedInfoElement True if a digest for the SignedInfoElement of the header's signature is to be generated, false otherwise
	 * @return The SHA-256 digest for message or field
	 */
	public static byte[] generateDigest(Object messageOrField) {
		JAXBElement jaxbElement = MiscUtils.getJaxbElement(messageOrField);
		byte[] encoded; 
		
		// The schema-informed fragment grammar option needs to be used for EXI encodings in the header's signature
		getExiCodec().setFragment(true);
		
		/*
		 * When creating the signature value for the SignedInfoElement, we need to use the XMLdsig schema,
		 * whereas for creating the reference elements of the signature, we need to use the V2G_CI_MsgDef schema.
		 */
		if (messageOrField instanceof SignedInfoType) encoded = getExiCodec().encodeEXI(jaxbElement, GlobalValues.SCHEMA_PATH_XMLDSIG.toString());
		else encoded = getExiCodec().encodeEXI(jaxbElement, GlobalValues.SCHEMA_PATH_MSG_DEF.toString());
		
		// Do not use the schema-informed fragment grammar option for other EXI encodings (message bodies)
		getExiCodec().setFragment(false);
		
		if (encoded == null) {
			getLogger().error("Digest could not be generated because of EXI encoding problem");
			return null;
		}
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(encoded);
			byte[] digest = md.digest();
			
			if (showSignatureVerificationLog) {
				/*
				 * Show Base64 encoding of digests only for reference elements, not for the SignedInfo element.
				 * The hashed SignedInfo element is input for ECDSA before the final signature value gets Base64 encoded.
				 */
				if ( !(messageOrField instanceof SignedInfoType) ) {
					getLogger().debug("\n"
									+ "\tDigest generated for reference element " + messageOrField.getClass().getSimpleName() + ": " + ByteUtils.toHexString(digest) + "\n"
									+ "\tBase64 encoding of digest: " + Base64.getEncoder().encodeToString(digest));
				}
			}
				
			return digest;
		} catch (NoSuchAlgorithmException e) {
			getLogger().error("NoSuchAlgorithmException occurred while trying to create digest", e);
			return null;
		}
	}
	
	
	/**
	 * Signs the SignedInfo element of the V2GMessage header.
	 * 
	 * @param signedInfoElementExi The EXI-encoded SignedInfo element given as a byte array
	 * @param ecPrivateKey The private key which is used to sign the SignedInfo element
	 * @return The signature value for the SignedInfo element given as a byte array
	 */
	public static byte[] signSignedInfoElement(byte[] signedInfoElementExi, ECPrivateKey ecPrivateKey) {
		try {
			Signature ecdsa = Signature.getInstance("SHA256withECDSA", "SunEC");
		
			ecdsa.initSign(ecPrivateKey);
			ecdsa.update(signedInfoElementExi);
			
			byte[] signature = ecdsa.sign();
			
			// Java operates on DER encoded signatures, but we must send the raw r and s values as signature 
			byte[] rawSignature = getRawSignatureFromDEREncoding(signature);
			
			return rawSignature;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to create signature", e);
			return null;
		}
	}
	
	/**
	 * Verifies the signature given in the received header of an EVCC or SECC message
	 * 
	 * @param signature The received header's signature
	 * @param verifyXMLSigRefElements The HashMap of signature IDs and digest values of the message body 
	 * 		  or fields respectively of the received message (to cross-check against the XML reference
	 * 		  elements contained in the received message header)
	 * @param verifyCert The certificate holding the public key corresponding to the private key which was used 
	 * 			for the signature. Given as a byte array, this function will call verifySignature() with an X509Certificate
	 * 			as last parameter.
	 * @return True, if digest validation of all XML reference elements and signature validation was 
	 * 		   successful, false otherwise
	 */
	public static boolean verifySignature(
			SignatureType signature, 
			HashMap<String, byte[]> verifyXMLSigRefElements, 
			byte[] verifyCert) {
		X509Certificate x509VerifyCert = getCertificate(verifyCert);
		return verifySignature(signature, verifyXMLSigRefElements, x509VerifyCert);
	}
	
	/**
	 * Verifies the signature given in the received header of an EVCC or SECC message
	 * 
	 * @param signature The received header's signature
	 * @param verifyXMLSigRefElements The HashMap of signature IDs and digest values of the message body 
	 * 		  or fields respectively of the received message (to cross-check against the XML reference
	 * 		  elements contained in the received message header)
	 * @param verifyCert The certificate holding the public key corresponding to the private key which was used for the signature
	 * @return True, if digest validation of all XML reference elements and signature validation was 
	 * 		   successful, false otherwise
	 */
	public static boolean verifySignature(
				SignatureType signature, 
				HashMap<String, byte[]> verifyXMLSigRefElements, 
				X509Certificate verifyCert) {
		byte[] calculatedReferenceDigest; 
		boolean messageDigestsEqual;
		
		/*
		 * 1. step: 
		 * Iterate over all element IDs of the message which should have been signed and find the 
		 * respective Reference element in the given message header
		 */
		for (String id : verifyXMLSigRefElements.keySet()) {
			getLogger().debug("Verifying digest for element '" + id + "'");
			messageDigestsEqual = false;
			calculatedReferenceDigest = verifyXMLSigRefElements.get(id);
			
			for (ReferenceType reference : signature.getSignedInfo().getReference()) {
				if (reference == null) {
					getLogger().warn("Reference element to check is null");
					continue;
				}
				
				// We need to check the URI attribute, not the Id attribute. But the Id must be set to sth. different than the IDs used in the body!
				if (reference.getURI() == null) {
					getLogger().warn("Reference ID element is null");
					continue;
				}
				
				if (reference.getURI().equals('#' + id)) {
					messageDigestsEqual = MessageDigest.isEqual(reference.getDigestValue(), calculatedReferenceDigest);
					
					if (showSignatureVerificationLog) {
						getLogger().debug("\n" 
										+ "\tReceived digest of reference with ID '" + id + "':   " + ByteUtils.toHexString(reference.getDigestValue()) + "\n"
										+ "\tCalculated digest of reference with ID '" + id + "': " + ByteUtils.toHexString(calculatedReferenceDigest) + "\n"
										+ "\t==> Match: " + messageDigestsEqual);
					}
					
				}
			}
			
			if (!messageDigestsEqual) {
				getLogger().error("No matching signature found for ID '" + id + "' and digest value " + 
								  ByteUtils.toHexString(calculatedReferenceDigest));
				return false;
			}
		}
		
		
		/*
		 * 2. step:
		 * Check the signature itself
		 */
		ECPublicKey ecPublicKey = (ECPublicKey) verifyCert.getPublicKey();
		Signature ecdsa;
		boolean verified; 
		
		try {
			getLogger().debug("Verifying signature of SignedInfo element ...");
			
			// Check if signature verification logging is to be shown (for debug purposes)
			
			if (showSignatureVerificationLog) showSignatureVerificationLog(verifyCert, signature, ecPublicKey);
			
			ecdsa = Signature.getInstance("SHA256withECDSA");
			// The Signature object needs to be initialized by setting it into the VERIFY state with the public key
			ecdsa.initVerify(ecPublicKey);
			
			// The data to be signed needs to be supplied to the Signature object
			byte[] exiEncodedSignedInfo = getExiCodec().getExiEncodedSignedInfo(signature.getSignedInfo());
			ecdsa.update(exiEncodedSignedInfo);
			
			// Java operates on DER encoded signature values, but the sent signature consists of the raw r and s value 
			byte[] signatureValue = signature.getSignatureValue().getValue();
			byte[] derEncodedSignatureValue = getDEREncodedSignature(signatureValue);
			
			// The verify() method will do both, the decryption and SHA256 validation. So don't hash separately before verifying
			verified = ecdsa.verify(derEncodedSignatureValue);
			
			return verified;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to verify signature value", e);
			return false;
		} 
	}
	
	
	/**
	 * Shows some extended logging while verifying a signature for debugging purposes.
	 * @param verifyCert The X509Certificate whose public key is used to verify the signature, used for printing the 
	 * 					 certificate's subject value
	 * @param signature The signature contained in the header of the V2GMessage
	 * @param ecPublicKey The public key used to verify the signature
	 */
	private static void showSignatureVerificationLog(X509Certificate verifyCert, SignatureType signature, ECPublicKey ecPublicKey) {
		byte[] computedSignedInfoDigest = generateDigest(signature.getSignedInfo());
		byte[] receivedSignatureValue = signature.getSignatureValue().getValue();
		
		getLogger().debug("\n" 
				 + "\tCertificate used to verify signature: " + verifyCert.getSubjectX500Principal().getName() + "\n"
				 + "\tPublic key used to verify signature: " + ByteUtils.toHexString(getUncompressedSubjectPublicKey(ecPublicKey)) + "\n"
				 + "\tReceived signature value: " + ByteUtils.toHexString(receivedSignatureValue) + " (Base64: " + Base64.getEncoder().encodeToString(receivedSignatureValue) + ")\n"
				 + "\tCalculated digest of SignedInfo element: " + ByteUtils.toHexString(computedSignedInfoDigest));
	}
	

	/**
	 * Java puts some encoding information into the ECPublicKey.getEncoded(). 
	 * This method returns the raw ECPoint (the x and y coordinate of the public key) in uncompressed form 
	 * (with the 0x04 as first octet), aka the Subject Public Key according to RFC 5480
	 *
	 * @param ecPublicKey The ECPublicKey provided by Java
	 * @return The uncompressed Subject Public Key (with the first octet set to 0x04)
	 */
	public static byte[] getUncompressedSubjectPublicKey(ECPublicKey ecPublicKey) {
		byte[] uncompressedPubKey = new byte[65];
		uncompressedPubKey[0] = 0x04;
		
		byte[] affineX = ecPublicKey.getW().getAffineX().toByteArray();
		byte[] affineY = ecPublicKey.getW().getAffineY().toByteArray();
		
		// If the length is 33 bytes, then the first byte is a 0x00 which is to be omitted
		if (affineX.length == 33)
			System.arraycopy(affineX, 1, uncompressedPubKey, 1, 32);
		else
			System.arraycopy(affineX, 0, uncompressedPubKey, 1, 32);
		
		if (affineY.length == 33)
			System.arraycopy(affineY, 1, uncompressedPubKey, 33, 32);
		else
			System.arraycopy(affineY, 0, uncompressedPubKey, 33, 32);
		
		return uncompressedPubKey;
	}
	
	
	/**
	 * An ECDSA signature consists of two integers s and r, each of the bit length equal to the curve size.
	 * When Java is creating an ECDSA signature, it is encoding it in the DER (Distinguished Encoding Rules) format.
	 * But in ISO 15118, we do not expect DER encoded signatures. Thus, this function takes the DER encoded signature
	 * as input and returns the raw r and s integer values of the signature. 
	 * 
	 * @param derEncodedSignature The DER encoded signature as a result from java.security.Signature.sign()
	 * @return A byte array containing only the r and s value of the signature
	 */
	public static byte[] getRawSignatureFromDEREncoding(byte[] derEncodedSignature) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		
		// Length of r is encoded in the fourth byte (either 32 (hex: 0x20) or 33 (hex: 0x21))
		int lengthOfR = (int) derEncodedSignature[3];
		// Length of r is encoded in the second byte AFTER r (either 32 (hex: 0x20) or 33 (hex: 0x21))
		int lengthOfS = (int) derEncodedSignature[lengthOfR + 5];
		
		// If r is made up of 33 bytes, then we need to skip the first fill byte (0x00) of r
		if (lengthOfR == 33) System.arraycopy(derEncodedSignature, 5, r, 0, 32);
		else System.arraycopy(derEncodedSignature, 4, r, 0, 32);
		
		// If r is made up of 33 bytes (hex value 0x21), then we need to skip the first fill byte (0x00) or r
		if (lengthOfS == 33) System.arraycopy(derEncodedSignature, lengthOfR + 7, s, 0, 32);
		else System.arraycopy(derEncodedSignature, lengthOfR + 6, s, 0, 32);
		
	    try {
			baos.write(r);
			baos.write(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    byte[] rawRAndS = baos.toByteArray();
	    
	    return rawRAndS;
	}
	
	
	/**
	 * When encoded in DER, the signature - holding the 
	 * x-coordinate of the elliptic curve point in the value "r"
	 * and the 
	 * y-coordinate of the elliptic curve point in the value "s" 
	 * - becomes the following sequence of bytes (in total 70 bytes instead of 64 bytes):
	 *
	 * 0x30 b1 0x02 b2 (vr) 0x02 b3 (vs)
	 *	
	 * where:
	 *	
	 * - 0x30 is always the first byte of the DER encoded signature format (ASN.1 tag for sequence)
	 * - b1 is a single byte value, encoding the length in bytes of the remaining list of bytes 
	 *   (from the first 0x02 to the end of the encoding); is a value between 0x44 and 0x46
	 * - 0x02 is a fixed value indicating that an integer value will follow (ASN.1 tag for int)
	 * - b2 is a single byte value, encoding the length in bytes of (vr);
	 *   (either 0x20 (32 bytes) or 0x21 (33 bytes), depending on whether an optional fill byte 0x00 is used as most significant byte)
	 * - (vr) is the signed big-endian encoding of the value "r", of minimal length;
	 * - 0x02 is a fixed value indicating that an integer value will follow (ASN.1 tag for int)
	 * - b3 is a single byte value, encoding the length in bytes of (vs);
	 *   (either 0x20 (32 bytes) or 0x21 (33 bytes), depending on whether an optional fill byte 0x00 is used as most significant byte)
	 * - (vs) is the signed big-endian encoding of the value "s", of minimal length.
	 */
	private static byte[] getDEREncodedSignature (byte[] signatureValue) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// First we separate x and y of coordinates into separate variables
	    byte[] r = new byte[32];
	    byte[] s = new byte[32];
	    System.arraycopy(signatureValue, 0, r, 0, 32);
	    System.arraycopy(signatureValue, 32, s, 0, 32);
	    
	    int neededByteLength = signatureValue.length + 6; // 6 bytes for the header
	    boolean isFillByteForR = false;
	    boolean isFillByteForS = false;
	    
	    if (r[0] < 0) { // checks if the value is negative which is equivalent to r[0] is bigger than 0x7f
	    	isFillByteForR = true;
	    	neededByteLength += 1;
	    }
	    
	    if (s[0] < 0)  {
	    	isFillByteForS = true;
	    	neededByteLength += 1;
	    }
	    
		baos.write(0x30);
		baos.write(neededByteLength - 2);
		
		baos.write(0x02);
		
		try {
			if (isFillByteForR) {
				baos.write(0x21);
				baos.write(0x00);
				baos.write(r);
			} else {
				baos.write(0x20);
				baos.write(r);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		baos.write(0x02);
		
		try {
			if (isFillByteForS) {
				baos.write(0x21);
				baos.write(0x00);
				baos.write(s);
			} else {
				baos.write(0x20);
				baos.write(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] derEncodedSignature = baos.toByteArray();
		
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return derEncodedSignature;
	}
	
	
	/**
	 * Sets the SSLContext of the TLSServer and TLSClient with the given keystore and truststore locations as
	 * well as the password protecting the keystores/truststores.
	 * 
	 * @param keyStorePath The relative path and filename for the keystore
	 * @param trustStorePath The relative path and filename for the truststore
	 * @param keyStorePassword The password protecting the keystore
	 */
	public static void setSSLContext(
			String keyStorePath, 
			String trustStorePath,
			String keyStorePassword) {
	    KeyStore keyStore = SecurityUtils.getKeyStore(keyStorePath, keyStorePassword);
	    KeyStore trustStore = SecurityUtils.getKeyStore(trustStorePath, keyStorePassword);

		try {
			// Initialize a key manager factory with the keystore
		    KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyFactory.init(keyStore, keyStorePassword.toCharArray());
		    KeyManager[] keyManagers = keyFactory.getKeyManagers();

		    // Initialize a trust manager factory with the truststore
		    TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());    
		    trustFactory.init(trustStore);
		    TrustManager[] trustManagers = trustFactory.getTrustManagers();

		    // Initialize an SSL context to use these managers and set as default
		    SSLContext sslContext = SSLContext.getInstance("TLS");
		    sslContext.init(keyManagers, trustManagers, null);
		    SSLContext.setDefault(sslContext); 
		} catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | 
				KeyManagementException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to initialize SSL context");
		}    
	}
	
	public static void setExiCodec(ExiCodec exiCodecChoice) {
		exiCodec = exiCodecChoice;
	}
	
	private static ExiCodec getExiCodec() {
		return exiCodec;
	}
}
