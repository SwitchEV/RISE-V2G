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
package org.eclipse.risev2g.secc.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ChangeProcessingState;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.SECCDiscoveryReq;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.AppProtocolType;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import org.eclipse.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.BodyBaseType;

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
		
		if (supportedAppProtocolRes.getResponseCode().equals(ResponseCodeType.FAILED_NO_NEGOTIATION)) 
			getLogger().error("Response code '" + supportedAppProtocolRes.getResponseCode() + "' will be sent");
		
		return getSendMessage(supportedAppProtocolRes, 
							  (supportedAppProtocolRes.getResponseCode().toString().startsWith("OK") ? 
							  V2GMessages.SESSION_SETUP_REQ : V2GMessages.NONE)
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
	protected void setMandatoryFieldsForFailedRes() {
		// No additional mandatory fields besides response code
	}
	
}
