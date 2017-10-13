@echo off
REM *******************************************************************************
REM The MIT License (MIT)
REM 
REM Copyright (c) 2015-2017  V2G Clarity (Dr. Marc Mültin) 
REM
REM Permission is hereby granted, free of charge, to any person obtaining a copy
REM of this software and associated documentation files (the "Software"), to deal
REM in the Software without restriction, including without limitation the rights
REM to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
REM copies of the Software, and to permit persons to whom the Software is
REM furnished to do so, subject to the following conditions:
REM
REM The above copyright notice and this permission notice shall be included in
REM all copies or substantial portions of the Software.
REM
REM THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
REM IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
REM FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
REM AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
REM LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
REM OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
REM THE SOFTWARE.
REM *******************************************************************************

REM ===========================================================================================================
REM This shell script can be used to create all necessary certificates and keystores needed in order to
REM - successfully perform a TLS handshake between the EVCC (TLSClient) and the SECC (TLSServer) and 
REM - install/update a contract certificate in the EVCC.
REM 
REM This file shall serve you with all information needed to create your own certificate chains.
REM 
REM Helpful information about using openssl is provided by Ivan Ristic's book "Bulletproof SSL and TLS".
REM Furthermore, you should have openssl 1.0.2 (or above) installed to comply with all security requirements 
REM imposed by ISO 15118. For example, openssl 0.9.8 does not come with SHA-2 for SHA-256 signature algorithms. 
REM 
REM Author: Dr. Marc Mültin (marc.mueltin@v2g-clarity.com) 
REM ===========================================================================================================


REM Some variables to create different outcomes of the PKI for testing purposes. Change the validity periods (given in number of days) to test 
REM - valid certificates (e.g. contract certificate or Sub-CA certificate)
REM - expired certificates (e.g. contract certificate or Sub-CA certificates) -> you need to reset your system time to the past to create expired certificates
REM - a to be updated contract certificate
SET validity_contract_cert=730
SET validity_mo_subca1_cert=1460
SET validity_mo_subca2_cert=1460
SET validity_oem_prov_cert=1460
SET validity_oem_subca1_cert=1460
SET validity_oem_subca2_cert=1460
SET validity_cps_leaf_cert=90
SET validity_cps_subca1_cert=1460
SET validity_cps_subca2_cert=730
SET validity_secc_cert=60
SET validity_cpo_subca1_cert=1460
SET validity_cpo_subca2_cert=365
SET validity_v2g_root_cert=3650
SET validity_oem_root_cert=3650
SET validity_mo_root_cert=3650


REM 0) Create directories if not yet existing. The keystores in the keystores folder (if existing) need to be deleted at first, so delete the complete folder.
if exist keystores rd /s /q keystores 
if not exist certs mkdir certs
if not exist csrs mkdir csrs
if not exist keystores mkdir keystores
if not exist privateKeys mkdir privateKeys

REM 1) Create a self-signed V2GRootCA certificate
REM 1.1) Create a 
REM	- private key -> -genkey
REM	- with elliptic curve parameters -> ecparam
REM	- for key of length 256 bit to be used for digital signatures -> -name secp256r1
REM	- with symmetric encryption AES 128 bit -> -aes128
REM   - and the passphrase for the private key provided in a file -> -passout file:passphrase.txt
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\v2gRootCA.key -aes128 -passout file:passphrase.txt
REM 1.2) Create a 
REM	- new -> -new
REM 	- self-signed certificate -> -new -x509 (and -out v2gRootCA.pem)
REM	- valid for 40 years -> -days 14600
REM	- with signature algorithm sha256 -> -sha256
REM	- with previously created private key -> -key privateKeys\v2gRootCA.key
REM	- and configuration data provided -> -config configs\v2gRootCACert.cnf
REM	- with extensions specified in section [ext] -> -extensions ext
openssl req -new -x509 -days %validity_v2g_root_cert% -sha256 -key privateKeys\v2gRootCA.key -set_serial 01 -passin file:passphrase.txt -config configs\v2gRootCACert.cnf -extensions ext -out certs\v2gRootCA.pem


REM 2) Create an intermediate CPO sub-CA certificate which is directly signed by the V2GRootCA certificate
REM 2.1) Create a private key (same procedure as for V2GRootCA certificate)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\cpoSubCA1.key -aes128 -passout file:passphrase.txt
REM 2.2) Create a 
REM 	- new Certificate Signing Request (CSR) -> -new (and -out cpoSubCA1.csr)
REM	- with previously created private key -> -key privateKeys\cpoSubCA1.key
REM	- and configuration data provided -> -config configs\cpoSubCA1Cert.cnf
REM	- with extensions specified in section [ext] -> -extensions ext
openssl req -new -key privateKeys\cpoSubCA1.key -passin file:passphrase.txt -config configs\cpoSubCA1Cert.cnf -extensions ext -out csrs\cpoSubCA1.csr
REM 2.3) Create a 
REM	- certificate for the CPOSubCA1 -> x509
REM	- with the previously created CSR -> -in csrs\cpoSubCA1.csr
REM	- signed by the V2GRootCA's private key -> -signkey privateKeys\v2gRootCA.key
REM	- with a validity of 4 years -> -days 1460
openssl x509 -req -in csrs\cpoSubCA1.csr -extfile configs\cpoSubCA1Cert.cnf -extensions ext -CA certs\v2gRootCA.pem -CAkey privateKeys\v2gRootCA.key -set_serial 02 -passin file:passphrase.txt -days %validity_cpo_subca1_cert% -out certs\cpoSubCA1.pem


REM 3) Create a second intermediate CPO sub-CA certificate  just the way the previous intermedia certificate was created which is directly signed by the CPOSubCA1
REM Differences to CPOSubCA1
REM - basicConstraints in config file sets pathlength to 0 (meaning that no further sub CA's certificate may be signed with this certificate, a leaf certificate must follow this certificate in a certificate chain)
REM - validity is set to 1 year (1 - 2 years are allowed according to ISO 15118)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\cpoSubCA2.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\cpoSubCA2.key -passin file:passphrase.txt -config configs\cpoSubCA2Cert.cnf -extensions ext -out csrs\cpoSubCA2.csr
openssl x509 -req -in csrs\cpoSubCA2.csr -extfile configs\cpoSubCA2Cert.cnf -extensions ext -CA certs\cpoSubCA1.pem -CAkey privateKeys\cpoSubCA1.key -set_serial 03 -passin file:passphrase.txt -days %validity_cpo_subca2_cert% -out certs\cpoSubCA2.pem

REM 4) Create an SECCCert certificate which is the leaf certificate belonging to the charging station which authenticates itself to the EVCC during a TLS handshake, signed by CPOSubCA2 certificate
REM Differences to CPOSubCA1 and CPOSubCA2
REM - basicConstraints sets CA to false, no pathlen is therefore set
REM - keyusage is set to digitalSignature instead of keyCertSign and cRLSign
REM - validity is set to 60 days (2 - 3 months are allowed according to ISO 15118)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\seccCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\seccCert.key -passin file:passphrase.txt -config configs\seccCert.cnf -extensions ext -out csrs\seccCert.csr
openssl x509 -req -in csrs\seccCert.csr -extfile configs\seccCert.cnf -extensions ext -CA certs\cpoSubCA2.pem -CAkey privateKeys\cpoSubCA2.key -set_serial 04 -passin file:passphrase.txt -days %validity_secc_cert% -out certs\seccCert.pem
REM Concatenate the intermediate CAs into one file intermediateCAs.pem
REM IMPORTANT: Concatenate in such a way that the chain leads from the leaf certificate to the root (excluding), this means here: first parameter of the type command is the intermediate CA's certificate which signs the leaf certificate (in this case cpoSubCA2.pem). Otherwise the Java method getCertificateChain() which is called on the keystore will only return the leaf certificate!
type certs\cpoSubCA2.pem certs\cpoSubCA1.pem > certs\intermediateCPOCAs.pem
REM Put the seccCertificate, the private key of the seccCertificate as well as the intermediate CAs in a pkcs12 container. 
REM IMPORTANT: It is necessary to put all necessary intermediate CAs directly into the PKCS12 container (with the -certfile switch), instead of later on iporting the PKCS12 containter only holding the leaf certificate (seccCert) and its private key and additionally importing the intermediate CAs via the keytool command (TLS handshake will fail).
REM This is the reason why we need two password files (passphrase.txt and passphrase2.txt). Possibly the passphrase.txt file resource is locked before being accessed a second time within the same command? See also http://rt.openssl.org/Ticket/Display.html?id=3168&user=guest&pass=guest
REM The -name switch corresponds to the -alias switch in the keytool command later on
openssl pkcs12 -export -inkey privateKeys\seccCert.key -in certs\seccCert.pem -certfile certs\intermediateCPOCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name secc_cert -out certs\cpoCertChain.p12

REM 5) Create a self-signed OEMRootCA certificate (validity is up to the OEM, this example applies the same validity as the V2GRootCA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\oemRootCA.key -aes128 -passout file:passphrase.txt
openssl req -new -x509 -days %validity_oem_root_cert% -sha256 -key privateKeys\oemRootCA.key -set_serial 05 -passin file:passphrase.txt -config configs\oemRootCACert.cnf -extensions ext -out certs\oemRootCA.pem

REM 6) Create an intermediate OEM sub-CA certificate which is directly signed by the OEMRootCA certificate (validity is up to the OEM, this example applies the same validity as the CPOSubCA1)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\oemSubCA1.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\oemSubCA1.key -passin file:passphrase.txt -config configs\oemSubCA1Cert.cnf -extensions ext -out csrs\oemSubCA1.csr
openssl x509 -req -in csrs\oemSubCA1.csr -extfile configs\oemSubCA1Cert.cnf -extensions ext -CA certs\oemRootCA.pem -CAkey privateKeys\oemRootCA.key -set_serial 06 -passin file:passphrase.txt -days %validity_oem_subca1_cert% -out certs\oemSubCA1.pem

REM 7) Create a second intermediate OEM sub-CA certificate which is directly signed by the OEMSubCA1 certificate (validity is up to the OEM, this example applies the same validity as the CPOSubCA2)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\oemSubCA2.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\oemSubCA2.key -passin file:passphrase.txt -config configs\oemSubCA2Cert.cnf -extensions ext -out csrs\oemSubCA2.csr
openssl x509 -req -in csrs\oemSubCA2.csr -extfile configs\oemSubCA2Cert.cnf -extensions ext -CA certs\oemSubCA1.pem -CAkey privateKeys\oemSubCA1.key -set_serial 07 -passin file:passphrase.txt -days %validity_oem_subca2_cert% -out certs\oemSubCA2.pem

REM 8) Create an OEM provisioning certificate which is the leaf certificate belonging to the OEM certificate chain (used for contract certificate installation)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\oemProvCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\oemProvCert.key -passin file:passphrase.txt -config configs\oemProvCert.cnf -extensions ext -out csrs\oemProvCert.csr
openssl x509 -req -in csrs\oemProvCert.csr -extfile configs\oemProvCert.cnf -extensions ext -CA certs\oemSubCA2.pem -CAkey privateKeys\oemSubCA2.key -set_serial 08 -passin file:passphrase.txt -days %validity_oem_prov_cert% -out certs\oemProvCert.pem
type certs\oemSubCA2.pem certs\oemSubCA1.pem > certs\intermediateOEMCAs.pem
openssl pkcs12 -export -inkey privateKeys\oemProvCert.key -in certs\oemProvCert.pem -certfile certs\intermediateOEMCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name oem_prov_cert -out certs\oemCertChain.p12

REM 9) Create a self-signed MORootCA (mobility operator) certificate (validity is up to the MO, this example applies the same validity as the V2GRootCA)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\moRootCA.key -aes128 -passout file:passphrase.txt
openssl req -new -x509 -days %validity_mo_root_cert% -sha256 -key privateKeys\moRootCA.key -set_serial 09 -passin file:passphrase.txt -config configs\moRootCACert.cnf -extensions ext -out certs\moRootCA.pem

REM 10) Create an intermediate MO sub-CA certificate which is directly signed by the MORootCA certificate (validity is up to the MO, this example applies the same validity as the CPOSubCA1)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\moSubCA1.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\moSubCA1.key -passin file:passphrase.txt -config configs\moSubCA1Cert.cnf -extensions ext -out csrs\moSubCA1.csr
openssl x509 -req -in csrs\moSubCA1.csr -extfile configs\moSubCA1Cert.cnf -extensions ext -CA certs\moRootCA.pem -CAkey privateKeys\moRootCA.key -set_serial 10 -passin file:passphrase.txt -days %validity_mo_subca1_cert% -out certs\moSubCA1.pem


REM 11) Create a second intermediate MO sub-CA certificate which is directly signed by the MOSubCA1 certificate (validity is up to the MO, this example applies the same validity as the CPOSubCA2)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\moSubCA2.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\moSubCA2.key -passin file:passphrase.txt -config configs\moSubCA2Cert.cnf -extensions ext -out csrs\moSubCA2.csr
openssl x509 -req -in csrs\moSubCA2.csr -extfile configs\moSubCA2Cert.cnf -extensions ext -CA certs\moSubCA1.pem -CAkey privateKeys\moSubCA1.key -set_serial 11 -passin file:passphrase.txt -days %validity_mo_subca2_cert% -out certs\moSubCA2.pem


REM 12) Create a contract certificate which is the leaf certificate belonging to the MO certificate chain (used for contract certificate installation)
REM Validity can be between 4 weeks and 2 years (restricted by the contract lifetime), for testing purposes the validity will be set to 2 years
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\contractCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\contractCert.key -passin file:passphrase.txt -config configs\contractCert.cnf -extensions ext -out csrs\contractCert.csr
openssl x509 -req -in csrs\contractCert.csr -extfile configs\contractCert.cnf -extensions ext -CA certs\moSubCA2.pem -CAkey privateKeys\moSubCA2.key -set_serial 12 -passin file:passphrase.txt -days %validity_contract_cert% -out certs\contractCert.pem
type certs\moSubCA2.pem certs\moSubCA1.pem > certs\intermediateMOCAs.pem
openssl pkcs12 -export -inkey privateKeys\contractCert.key -in certs\contractCert.pem -certfile certs\intermediateMOCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name contract_cert -out certs\moCertChain.p12

REM 13) Create an intermediate provisioning service sub-CA certificate which is directly signed by the V2GRootCA certificate 
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\cpsSubCA1.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\cpsSubCA1.key -passin file:passphrase.txt -config configs\cpsSubCA1Cert.cnf -extensions ext -out csrs\cpsSubCA1.csr
openssl x509 -req -in csrs\cpsSubCA1.csr -extfile configs\cpsSubCA1Cert.cnf -extensions ext -CA certs\v2gRootCA.pem -CAkey privateKeys\v2gRootCA.key -set_serial 13 -passin file:passphrase.txt -days %validity_cps_subca1_cert% -out certs\cpsSubCA1.pem

REM 14) Create a second intermediate provisioning sub-CA certificate which is directly signed by the CPSSubCA1 certificate (validity 1 - 2 years, we make it 2 years)
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\cpsSubCA2.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\cpsSubCA2.key -passin file:passphrase.txt -config configs\cpsSubCA2Cert.cnf -extensions ext -out csrs\cpsSubCA2.csr
openssl x509 -req -in csrs\cpsSubCA2.csr -extfile configs\cpsSubCA2Cert.cnf -extensions ext -CA certs\cpsSubCA1.pem -CAkey privateKeys\cpsSubCA1.key -set_serial 14 -passin file:passphrase.txt -days %validity_cps_subca2_cert% -out certs\cpsSubCA2.pem

REM 15) Create a provisioning service certificate which is the leaf certificate belonging to the provisioning certificate chain (used for contract certificate installation)
REM Validity can be between 2 - 3 months, we make it 3 months
openssl ecparam -genkey -name secp256r1 | openssl ec -out privateKeys\cpsLeafCert.key -aes128 -passout file:passphrase.txt
openssl req -new -key privateKeys\cpsLeafCert.key -passin file:passphrase.txt -config configs\cpsLeafCert.cnf -extensions ext -out csrs\cpsLeafCert.csr
openssl x509 -req -in csrs\cpsLeafCert.csr -extfile configs\cpsLeafCert.cnf -extensions ext -CA certs\cpsSubCA2.pem -CAkey privateKeys\cpsSubCA2.key -set_serial 15 -passin file:passphrase.txt -days %validity_cps_leaf_cert% -out certs\cpsLeafCert.pem
type certs\cpsSubCA2.pem certs\cpsSubCA1.pem > certs\intermediateCPSCAs.pem
openssl pkcs12 -export -inkey privateKeys\cpsLeafCert.key -in certs\cpsLeafCert.pem -certfile certs\intermediateCPSCAs.pem -aes128 -passin file:passphrase.txt -passout file:passphrase2.txt -name cps_leaf_cert -out certs\cpsCertChain.p12

REM 16) Finally we need to convert the certificates from PEM format to DER format (PEM is the default format, but ISO 15118 only allows DER format)
openssl x509 -inform PEM -in certs\v2gRootCA.pem       -outform DER -out certs\v2gRootCA.der
openssl x509 -inform PEM -in certs\cpsSubCA1.pem      -outform DER -out certs\cpsSubCA1.der
openssl x509 -inform PEM -in certs\cpsSubCA2.pem      -outform DER -out certs\cpsSubCA2.der
openssl x509 -inform PEM -in certs\cpsLeafCert.pem -outform DER -out certs\cpsLeafCert.der
openssl x509 -inform PEM -in certs\cpoSubCA1.pem       -outform DER -out certs\cpoSubCA1.der
openssl x509 -inform PEM -in certs\cpoSubCA2.pem       -outform DER -out certs\cpoSubCA2.der
openssl x509 -inform PEM -in certs\seccCert.pem        -outform DER -out certs\seccCert.der
openssl x509 -inform PEM -in certs\oemRootCA.pem       -outform DER -out certs\oemRootCA.der
openssl x509 -inform PEM -in certs\oemSubCA1.pem       -outform DER -out certs\oemSubCA1.der
openssl x509 -inform PEM -in certs\oemSubCA2.pem       -outform DER -out certs\oemSubCA2.der
openssl x509 -inform PEM -in certs\oemProvCert.pem     -outform DER -out certs\oemProvCert.der
openssl x509 -inform PEM -in certs\moRootCA.pem        -outform DER -out certs\moRootCA.der
openssl x509 -inform PEM -in certs\moSubCA1.pem        -outform DER -out certs\moSubCA1.der
openssl x509 -inform PEM -in certs\moSubCA2.pem        -outform DER -out certs\moSubCA2.der
openssl x509 -inform PEM -in certs\contractCert.pem    -outform DER -out certs\contractCert.der
REM Since the intermediate certificates need to be in PEM format when putting them in a PKCS12 container and the resulting PKCS12 file is a binary format, it might be sufficient. Otherwise, I have currently no idea how to covert the intermediate certificates in DER without running into problems when creating the PKCS12 container.

REM 17) In case you want the private keys in PKCS#8 file format and DER encoded, use this command. Especially necessary for the private key of MOSubCA2 in RISE V2G
openssl pkcs8 -topk8 -in privateKeys\moSubCA2.key -inform PEM -passin file:passphrase.txt -passout file:passphrase2.txt -outform DER -out privateKeys\moSubCA2.pkcs8.der


REM XX) Create the initial Java truststores and keystores
REM XX.1) truststore for the EVCC which needs to hold the V2GRootCA certificate (the EVCC does not verify the received contract certificate chain, therefore no MORootCA needs to be imported in evccTruststore.jks )
keytool -import -keystore keystores\evccTruststore.jks -alias v2g_root_ca -file certs\v2gRootCA.der -storepass:file passphrase.txt -noprompt
REM XX.2) truststore for the SECC which needs to hold the V2GRootCA certificate and the MORootCA which signed the MOSubCA1 (needed for verifying the  contract certificate signature chain which will be sent from the EVCC to the SECC with PaymentDetailsReq message). According to ISO 15118-2, MORootCA is not necessarily needed as the MOSubCA1 could instead be signed by a V2GRootCA.
keytool -import -keystore keystores\seccTruststore.jks -alias v2g_root_ca -file certs\v2gRootCA.der -storepass:file passphrase.txt -noprompt
keytool -import -keystore keystores\seccTruststore.jks -alias mo_root_ca -file certs\moRootCA.der -storepass:file passphrase.txt -noprompt
REM XX.3) keystore for the SECC which needs to hold the CPOSubCA1, CPOSubCA2, and SECCCert certificates
keytool -importkeystore -srckeystore certs\cpoCertChain.p12 -srcstoretype pkcs12 -srcstorepass:file passphrase.txt -srcalias secc_cert -destalias secc_cert -destkeystore keystores\seccKeystore.jks -storepass:file passphrase.txt -noprompt
REM XX.4) keystore for the EVCC which needs to hold the OEMSubCA1, OEMSubCA2, and OEMProvCert certificates
keytool -importkeystore -srckeystore certs\oemCertChain.p12 -srcstoretype pkcs12 -srcstorepass:file passphrase.txt -srcalias oem_prov_cert -destalias oem_prov_cert -destkeystore keystores\evccKeystore.jks -storepass:file passphrase.txt -noprompt


REM Side notes for OCSP stapling in Java: see http://openjdk.java.net/jeps/8046321
