1. Generated the Java classes from V2G_CI_MsgDef.xsd into the package v2gMessages.msgDef.
This creates all message types defined in 
- V2G_CI_MsgDef
- V2G_CI_MsgHeader
- V2G_CI_MsgBody
- V2G_CI_MsgDataTypes
- mxldsig-core-schema.xsd
(see page 263 of FDIS).

2. Generated the Java classes from V2G_CI_AppProtocol.xsd into the package v2gMessages.appProtocol
(because of the class ResponseCodeType, which is defined in both schema files and would therefore
raise conflicts).

3. Add SECCDiscoveryReq.java and SECCDiscoveryRes.java to the package v2gMessages.


-------------

When accessing the schema files in a jar file, it would be best to use
grammarFactory.createGrammars(getClass().getResourceAsStream("/schemas/[xsd-filename]");
in ExificientCodec.java instead of
grammarFactory.createGrammars([String for file location]) for example. 
For this, a resource folder (maybe called "resources") must exist at the same level as
the src folder and added to the Build Path. Inside this resources folder, there must reside the schemas
folder with the xsd files inside (then you see a schemas folder in the bin folder and it should work).