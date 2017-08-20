/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-207  V2G Clarity (Dr.-Ing. Marc Mültin) 
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
package org.v2gclarity.risev2g.secc.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.v2gclarity.risev2g.shared.enumerations.GlobalValues;
import org.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import org.v2gclarity.risev2g.shared.messageHandling.ChangeProcessingState;
import org.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.v2gclarity.risev2g.shared.v2gMessages.SECCDiscoveryReq;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.AppProtocolType;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.ResponseCodeType;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import org.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;

public class WaitForSupportedAppProtocolReq extends ServerState {
	
	private SupportedAppProtocolRes supportedAppProtocolRes;
	
	public WaitForSupportedAppProtocolReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
	}
	
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		supportedAppProtocolRes = new SupportedAppProtocolRes();
		
		if (message instanceof SupportedAppProtocolReq) {
			getLogger().debug("SupportedAppProtocolReq received");
			boolean match = false;
			ResponseCodeType responseCode = ResponseCodeType.FAILED_NO_NEGOTIATION;
			SupportedAppProtocolReq supportedAppProtocolReq = (SupportedAppProtocolReq) message;
			
			// The provided appProtocols might not be sorted by priority
			Collections.sort(supportedAppProtocolReq.getAppProtocol(), (appProtocol1, appProtocol2) ->
				Short.compare(appProtocol1.getPriority(), appProtocol2.getPriority()));
			
			/*
			 * If protocol and major version matches with more than one supported protocol,
			 * choose the one with highest priority
			 */
			for (AppProtocolType evccAppProtocol : supportedAppProtocolReq.getAppProtocol()) {
				/*
				 * A getSupportedAppProtocols().contains(evccAppProtocol) does not work here since 
				 * priority and schemaID are not provided in getSupportedAppProtocols()
				 */
				for (AppProtocolType seccAppProtocol : getSupportedAppProtocols()) {
					if (evccAppProtocol.getProtocolNamespace().equals(seccAppProtocol.getProtocolNamespace()) &&
						evccAppProtocol.getVersionNumberMajor() == seccAppProtocol.getVersionNumberMajor()) {
						if (evccAppProtocol.getVersionNumberMinor() == seccAppProtocol.getVersionNumberMinor()) {
							responseCode = ResponseCodeType.OK_SUCCESSFUL_NEGOTIATION;
						} else {
							responseCode = ResponseCodeType.OK_SUCCESSFUL_NEGOTIATION_WITH_MINOR_DEVIATION;
						}
						match = true;
						supportedAppProtocolRes.setSchemaID(evccAppProtocol.getSchemaID());
						break;
					}
				}
				
				if (match) break;
			}
				
			supportedAppProtocolRes.setResponseCode(responseCode);
		} else if (message instanceof SECCDiscoveryReq) {
			getLogger().debug("Another SECCDiscoveryReq was received, changing to state WaitForSECCDiscoveryReq");
			return new ChangeProcessingState(message, getCommSessionContext().getStates().get(V2GMessages.SECC_DISCOVERY_REQ));
		} else if (message != null) {
			/*
			 * This check has been introduced to make sure the application can deal with incoming messages which rely 
			 * on the DINSPEC 70121 XSD schema (which is different from the ISO 15118-2 schema. Without this check, 
			 * the message.getClass() would throw a NullPointerException and the application would die.
			 */
			getLogger().error("Invalid message (" + message.getClass().getSimpleName() + 
							  ") at this state (" + this.getClass().getSimpleName() + ")");
			supportedAppProtocolRes.setResponseCode(ResponseCodeType.FAILED_NO_NEGOTIATION);
		} else {
			getLogger().error("Invalid message at this state, message seems to be null. Check if same XSD schema is used on EVCC side.");
			supportedAppProtocolRes.setResponseCode(ResponseCodeType.FAILED_NO_NEGOTIATION);
		}
		
		return getSendMessage(supportedAppProtocolRes, 
							  (supportedAppProtocolRes.getResponseCode().toString().startsWith("OK") ? 
							  V2GMessages.SESSION_SETUP_REQ : V2GMessages.NONE),
							  supportedAppProtocolRes.getResponseCode()
							 );
	}
	
	
	/**
	 * All supported versions of the ISO/IEC 15118-2 protocol are listed here.
	 * Currently, only IS version of April 2014 is supported (see [V2G2-098]), more could be provided here.
	 * The values for priority and schema ID do not need to be set since these values are provided by
	 * the EVCC.
	 * 
	 * @return A list of supported of AppProtocol entries 
	 */
	private List<AppProtocolType> getSupportedAppProtocols() {
		List<AppProtocolType> supportedAppProtocols = new ArrayList<AppProtocolType>();
		
		AppProtocolType appProtocol1 = new AppProtocolType();
		appProtocol1.setProtocolNamespace(GlobalValues.V2G_CI_MSG_DEF_NAMESPACE.toString());
		appProtocol1.setVersionNumberMajor(2);
		appProtocol1.setVersionNumberMinor(0);
		
		supportedAppProtocols.add(appProtocol1);
		
		return supportedAppProtocols;
	}


	@Override
	public BodyBaseType getResponseMessage() {
		return null;
	}
	
}
