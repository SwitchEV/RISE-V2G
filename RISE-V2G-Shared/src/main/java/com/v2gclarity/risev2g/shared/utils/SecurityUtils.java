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
package com.v2gclarity.risev2g.shared.utils;

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
import java.util.Map;
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
import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import com.v2gclarity.risev2g.shared.enumerations.PKI;
import com.v2gclarity.risev2g.shared.exiCodec.ExiCodec;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CanonicalizationMethodType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ContractSignatureEncryptedPrivateKeyType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DiffieHellmanPublickeyType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.DigestMethodType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EMAIDType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ListOfRootCertificateIDsType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ReferenceType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureMethodType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignatureType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SignedInfoType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SubCertificatesType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.TransformType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.TransformsType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.X509IssuerSerialType;

import java.util.Base64;

public final class SecurityUtils {
	/*
	 * Add VM (virtual machine) argument "-Djavax.net.debug=ssl" if you want more detailed debugging output
	 */
	
	static Logger logger = LogManager.getLogger(SecurityUtils.class.getSimpleName());
	static ExiCodec exiCodec;
	static boolean showSignatureVerificationLog = ((boolean) MiscUtils.getPropertyValue("signature.verification.showlog")); 
	
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
	 * The keystore file itself must reside outside the JAR file, at the same level as the JAR file itself,
	 * because 
	 * a) at least the evccKeystore needs to be editable when installing the contract certificate (JAR file is read-only), and
	 * b) it is very likely that private keys and certificate chains might be stored separately in a secure hardware module.
	 * Therefore, the file is not loaded with getResourceAsStream(), but with a FileInputStream.
	 * 
	 * @param keyStorePath The relative path and file name of the keystore 
	 * @param keyStorePassword The password which protects the keystore
	 * @return The respective keystore
	 */
	public static KeyStore getKeyStore(String keyStorePath, String keyStorePassword) {
		FileInputStream keyStore;
		
		try {
			keyStore = new FileInputStream(keyStorePath);
			return getKeyStore(keyStore, keyStorePassword, "jks");
		} catch (FileNotFoundException e) {
			getLogger().error("Keystore file location '" + keyStorePath + "' not found (FileNotFoundException).");
			return null;
		}
	}
	
	/**
	 * Returns the standard JKS truststore which holds the respective trusted certificates for the EVCC 
	 * or SECC (whoever calls this method).
	 * 
	 * The truststore file itself must reside outside the JAR file, at the same level as the JAR file itself,
	 * because 
	 * a) at least the evccKeystore needs to be editable when installing the contract certificate (JAR file is read-only), and
	 * b) it is very likely that private keys and certificate chains might be stored separately in a secure hardware module.
	 * Therefore, the file is not loaded with getResourceAsStream(), but with a FileInputStream.
	 * 
	 * @param trustStorePath The relative path and file name of the truststore
	 * @param trustStorePassword The password which protects the truststore
	 * @return The respective truststore
	 */
	public static KeyStore getTrustStore(String trustStorePath, String trustStorePassword) {
		FileInputStream trustStore;
		
		try {
			trustStore = new FileInputStream(trustStorePath);
			return getKeyStore(trustStore, trustStorePassword, "jks");
		} catch (FileNotFoundException e) {
			getLogger().error("Truststore file location '" + trustStorePath + "' not found (FileNotFoundException).");
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
	 * Checks whether the given certificate is currently valid with regards to date and time.
	 * 
	 * @param certificate The X509Certificiate to be checked for validity
	 * @return ResponseCode FAILED_CertificateExpired, if the certificate is expired. FAILED, if the certificate is
	 * 			not yet valid, since there is no other proper response code available. OK, otherwise.
	 */
	public static ResponseCodeType verifyValidityPeriod(X509Certificate certificate) {
		try {
			certificate.checkValidity();		
			return ResponseCodeType.OK;
		} catch (CertificateExpiredException e) {
			X500Principal subject = certificate.getSubjectX500Principal();
			
			getLogger().warn("Certificate with distinguished name '" + subject.getName() + 
							 "' already expired (not after " + certificate.getNotAfter() + ")");
			return ResponseCodeType.FAILED_CERTIFICATE_EXPIRED;
		} catch (CertificateNotYetValidException e) {
			X500Principal subject = certificate.getSubjectX500Principal();
			getLogger().warn("Certificate with distinguished name '" + subject.getName() + 
							 "' not yet valid (not before " + certificate.getNotBefore() + ")");
			return ResponseCodeType.FAILED;
		} 
	}
	
	
	/**
	 * Domain Component restrictions: <br/>
	 * - SECC certificate: "CPO" (verification by EVCC) <br/>
	 * - CPS leaf certificate: "CPS" (verification by EVCC) <br/>
	 * - OEM Provisioning Certificate: "OEM" (verification by provisioning service (neither EVCC nor SECC))
	 * 
	 * @param certificate The X509Certificiate to be checked for validity
	 * @param domainComponent The domain component to be checked for in the distinguished name of the certificate
	 * @return True, if the given domain component is present in the distinguished name, false otherwise
	 */
	public static boolean verifyDomainComponent(X509Certificate certificate, String domainComponent) {
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
			getLogger().error("InvalidNameException occurred while trying to check domain component of certificate", e);
		}
		
		getLogger().error("Expected domain component (DC) '" + domainComponent + "' not found in certificate "
						+ "with distinguished name '" + dn + "'");
		return false;
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
	 * Executes the following validity checks:
	 * <br/><br/>
	 * 1. Verifies the signature for each certificate in the given certificate chain all the way up to the trust
	 *    anchor. Certificates in certificate chain must be in the right order (leaf -> Sub-CA2 -> Sub-CA1) <br/>
	 * 2. Verifies whether the given certificate is currently valid with regards to date and time.<br/>
	 * 3. Verifies that certificate attributes are set correctly, depending on the PKI the certificate chain belongs to
	 * 
	 * @param certChain The certificate chain to iterate over to check for validity
	 * @param trustStoreFileName The relative path and file name of the truststore 
	 * @param pki The Public Key Infrastructure to which the certChain belongs (a PKI enumeration value)
	 * @return ResponseCode applicable to the verification steps 
	 */
	public static ResponseCodeType verifyCertificateChain(
			CertificateChainType certChain, 
			String trustStoreFileName,
			PKI pki) {
		X509Certificate leafCertificate = null;
		X509Certificate subCA1Certificate = null;
		X509Certificate subCA2Certificate = null;
		ResponseCodeType responseCode = null;
		
		// Get leaf certificate
		if (certChain != null) {
			leafCertificate = getCertificate(certChain.getCertificate());
		} else {
			getLogger().error("Signature verification failed because provided certificate chain is empty (null)");
			return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
		}
		
		// Get Sub-CA certificates
		if (leafCertificate != null) {
			SubCertificatesType subCertificates = certChain.getSubCertificates();
			
			if (subCertificates != null && subCertificates.getCertificate().size() != 0) {
				subCA2Certificate = getCertificate(subCertificates.getCertificate().get(0));
				
				if (subCertificates.getCertificate().size() == 2)
					subCA1Certificate = getCertificate(subCertificates.getCertificate().get(1));
			} else {
				getLogger().error("Signature verification failed because no Sub-CA certificates available in provided "
								+ "certificate chain");
				return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
			}
		} else {
			getLogger().error("Signature verification failed because no leaf certificate available in provided "
							+ "certificate chain");
			return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
		}
		
		
		/*
		 * ****************
		 * SIGNATURE CHECKS
		 * ****************
		 */
		
		// Check signature of leaf certificate
		if (!verifySignature(leafCertificate, subCA2Certificate)) return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
				 
		// Check signature of Sub-CA 2 and optionally, if present, Sub-CA 2 certificate
		if (subCA1Certificate != null) {
			if (!verifySignature(subCA2Certificate, subCA1Certificate)) return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
			if (!verifySignature(subCA1Certificate, trustStoreFileName)) return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
		} else {
			// In case there is only one intermediate certificate (profile of Sub-CA 2)
			if (!verifySignature(subCA2Certificate, trustStoreFileName)) return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
		}
		
		
		/*
		 * **********************
		 * VALIDITY PERIOD CHECKS
		 * **********************
		 */
		ResponseCodeType validityResponseCode = null;
		
		// Check validity of leaf certificate
		validityResponseCode = verifyValidityPeriod(leafCertificate); 
		if (!validityResponseCode.equals(ResponseCodeType.OK)) return validityResponseCode;
		
		// Check validity of Sub-CA2 certificate
		validityResponseCode = verifyValidityPeriod(subCA2Certificate);
		if (!validityResponseCode.equals(ResponseCodeType.OK)) return validityResponseCode;
		
		// Check validity of Sub-CA1 certificate, if present
		if (subCA1Certificate != null) {
			validityResponseCode = verifyValidityPeriod(subCA1Certificate);
			if (!validityResponseCode.equals(ResponseCodeType.OK)) return validityResponseCode;
		}
		
		
		/*
		 * ***********************************
		 * COMMON CERTIFICATE ATTRIBUTES CHECK
		 * ***********************************
		 */
		
		// Check pathLenContraint (maximum number of non-self-issued intermediate certificates that may follow this certificate)
		if (subCA2Certificate.getBasicConstraints() != 0) {
			getLogger().error("Sub-CA 2 certificate with distinguished name '" + 
							  subCA2Certificate.getSubjectX500Principal().getName() + "' has incorrect value for " +
							  "pathLenConstraint. Should be 0 instead of " + subCA2Certificate.getBasicConstraints());
			return ResponseCodeType.FAILED_CERTIFICATE_EXPIRED;
		}
		
		if (subCA1Certificate != null && subCA1Certificate.getBasicConstraints() != 1) {
			getLogger().error("Sub-CA 1 certificate with distinguished name '" + 
							  subCA1Certificate.getSubjectX500Principal().getName() + "' has incorrect value for " +
							  "pathLenConstraint. Should be 1 instead of " + subCA2Certificate.getBasicConstraints());
			return ResponseCodeType.FAILED_CERTIFICATE_EXPIRED;
		}
		
		
		responseCode = verifyLeafCertificateAttributes(leafCertificate, pki);
		if (responseCode.equals(ResponseCodeType.OK))
			return responseCode;
		
		return ResponseCodeType.OK;
	}
	
	
	/**
	 * Checks certificate attributes for a given leaf certificate belonging to an ISO 15118 PKI.
	 * 
	 * @param certificate The X.509 certificate whose attributes need to be checked
	 * @param pki The PKI to which the certificate belongs
	 * @return
	 */
	public static ResponseCodeType verifyLeafCertificateAttributes(X509Certificate leafCertificate, PKI pki) {
		switch (pki) {
		case CPO:
			if (!verifyDomainComponent(leafCertificate, "CPO")) {
				getLogger().error("SECC leaf certificate with distinguished name '" + 
						leafCertificate.getSubjectX500Principal().getName() + "' has incorrect value for " +
						  "domain component. Should be 'CPO'");
				return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
			}
			break;
		case CPS:
			if (!verifyDomainComponent(leafCertificate, "CPS")) {
				getLogger().error("CPS leaf certificate with distinguished name '" + 
						leafCertificate.getSubjectX500Principal().getName() + "' has incorrect value for " +
						  "domain component. Should be 'CPS'");
				return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
			}
			break;
		case MO:
			if (!isEMAIDSyntaxValid(leafCertificate)) {
				return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
			}
			break;
		case OEM:
			if (!verifyDomainComponent(leafCertificate, "OEM")) {
				getLogger().error("OEM provisioning certificate with distinguished name '" + 
						leafCertificate.getSubjectX500Principal().getName() + "' has incorrect value for " +
						  "domain component. Should be 'OEM'");
				return ResponseCodeType.FAILED_CERT_CHAIN_ERROR;
			}
			break;
		default:
			break;
		}
		
		return ResponseCodeType.OK;
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
	public static boolean verifySignature(X509Certificate certificate, X509Certificate issuingCertificate) {
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
	 * Iterates over the certificates stored in the truststore to verify the signature of the provided certificate
	 * 
	 * @param trustStoreFilename The relative path and file name of the truststore
	 * @param certificate The certificate whose signature needs to be verified
	 * @return True, if the provided certificate has been signed by one of the certificates in the 
	 * 		   truststore, false otherwise
	 */
	public static boolean verifySignature(X509Certificate certificate, String trustStoreFilename) {
		KeyStore trustStore = SecurityUtils.getTrustStore(trustStoreFilename, GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
		X500Principal expectedIssuer = certificate.getIssuerX500Principal();
		
		try {
			Enumeration<String> aliases = trustStore.aliases();
			while (aliases.hasMoreElements()) {
				X509Certificate rootCA = (X509Certificate) trustStore.getCertificate(aliases.nextElement());
				if (rootCA.getSubjectX500Principal().getName().equals(expectedIssuer.getName()) &&
					verifySignature(certificate, rootCA)) return true;
			}
		} catch (KeyStoreException | NullPointerException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to verify trust " +
							  "status of certificate with distinguished name '" + 
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
		dhPublicKey.setId("id1"); 
		
		byte[] uncompressedDHpublicKey = getUncompressedSubjectPublicKey((ECPublicKey) ecdhKeyPair.getPublic());
		
		getLogger().debug("Created DHpublickey: " + ByteUtils.toHexString(uncompressedDHpublicKey));
		dhPublicKey.setValue(uncompressedDHpublicKey);
		
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
	 * ----- !! IMPORTANT NOTE!! -----
	 * The PKCS#8 key file must be encrypted using a PKCS#12 encryption scheme, since JCE parsing of Pbes2Parameters (as defined in PKCS#5) 
	 * is buggy in Java 1.8, see also https://bugs.openjdk.java.net/browse/JDK-8076999. The bug results in an IOException when trying to 
	 * instantiate the EncryptedPrivateKeyInfo class.
	 * 
	 * The OpenSSL command used to create the DER-encoded and encrypted PKCS#8 file needs to use the 'v1 alg' option, specifying a proper algorithm. 
	 * Example: '-v1 PBE-SHA1-3DES' (see https://www.openssl.org/docs/man1.0.2/man1/openssl-pkcs8.html).
	 * -----
	 * 
	 * @param A PKCS#8 (.key) file containing the private key with value "s"
	 * @return The private key as an ECPrivateKey instance
	 */
	public static ECPrivateKey getPrivateKey(String keyFilePath) {
		Path fileLocation = Paths.get(keyFilePath);
		byte[] pkcs8ByteArray;
		
		try {
			pkcs8ByteArray = Files.readAllBytes(fileLocation);
			
			// Get the password that was used to encrypt the private key
			PBEKeySpec password = new PBEKeySpec(GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString().toCharArray());
			
			// Read the ASN.1 structure of the PKCS#8 DER-encoded file
		    EncryptedPrivateKeyInfo encryptedPrivKeyInfo = new EncryptedPrivateKeyInfo(pkcs8ByteArray);
		    
		    // Instantiate the key factory which will create the symmetric (secret) key using algorithm that is encoded in the ASN.1 structure 
		    // (see 'v1 alg' in OpenSSL's pkcs8 command) and the given password
		    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedPrivKeyInfo.getAlgName());
		    
		    // Create the symmetric key from the given password
		    Key decryptKey = secretKeyFactory.generateSecret(password);
		    
		    // Extract the PKCS8EncodedKeySpec object from the encrypted data
		    PKCS8EncodedKeySpec pkcs8PrivKeySpec = encryptedPrivKeyInfo.getKeySpec(decryptKey);
		    
		    // Generate the EC private key
			ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(pkcs8PrivKeySpec);

			return privateKey;
		} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to access private key at " +
					  "location '" + keyFilePath + "'");
			e.printStackTrace();
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
				getLogger().debug("Decrypted private key belonging to contract certificate saved. Key bytes: " + 
								   ByteUtils.toHexString(contractCertPrivateKey.getEncoded()));
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
		} else if (!verifyValidityPeriod(contractCert).equals(ResponseCodeType.OK)) { 
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
		} else if (contractCert != null && !verifyValidityPeriod(contractCert).equals(ResponseCodeType.OK)) {
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
     * @param dhPrivateKey The DH private key
     * @param contractCertPrivateKey The private key of the contract certificate
     * @return The encrypted private key of the to be installed contract certificate
     */
	public static ContractSignatureEncryptedPrivateKeyType encryptContractCertPrivateKey(
			ECPublicKey certificateECPublicKey, 
			ECPrivateKey dhPrivateKey,
			ECPrivateKey contractCertPrivateKey) {
		// Generate the shared secret by using the public key of either OEMProvCert or ContractCert
		byte[] sharedSecret = generateSharedSecret(dhPrivateKey, certificateECPublicKey);
		
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
			getLogger().debug("Encrypted private key: " + ByteUtils.toHexString(encryptedKeyWithIV));
			
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
		ECPublicKey publicKey = getPublicKey(dhPublicKey);
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
	 * Returns the EMAID (e-mobility account identifier) from the contract certificate as part of the contract certificate chain.
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
	 * @param contractCertificate The contract certificate 
	 * @return The EMAID
	 */
	public static EMAIDType getEMAID(X509Certificate contractCertificate) {
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
			    	emaid.setId("id1");
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
	 * @param jaxbMessageOrField The message or field for which a digest is to be generated, given as a JAXB element
	 * @param digestForSignedInfoElement True if a digest for the SignedInfoElement of the header's signature is to be generated, false otherwise
	 * @return The SHA-256 digest for message or field
	 */
	@SuppressWarnings("rawtypes")
	public static byte[] generateDigest(String id, JAXBElement jaxbMessageOrField) {
		byte[] encoded; 
		
		// The schema-informed fragment grammar option needs to be used for EXI encodings in the header's signature
		getExiCodec().setFragment(true);
		
		/*
		 * When creating the signature value for the SignedInfoElement, we need to use the XMLdsig schema,
		 * whereas for creating the reference elements of the signature, we need to use the V2G_CI_MsgDef schema.
		 */
		if (jaxbMessageOrField.getValue() instanceof SignedInfoType) {
			encoded = getExiCodec().encodeEXI(jaxbMessageOrField, GlobalValues.SCHEMA_PATH_XMLDSIG.toString());
		} else encoded = getExiCodec().encodeEXI(jaxbMessageOrField, GlobalValues.SCHEMA_PATH_MSG_DEF.toString());
		
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
				if ( !(jaxbMessageOrField.getValue() instanceof SignedInfoType) ) {
					getLogger().debug("\n"
									+ "\tDigest generated for XML reference element " + jaxbMessageOrField.getName().getLocalPart() + " with ID '" + id + "': " + ByteUtils.toHexString(digest) + "\n"
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
		
			getLogger().debug("EXI encoded SignedInfo: " + ByteUtils.toHexString(signedInfoElementExi));
			
			if (ecPrivateKey != null) {
				getLogger().debug("\n\tPrivate key used for creating signature: " + ByteUtils.toHexString(ecPrivateKey.getS().toByteArray()));
				
				ecdsa.initSign(ecPrivateKey);
				ecdsa.update(signedInfoElementExi);
				
				byte[] signature = ecdsa.sign();
				
				// Java operates on DER encoded signatures, but we must send the raw r and s values as signature 
				byte[] rawSignature = getRawSignatureFromDEREncoding(signature);
				
				getLogger().debug("Signature value: " + ByteUtils.toHexString(rawSignature));
				
				return rawSignature;
			} else {
				getLogger().error("Private key used to sign SignedInfo element is null");
				return null;
			}	
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | NoSuchProviderException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to create signature", e);
			return null;
		}
	}
	
	/**
	 * Verifies the signature given in the received header of an EVCC or SECC message
	 * 
	 * @param signature The received header's signature
	 * @param jaxbSignature The received header's signature, given as a JAXB element (needed for EXI operations)
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
			JAXBElement<SignedInfoType> jaxbSignature,
			HashMap<String, byte[]> verifyXMLSigRefElements, 
			byte[] verifyCert) {
		X509Certificate x509VerifyCert = getCertificate(verifyCert);
		return verifySignature(signature, jaxbSignature, verifyXMLSigRefElements, x509VerifyCert);
	}
	
	/**
	 * Verifies the signature given in the received header of an EVCC or SECC message
	 * 
	 * @param signature The received header's signature
	 * @param jaxbSignature The received header's signature, given as a JAXB element (needed for EXI operations)
	 * @param verifyXMLSigRefElements The HashMap of signature IDs and digest values of the message body 
	 * 		  or fields respectively of the received message (to cross-check against the XML reference
	 * 		  elements contained in the received message header)
	 * @param verifyCert The certificate holding the public key corresponding to the private key which was used for the signature
	 * @return True, if digest validation of all XML reference elements and signature validation was 
	 * 		   successful, false otherwise
	 */
	public static boolean verifySignature(
				SignatureType signature,
				JAXBElement<SignedInfoType> jaxbSignedInfo,
				HashMap<String, byte[]> verifyXMLSigRefElements, 
				X509Certificate verifyCert) {
		byte[] calculatedReferenceDigest; 
		boolean messageDigestsEqual;
		
		/*
		 * 1. step: 
		 * Iterate over all element IDs of the message which should have been signed and find the 
		 * respective Reference element in the given message header
		 */
		for (Map.Entry<String, byte[]> verifyXMLSigRefElement : verifyXMLSigRefElements.entrySet()) {
			String id = verifyXMLSigRefElement.getKey();
			getLogger().debug("Verifying digest for element '" + id + "'");
			messageDigestsEqual = false;
			calculatedReferenceDigest = verifyXMLSigRefElement.getValue();
			
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
			
			if (showSignatureVerificationLog) showSignatureVerificationLog(verifyCert, signature, jaxbSignedInfo, ecPublicKey);
			
			ecdsa = Signature.getInstance("SHA256withECDSA");
			// The Signature object needs to be initialized by setting it into the VERIFY state with the public key
			ecdsa.initVerify(ecPublicKey);
			
			// The data to be signed needs to be supplied to the Signature object
			byte[] exiEncodedSignedInfo = getExiCodec().getExiEncodedSignedInfo(jaxbSignedInfo);
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
	private static void showSignatureVerificationLog(
			X509Certificate verifyCert, 
			SignatureType signature, 
			JAXBElement<SignedInfoType> jaxbSignedInfo, 
			ECPublicKey ecPublicKey) {
		byte[] computedSignedInfoDigest = generateDigest("", jaxbSignedInfo);
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
	 * An ECDSA signature consists of two positive integers r and s, each of the bit length equal to the curve size.
	 * When Java is creating an ECDSA signature, it is encoding it in the DER (Distinguished Encoding Rules) format.
	 * But in ISO 15118, we do not expect DER encoded signatures. Thus, this function takes the DER encoded signature
	 * as input and returns the raw r and s integer values of the signature. 
	 * See further explanations in the @getDEREncodedSignature function for DER encoded ECDSA signatures.
	 * 
	 * @param derEncodedSignature The DER encoded signature as a result from java.security.Signature.sign()
	 * @return A byte array containing only the r and s value of the signature
	 */
	public static byte[] getRawSignatureFromDEREncoding(byte[] derEncodedSignature) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		
		// Length of r is encoded in the fourth byte 
		int lengthOfR = derEncodedSignature[3];
		
		// Length of r is encoded in the second byte AFTER r 
		int lengthOfS = derEncodedSignature[lengthOfR + 5];
		
		// Length of r and s are either 33 bytes (including padding byte 0x00), 32 bytes (normal), or less (leftmost 0x00 bytes were removed)
		try {
			if (lengthOfR == 33) System.arraycopy(derEncodedSignature, 5, r, 0, lengthOfR - 1); // skip leftmost padding byte 0x00
			else if (lengthOfR == 32) System.arraycopy(derEncodedSignature, 4, r, 0, lengthOfR);
			else System.arraycopy(derEncodedSignature, 4, r, 32 - lengthOfR, lengthOfR); // destPos = number of leftmost 0x00 bytes
			
			if (lengthOfS == 33) System.arraycopy(derEncodedSignature, lengthOfR + 7, s, 0, lengthOfS - 1); // skip leftmost padding byte 0x00
			else if (lengthOfS == 32) System.arraycopy(derEncodedSignature, lengthOfR + 6, s, 0, lengthOfS);
			else System.arraycopy(derEncodedSignature, lengthOfR + 6, s, 32 - lengthOfS, lengthOfS); // destPos = number of leftmost 0x00 bytes
		} catch (ArrayIndexOutOfBoundsException e) {
			getLogger().error("ArrayIndexOutOfBoundsException occurred while trying to get raw signature from DER encoded signature.", e);
		}
		
	    try {
			baos.write(r);
			baos.write(s);
		} catch (IOException e) {
			getLogger().error("IOException occurred while trying to write r and s into DER-encoded signature", e);
		}
	    
	    byte[] rawRAndS = baos.toByteArray();
	   
	    if (showSignatureVerificationLog) {
			StringBuilder sb = new StringBuilder();
			sb.append("Signature encoding DER -> raw:").append(System.lineSeparator());
			sb.append("\tDER: ").append(ByteUtils.toHexString(derEncodedSignature)).append(System.lineSeparator());
			sb.append("\tR: ").append(ByteUtils.toHexString(r)).append(System.lineSeparator());
			sb.append("\tS: ").append(ByteUtils.toHexString(s)).append(System.lineSeparator());
			sb.append("\tRaw: ").append(ByteUtils.toHexString(rawRAndS));
			getLogger().debug(sb.toString());
		}
	    
	    return rawRAndS;
	}
	
	
	/**
	 * When encoded in DER, the signature - holding the 
	 * x-coordinate of the elliptic curve point in the value "r"
	 * and the 
	 * y-coordinate of the elliptic curve point in the value "s" 
	 * - becomes the following sequence of bytes (in total somewhere between 68 and 72 bytes instead of 64 bytes):
	 *
	 * 0x30 len(z) 0x02 len(r) r 0x02 len(s) s
	 *	
	 * where:
	 *	
	 * - 0x30: is always the first byte of the DER encoded signature format (ASN.1 tag for sequence)
	 * 
	 * - len(z): is a single byte value, encoding the length in bytes of the sequence z (remaining list of bytes)
	 *   (from the first 0x02 to the end of the encoding); is a value between 0x43 and 0x46
	 *   
	 * - 0x02: is a fixed value indicating that an integer value will follow (ASN.1 tag for int)
	 * 
	 * - len(r): is a single byte value, encoding the length in bytes of r; 
	 *   Distinguished Encoding Rules (DER)-encoded integers are defined so that they can encode both positive and negative values  
	 *   (aka signed values). This means that the leftmost bit (aka most-significant bit in big-endian) indicates whether the value 
	 *   is positive (0) or negative (1).
	 *   For ECDSA, however, the r and s values are positive integers. So the leftmost bit must be a 0. If it's not, a 0x00  
	 *   padding byte must be added.
	 *   
	 *   Furthermore, DER require that integer values are represented in the shortest byte representation possible. This 
	 *   effectively prohibits the use of leading zeroes (0x00) if the leftmost bit was not set to 1.
	 *   
	 *   So len(r) will either be 0x21 (33 bytes), 0x20 (32 bytes) or less (mostly not less than 0x1F (31 bytes)).
	 *   Case 31 bytes or less: The leftmost bytes of the raw (non-DER-encoded) r are 0x00 and, according to DER, need to be 
	 *    			    			removed so that r is DER-encoded in the shortest possible way. Also, the leftmost bit of the 
	 *    						remaining byte array is 0 (-> a positive x-value).
	 *   Case 32 bytes: What we would normally expect, as the x- and y-coordinates are positive values of 32 bytes length. 
	 *   				The leftmost bit is set to 0 and the leftmost byte is not 0x00.
	 *   Case 33 bytes: A padding 0x00 byte was added as the most significant (leftmost) byte because the raw (non-DER-encoded) r
	 *   				 value had the leftmost bit set to 1, which would result in a negative value. 
	 *   
	 * - r: is the signed big-endian encoding of the value "r", of minimal length;
	 * 
	 * - 0x02: is a fixed value indicating that an integer value will follow (ASN.1 tag for int)
	 * 
	 * - len(s): is a single byte value, encoding the length in bytes of s;
	 *   (See further explanation of len(r) that applies as well for len(s))
	 *   
	 * - s: is the signed big-endian encoding of the value "s", of minimal length.
	 * 
	 * @param rawSignatureValue The r and s values (each 32 bytes) of an ECDSA signature, given as a byte array of 64 bytes
	 * @return A byte array representing the DER-encoded version of the raw r and s values
	 */
	private static byte[] getDEREncodedSignature (byte[] rawSignatureValue) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// First we separate x and y of coordinates into separate byte arrays r and s
		byte[] r = new byte[32];
		byte[] s = new byte[32];
		
		try {
			System.arraycopy(rawSignatureValue, 0, r, 0, 32);
			System.arraycopy(rawSignatureValue, 32, s, 0, 32);
		} catch (ArrayIndexOutOfBoundsException e) {
			getLogger().error("ArrayIndexOutOfBoundsException occurred while trying to get DER encoded signature", e);
			return new byte[0];
 		}
		
		// Then encode both parts (r & s) individually 
		byte[] rDerEncoded = getDerEncodedSignatureValue(r);
		byte[] sDerEncoded = getDerEncodedSignatureValue(s);
		
		// And write everything with the proper header to the buffer
		baos.write(0x30);
		baos.write(rDerEncoded.length + sDerEncoded.length);
		try {
			baos.write(rDerEncoded);
			baos.write(sDerEncoded);
		} catch (IOException e) {
			getLogger().error("IOException occurred while trying to write DER encoded signature r and s value", e);
		}
		
		byte[] derEncodedSignature = baos.toByteArray();
		
		try {
			baos.close();
		} catch (IOException e) {
			getLogger().error("IOException occurred while trying to close ByteArrayOutputStream", e);
		}
		
		if (showSignatureVerificationLog) {
			StringBuilder sb = new StringBuilder();
			sb.append("Signature encoding raw -> DER:").append(System.lineSeparator());
			sb.append("\tRaw: ").append(ByteUtils.toHexString(rawSignatureValue)).append(System.lineSeparator());
			sb.append("\tR: ").append(ByteUtils.toHexString(r)).append(System.lineSeparator());
			sb.append("\tR (DER-encoded): ").append(ByteUtils.toHexString(rDerEncoded)).append(System.lineSeparator());
			sb.append("\tS: ").append(ByteUtils.toHexString(s)).append(System.lineSeparator());
			sb.append("\tS (DER-encoded): ").append(ByteUtils.toHexString(sDerEncoded)).append(System.lineSeparator());
			sb.append("\tDER: ").append(ByteUtils.toHexString(derEncodedSignature));
			getLogger().debug(sb.toString());
 		}
		
		return derEncodedSignature;
	}
	
	
	/**
	* Helper function which provides a partial DER encoding for positive integer values used for r and s
	* 
	* @param value byte array containing a positive integer (non two's complement)
	* @return DER-encoded value of r or s (depending on the @param), including int content type, length and, if needed, padding 
	*/
	private static byte[] getDerEncodedSignatureValue(byte[] value) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Check if the value is negative which is equivalent to r[0] being bigger than 0x7f
		boolean isFillByteNeeded = value[0] < 0; 
		
		int indexOfFirstNonNullValue = 0;
		for (/* empty init statement */; indexOfFirstNonNullValue < value.length; indexOfFirstNonNullValue++) {
			if (value[indexOfFirstNonNullValue] != 0) {
				break;
			}
		}
		
		byte derEncodedLength = (byte) (value.length - indexOfFirstNonNullValue);
	    
	    baos.write(0x02);
    		if (isFillByteNeeded) {
    			baos.write(derEncodedLength + 1);
    			baos.write(0x00);
    		} else {
    			baos.write(derEncodedLength);
    		}
    		
    		baos.write(value, indexOfFirstNonNullValue, value.length - indexOfFirstNonNullValue);
    		byte[] result = baos.toByteArray();
    		
    		try {
    			baos.close();
    		} catch (IOException e) {
    			getLogger().error("IOException occurred while trying to close ByteArrayOutputStream", e);
    		}
	   
    		return result;
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
	
	/**
	 * Checks the syntax of the EMAID according to Annex H.1 of ISO 15118-2
	 * 
	 * @param certChain The contract certificate chain. The EMAID is read from the contract certificate's common name
	 * @return True, if the syntax is valid, false otherwise
	 */
	public static boolean isEMAIDSyntaxValid(X509Certificate contractCertificate) {
		String emaid = getEMAID(contractCertificate).getValue().toUpperCase();
		
		if (emaid.length() < 14 || emaid.length() > 18) {
			getLogger().error("EMAID is invalid. Its length (" + emaid.length() + ") mus be between "
							+ "14 (min, excluding separators) and 18 (max, including separators)");
			return false;
		}
		
		String emaidWithoutSeparator = emaid.replace("-", "");
		
		// Check country code
		if (Character.isDigit(emaidWithoutSeparator.charAt(0)) || Character.isDigit(emaidWithoutSeparator.charAt(1))) {
			getLogger().error("EMAID (" + emaid + ") is invalid, the first two characters must not be a digit");
			return false;
		}
		
		// Check provider ID
		if (! (Character.isLetterOrDigit(emaidWithoutSeparator.charAt(2)) && 
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(3)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(4))) )  {
			getLogger().error("EMAID (" + emaid + ") is invalid, the provider ID must be alpha-numerical");
			return false;
		}
		
		// Check emaInstance
		if (! (Character.isLetterOrDigit(emaidWithoutSeparator.charAt(5)) && 
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(6)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(7)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(8)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(9)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(10)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(11)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(12)) &&
			   Character.isLetterOrDigit(emaidWithoutSeparator.charAt(13))) )  {
			getLogger().error("EMAID (" + emaid + ") is invalid, the eMA instance must be alpha-numerical");
			return false;
		}
		
		return true;
	}
	
	public static void setExiCodec(ExiCodec exiCodecChoice) {
		exiCodec = exiCodecChoice;
	}
	
	private static ExiCodec getExiCodec() {
		return exiCodec;
	}
}
