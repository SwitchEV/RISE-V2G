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
package com.v2gclarity.risev2g.secc.states;

import com.v2gclarity.risev2g.secc.session.V2GCommunicationSessionSECC;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.BodyBaseType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ParameterSetType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ParameterType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDetailReqType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDetailResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceParameterListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
				setMandatoryFieldsForFailedRes(serviceDetailRes, serviceDetailRes.getResponseCode());
			}
		} else {
			if (serviceDetailRes.getResponseCode().equals(ResponseCodeType.FAILED_SEQUENCE_ERROR)) {
				BodyBaseType responseMessage = getSequenceErrorResMessage(new ServiceDetailResType(), message);
				
				return getSendMessage(responseMessage, V2GMessages.NONE, serviceDetailRes.getResponseCode());
			} else {
				setMandatoryFieldsForFailedRes(serviceDetailRes, serviceDetailRes.getResponseCode());
			}
		}
		
		return getSendMessage(serviceDetailRes, V2GMessages.NONE, serviceDetailRes.getResponseCode());
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
	public BodyBaseType getResponseMessage() {
		return serviceDetailRes;
	}

}
