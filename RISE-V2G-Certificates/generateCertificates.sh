#*******************************************************************************
#  Copyright (c) 2016 Dr.-Ing. Marc Mültin.
#  All rights reserved. This program and the accompanying materials
#  are made available under the terms of the Eclipse Public License v1.0
#  which accompanies this distribution, and is available at
#  http://www.eclipse.org/legal/epl-v10.html
#
#  Contributors:
#    Dr.-Ing. Marc Mültin - initial API and implementation and initial documentation
#*******************************************************************************
# This shell script can be used to create all necessary certificates and keystores needed in order to
# - successfully perform a TLS handshake between the EVCC (TLSClient) and the SECC (TLSServer) and 
# - install/update a contract certificate in the EVCC.
# Previously created certificates should have been provided with the respective release of the RISE V2G project for testing purposes. However, certain certificates might not be valid any more in which case you need to create new certificates. 
# This file shall serve you with all information needed to create your own certificate chains.
#
# Helpful information about using OpenSSL is provided by Ivan Ristic's book "Bulletproof SSL and TLS".
# Furthermore, you should have OpenSSL 1.0.2 (or above) installed to comply with all security requirements imposed by ISO 15118. For example, OpenSSL 0.9.8 does not come with SHA-2 for SHA-256 signature algorithms.
#
# Author: Marc Mültin (marc.mueltin@chargepartner.com) 

# 0) Create directories if not yet existing
mkdir -p certs
mkdir -p csrs
mkdir -p keystores
mkdir -p privateKeys

# 1) Create a self-signed V2GRootCA certificate
# 1.1) Create a 
#	- private key -> -genkey
#	- with elliptic curve parameters -> ecparam
#	- for key of length 256 bit to be used for digital signatures -> -name secp256r1
#	- with symmetric encryption AES 128 bit -> -aes128
#   - and the passphrase for the private key provided in a file -> -passout file:passphrase.txt
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/v2gRootCA.key -aes128 -passout file:passphrase.txt
# 1.2) Create a 
#	- new -> -new
# 	- self-signed certificate -> -new -x509 (and -out v2gRootCA.pem)
#	- valid for 40 years -> -days 14600
#	- with signature algorithm sha256 -> -sha256
#	- with previously created private key -> -key privateKeys/v2gRootCA.key
#	- and configuration data provided -> -config configs/v2gRootCA.cnf
#	- with extensions specified in section [ext] -> -extensions ext
openssl req -new -x509 -days 14600 -sha256 -key privateKeys/v2gRootCA.key -set_serial 01 -passin file:passphrase.txt -config configs/v2gRootCA.cnf -extensions ext -out certs/v2gRootCA.pem


# 2) Create an intermediate CPO sub-CA certificate which is directly signed by the V2GRootCA certificate
# 2.1) Create a private key (same procedure as for V2GRootCA certificate)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/cpoSub1CA.key -aes128 -passout file:passphrase.txt
# 2.2) Create a 
# 	- new Certificate Signing Request (CSR) -> -new (and -out cpoSub1CA.csr)
#	- with previously created private key -> -key privateKeys/cpoSub1CA.key
#	- and configuration data provided -> -config configs/cpoSub1CA.cnf
#	- with extensions specified in section [ext] -> -extensions ext
openssl req -new -key privateKeys/cpoSub1CA.key -passin file:passphrase.txt -config configs/cpoSub1CA.cnf -extensions ext -out csrs/cpoSub1CA.csr
# 2.3) Create a 
#	- certificate for the CPOSub1CA -> x509
#	- with the previously created CSR -> -in csrs/cpoSub1CA.csr
#	- signed by the V2GRootCA's private key -> -signkey privateKeys/v2gRootCA.key
#	- with a validity of 4 years -> -days 1460
openssl x509 -req -in csrs/cpoSub1CA.csr -extfile configs/cpoSub1CA.cnf -extensions ext -CA certs/v2gRootCA.pem -CAkey privateKeys/v2gRootCA.key -set_serial 02 -passin file:passphrase.txt -days 1460 -out certs/cpoSub1CA.pem


# 3) Create a second intermediate CPO sub-CA certificate  just the way the previous intermedia certificate was created which is directly signed by the CPOSub1CA
# Differences to CPOSub1CA
# - basicConstraints in config file sets pathlength to 0 (meaning that no further sub CA's certificate may be signed with this certificate, a leaf certificate must follow this certificate in a certificate chain)
# - validity is set to 1 year (1 - 2 years are allowed according to ISO 15118)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/cpoSub2CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/cpoSub2CA.key -passin file:passphrase.txt -config configs/cpoSub2CA.cnf -extensions ext -out csrs/cpoSub2CA.csr
openssl x509 -req -in csrs/cpoSub2CA.csr -extfile configs/cpoSub2CA.cnf -extensions ext -CA certs/cpoSub1CA.pem -CAkey privateKeys/cpoSub1CA.key -set_serial 03 -passin file:passphrase.txt -days 365 -out certs/cpoSub2CA.pem


# 4) Create an SECCCert certificate which is the leaf certificate belonging to the charging station which authenticates itself to the EVCC during a TLS handshake, signed by CPOSub2CA certificate
# Differences to CPOSub1CA and CPOSub2CA
# - basicConstraints sets CA to false, no pathlen is therefore set
# - keyusage is set to digitalSignature instead of keyCertSign and cRLSign
# - validity is set to 60 days (2 - 3 months are allowed according to ISO 15118)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/seccCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/seccCert.key -passin file:passphrase.txt -config configs/seccCert.cnf -extensions ext -out csrs/seccCert.csr
openssl x509 -req -in csrs/seccCert.csr -extfile configs/seccCert.cnf -extensions ext -CA certs/cpoSub2CA.pem -CAkey privateKeys/cpoSub2CA.key -set_serial 04 -passin file:passphrase.txt -days 60 -out certs/seccCert.pem
# Concatenate the intermediate CAs into one file intermediateCAs.pem
# IMPORTANT: Concatenate in such a way that the chain leads from the leaf certificate to the root (excluding), this means here: first parameter of the cat command is the intermediate CA's certificate which signs the leaf certificate (in this case cpoSub2CA.pem). Otherwise the Java method getCertificateChain() which is called on the keystore will only return the leaf certificate!
cat certs/cpoSub2CA.pem certs/cpoSub1CA.pem > certs/intermediateCPOCAs.pem
# Put the seccCertificate, the private key of the seccCertificate as well as the intermediate CAs in a pkcs12 container. 
# IMPORTANT: It is necessary to put all necessary intermediate CAs directly into the PKCS12 container (with the -certfile switch), instead of later on mporting the PKCS12 containter only holding the leaf certificate (seccCert) and its private key and additionally importing the intermediate CAs via the keytool command (TLS handshake will fail).
# This is the reason why we need two password files (passphrase.txt and passphrase2.txt). Possibly the passphrase.txt file resource is locked before being accessed a second time within the same command? See also http://rt.openssl.org/Ticket/Display.html?id=3168&user=guest&pass=guest
# The -name switch corresponds to the -alias switch in the keytool command later on
openssl pkcs12 -export -inkey privateKeys/seccCert.key -in certs/seccCert.pem -certfile certs/intermediateCPOCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name secc_cert -out certs/seccCert.p12


# 5) Create a self-signed OEMRootCA certificate (validity is up to the OEM, this example applies the same validity as the V2GRootCA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/oemRootCA.key -aes128 -passout file:passphrase.txt
openssl req -new -x509 -days 14600 -sha256 -key privateKeys/oemRootCA.key -set_serial 05 -passin file:passphrase.txt -config configs/oemRootCA.cnf -extensions ext -out certs/oemRootCA.pem


# 6) Create an intermediate OEM sub-CA certificate which is directly signed by the OEMRootCA certificate (validity is up to the OEM, this example applies the same validity as the CPOSub1CA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/oemSub1CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/oemSub1CA.key -passin file:passphrase.txt -config configs/oemSub1CA.cnf -extensions ext -out csrs/oemSub1CA.csr
openssl x509 -req -in csrs/oemSub1CA.csr -extfile configs/oemSub1CA.cnf -extensions ext -CA certs/oemRootCA.pem -CAkey privateKeys/oemRootCA.key -set_serial 06 -passin file:passphrase.txt -days 1460 -out certs/oemSub1CA.pem


# 7) Create a second intermediate OEM sub-CA certificate which is directly signed by the OEMSub1CA certificate (validity is up to the OEM, this example applies the same validity as the CPOSub2CA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/oemSub2CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/oemSub2CA.key -passin file:passphrase.txt -config configs/oemSub2CA.cnf -extensions ext -out csrs/oemSub2CA.csr
openssl x509 -req -in csrs/oemSub2CA.csr -extfile configs/oemSub2CA.cnf -extensions ext -CA certs/oemSub1CA.pem -CAkey privateKeys/oemSub1CA.key -set_serial 07 -passin file:passphrase.txt -days 1460 -out certs/oemSub2CA.pem


# 8) Create an OEM provisioning certificate which is the leaf certificate belonging to the OEM certificate chain (used for contract certificate installation)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/oemProvCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/oemProvCert.key -passin file:passphrase.txt -config configs/oemProvCert.cnf -extensions ext -out csrs/oemProvCert.csr
openssl x509 -req -in csrs/oemProvCert.csr -extfile configs/oemProvCert.cnf -extensions ext -CA certs/oemSub2CA.pem -CAkey privateKeys/oemSub2CA.key -set_serial 08 -passin file:passphrase.txt -days 60 -out certs/oemProvCert.pem
cat certs/oemSub2CA.pem certs/oemSub1CA.pem > certs/intermediateOEMCAs.pem
openssl pkcs12 -export -inkey privateKeys/oemProvCert.key -in certs/oemProvCert.pem -certfile certs/intermediateOEMCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name oem_prov_cert -out certs/oemProvCert.p12


# 9) Create a self-signed MORootCA (mobility operator) certificate (validity is up to the MO, this example applies the same validity as the V2GRootCA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/moRootCA.key -aes128 -passout file:passphrase.txt
openssl req -new -x509 -days 14600 -sha256 -key privateKeys/moRootCA.key -set_serial 09 -passin file:passphrase.txt -config configs/moRootCA.cnf -extensions ext -out certs/moRootCA.pem


# 10) Create an intermediate MO sub-CA certificate which is directly signed by the MORootCA certificate (validity is up to the MO, this example applies the same validity as the CPOSub1CA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/moSub1CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/moSub1CA.key -passin file:passphrase.txt -config configs/moSub1CA.cnf -extensions ext -out csrs/moSub1CA.csr
openssl x509 -req -in csrs/moSub1CA.csr -extfile configs/moSub1CA.cnf -extensions ext -CA certs/moRootCA.pem -CAkey privateKeys/moRootCA.key -set_serial 10 -passin file:passphrase.txt -days 1460 -out certs/moSub1CA.pem


# 11) Create a second intermediate MO sub-CA certificate which is directly signed by the MOSub1CA certificate (validity is up to the MO, this example applies the same validity as the CPOSub2CA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/moSub2CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/moSub2CA.key -passin file:passphrase.txt -config configs/moSub2CA.cnf -extensions ext -out csrs/moSub2CA.csr
openssl x509 -req -in csrs/moSub2CA.csr -extfile configs/moSub2CA.cnf -extensions ext -CA certs/moSub1CA.pem -CAkey privateKeys/moSub1CA.key -set_serial 11 -passin file:passphrase.txt -days 1460 -out certs/moSub2CA.pem


# 12) Create a contract certificate which is the leaf certificate belonging to the MO certificate chain (used for contract certificate installation)
# Validity can be between 4 weeks and 2 years (restricted by the contract lifetime), for testing purposes the validity will be set to 2 years
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/contractCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/contractCert.key -passin file:passphrase.txt -config configs/contractCert.cnf -extensions ext -out csrs/contractCert.csr
openssl x509 -req -in csrs/contractCert.csr -extfile configs/contractCert.cnf -extensions ext -CA certs/moSub2CA.pem -CAkey privateKeys/moSub2CA.key -set_serial 12 -passin file:passphrase.txt -days 730 -out certs/contractCert.pem
cat certs/moSub2CA.pem certs/moSub1CA.pem > certs/intermediateMOCAs.pem
openssl pkcs12 -export -inkey privateKeys/contractCert.key -in certs/contractCert.pem -certfile certs/intermediateMOCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name contract_cert -out certs/contractCert.p12


# 13) Create an intermediate provisioning service sub-CA certificate which is directly signed by the V2GRootCA certificate 
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/provSub1CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/provSub1CA.key -passin file:passphrase.txt -config configs/provSub1CA.cnf -extensions ext -out csrs/provSub1CA.csr
openssl x509 -req -in csrs/provSub1CA.csr -extfile configs/provSub1CA.cnf -extensions ext -CA certs/v2gRootCA.pem -CAkey privateKeys/v2gRootCA.key -set_serial 13 -passin file:passphrase.txt -days 1460 -out certs/provSub1CA.pem


# 14) Create a second intermediate provisioning sub-CA certificate which is directly signed by the ProvSub1CA certificate (validity 1 - 2 years, we make it 2 years)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/provSub2CA.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/provSub2CA.key -passin file:passphrase.txt -config configs/provSub2CA.cnf -extensions ext -out csrs/provSub2CA.csr
openssl x509 -req -in csrs/provSub2CA.csr -extfile configs/provSub2CA.cnf -extensions ext -CA certs/provSub1CA.pem -CAkey privateKeys/provSub1CA.key -set_serial 14 -passin file:passphrase.txt -days 730 -out certs/provSub2CA.pem


# 15) Create a provisioning service certificate which is the leaf certificate belonging to the provisioning certificate chain (used for contract certificate installation)
# Validity can be between 2 - 3 months, we make it 3 months
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys/provServiceCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys/provServiceCert.key -passin file:passphrase.txt -config configs/provServiceCert.cnf -extensions ext -out csrs/provServiceCert.csr
openssl x509 -req -in csrs/provServiceCert.csr -extfile configs/provServiceCert.cnf -extensions ext -CA certs/provSub2CA.pem -CAkey privateKeys/provSub2CA.key -set_serial 15 -passin file:passphrase.txt -days 90 -out certs/provServiceCert.pem
cat certs/provSub2CA.pem certs/provSub1CA.pem > certs/intermediateProvCAs.pem
openssl pkcs12 -export -inkey privateKeys/provServiceCert.key -in certs/provServiceCert.pem -certfile certs/intermediateProvCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name prov_service_cert -out certs/provServiceCert.p12


# XX) Finally we need to convert the certificates from PEM format to DER format (PEM is the default format, but ISO 15118 only allows DER format)
openssl x509 -inform PEM -in certs/v2gRootCA.pem -outform DER -out certs/v2gRootCA.crt
openssl x509 -inform PEM -in certs/oemRootCA.pem -outform DER -out certs/oemRootCA.crt
openssl x509 -inform PEM -in certs/moRootCA.pem -outform DER -out certs/moRootCA.crt
# Since the intermediate certificates need to be in PEM format when putting them in a PKCS12 container and the resulting PKCS12 file is a binary format, it might be sufficient. Otherwise, I have currently no idea how to covert the intermediate certificates in DER without running into problems when creating the PKCS12 container.


# XX) Create the initial Java truststores and keystores
# XX.1) truststore for the EVCC which needs to hold the V2GRootCA certificate (the EVCC does not verify the received certificate chain, therefore no MORootCA needs to be imported in evccTruststore.jks )
keytool -import -keystore keystores/evccTruststore.jks -alias v2g_root_ca -file certs/v2gRootCA.crt -storepass:file passphrase.txt -noprompt
# XX.2) truststore for the SECC which needs to hold the V2GRootCA certificate and the MORootCA which signed the MOSub1CA (needed for verifying the  contract certificate signature chain which will be sent from the EVCC to the SECC with PaymentDetailsReq message). According to ISO 15118-2, MORootCA is not necessarily needed as the MOSub1CA could instead be signed by a V2GRootCA.
keytool -import -keystore keystores/seccTruststore.jks -alias v2g_root_ca -file certs/v2gRootCA.crt -storepass:file passphrase.txt -noprompt
keytool -import -keystore keystores/seccTruststore.jks -alias mo_root_ca -file certs/moRootCA.crt -storepass:file passphrase.txt -noprompt
# XX.3) keystore for the SECC which needs to hold the CPOSub1CA, CPOSub1CA and SECCCert certificates
keytool -importkeystore -srckeystore certs/seccCert.p12 -srcstoretype pkcs12 -srcstorepass:file passphrase.txt -srcalias secc_cert -destalias secc_cert -destkeystore keystores/seccKeystore.jks -storepass:file passphrase.txt -noprompt
# XX.4) keystore for the EVCC which needs to hold the OEMSub1CA, OEMSub2CA and OEMProvCert certificates
keytool -importkeystore -srckeystore certs/oemProvCert.p12 -srcstoretype pkcs12 -srcstorepass:file passphrase.txt -srcalias oem_prov_cert -destalias oem_prov_cert -destkeystore keystores/evccKeystore.jks -storepass:file passphrase.txt -noprompt


# Side notes for OCSP stapling in Java: see http://openjdk.java.net/jeps/8046321
