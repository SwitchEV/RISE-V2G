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

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ParameterSetType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ParameterType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDetailReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDetailResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceParameterListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForServiceDetailReq extends ServerState {

	private ServiceDetailResType serviceDetailRes;
	
	public WaitForServiceDetailReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		serviceDetailRes = new ServiceDetailResType();
	}

	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ServiceDetailReqType.class, serviceDetailRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			ServiceDetailReqType serviceDetailReq = (ServiceDetailReqType) v2gMessageReq.getBody().getBodyElement().getValue();
		
			if (isResponseCodeOK(serviceDetailReq)) {
				ServiceParameterListType serviceParameterList = new ServiceParameterListType();

				// The charge service has no parameters and is therefore not checked for here
				if (serviceDetailReq.getServiceID() == 2) {
					// parameters for certificate service
					serviceParameterList.getParameterSet().add(getCertificateInstallationParameters());
					serviceParameterList.getParameterSet().add(getCertificateUpdateParameters());
				} else if (serviceDetailReq.getServiceID() == 3) {
					// Comment out Internet access service which will not be available
					serviceParameterList.getParameterSet().add(getInternetAccessFTPPort20Parameters());
					serviceParameterList.getParameterSet().add(getInternetAccessFTPPort21Parameters());
					serviceParameterList.getParameterSet().add(getInternetAccessHTTPParameters());
					serviceParameterList.getParameterSet().add(getInternetAccessHTTPSParameters());
				}
				
				// Optionally, further service details parameters can be provided (if previously offered)
				
				serviceDetailRes.setServiceID(serviceDetailReq.getServiceID());
				
				// The ServiceParameterList itself is optional, but if you send it, it shall not be empty
				if (serviceParameterList.getParameterSet().size() > 0) {
					serviceDetailRes.setServiceParameterList(serviceParameterList);
				}
				
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.SERVICE_DETAIL_REQ);
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.PAYMENT_SERVICE_SELECTION_REQ);
				
				return getSendMessage(serviceDetailRes, V2GMessages.FORK);
			} else {
				getLogger().error("Response code '" + serviceDetailRes.getResponseCode() + "' will be sent");
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(serviceDetailRes, V2GMessages.NONE);
	}
		
		
	private boolean isResponseCodeOK(ServiceDetailReqType serviceDetailReq) {
		for (ServiceType service : getCommSessionContext().getOfferedServices()) {
			if (service.getServiceID() == serviceDetailReq.getServiceID())
				return true;
		}
		
		serviceDetailRes.setResponseCode(ResponseCodeType.FAILED_SERVICE_ID_INVALID);
		return false;
	}
	
	
	private ParameterSetType getCertificateInstallationParameters() {
		ParameterSetType parameterSet = new ParameterSetType();
		
		ParameterType certInstallation = new ParameterType();
		certInstallation.setName("Service");
		certInstallation.setStringValue("Installation");
		
		parameterSet.getParameter().add(certInstallation);
		parameterSet.setParameterSetID((short) 1);
		
		return parameterSet;
	}
	
	
	private ParameterSetType getCertificateUpdateParameters() {
		ParameterSetType parameterSet = new ParameterSetType();
		
		ParameterType certUpdate = new ParameterType();
		certUpdate.setName("Service");
		certUpdate.setStringValue("Update");
		
		parameterSet.getParameter().add(certUpdate);
		parameterSet.setParameterSetID((short) 2);
		
		return parameterSet;
	}
	
	
	private ParameterSetType getInternetAccessFTPPort20Parameters() {	
		ParameterSetType parameterSet = new ParameterSetType();
		
		ParameterType ftpPort20 = new ParameterType();
		ftpPort20.setName("FTP20");
		ftpPort20.setStringValue("ftp");
		ftpPort20.setIntValue(20);
		
		parameterSet.getParameter().add(ftpPort20);
		parameterSet.setParameterSetID((short) 1);
		
		return parameterSet;
	}
	
	
	private ParameterSetType getInternetAccessFTPPort21Parameters() {	
		ParameterSetType parameterSet = new ParameterSetType();
		
		ParameterType ftpPort21 = new ParameterType();
		ftpPort21.setName("FTP21");
		ftpPort21.setStringValue("ftp");
		ftpPort21.setIntValue(21);
		
		parameterSet.getParameter().add(ftpPort21);
		parameterSet.setParameterSetID((short) 2);
		
		return parameterSet;
	}
	
	
	private ParameterSetType getInternetAccessHTTPParameters() {	
		ParameterSetType parameterSet = new ParameterSetType();
		
		ParameterType http = new ParameterType();
		http.setName("HTTP port 80");
		http.setStringValue("http");
		http.setIntValue(80);
		
		parameterSet.getParameter().add(http);
		parameterSet.setParameterSetID((short) 3);
		
		return parameterSet;
	}
	
	
	private ParameterSetType getInternetAccessHTTPSParameters() {	
		ParameterSetType parameterSet = new ParameterSetType();
		
		ParameterType https = new ParameterType();
		https.setName("HTTP port 443");
		https.setStringValue("https");
		https.setIntValue(443);
		
		parameterSet.getParameter().add(https);
		parameterSet.setParameterSetID((short) 4);
		
		return parameterSet;
	}

	
	@Override
	protected void setMandatoryFieldsForFailedRes() {
		serviceDetailRes.setServiceID(1);
	}

}
