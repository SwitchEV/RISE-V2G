1. Generated the Java classes from V2G_CI_MsgDef.xsd into the package v2gMessages.msgDef.
This creates all message types defined in 
- V2G_CI_MsgDef
- V2G_CI_MsgHeader
- V2G_CI_MsgBody
- V2G_CI_MsgDataTypes
- xldsig-core-schema.xsd


2. Generated the Java classes from V2G_CI_AppProtocol.xsd into the package v2gMessages.appProtocol
(because of the class ResponseCodeType, which is defined in both schema files and would therefore
raise conflicts).

3. Add SECCDiscoveryReq.java and SECCDiscoveryRes.java to the package v2gMessages.
