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
package com.v2gclarity.risev2g.evcc.states;

import com.v2gclarity.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import com.v2gclarity.risev2g.evcc.transportLayer.TLSClient;
import com.v2gclarity.risev2g.shared.enumerations.V2GMessages;
import com.v2gclarity.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import com.v2gclarity.risev2g.shared.messageHandling.TerminateSession;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils;
import com.v2gclarity.risev2g.shared.utils.SecurityUtils.ContractCertificateStatus;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.SelectedServiceType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceCategoryType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryResType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceListType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.ServiceType;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForServiceDiscoveryRes extends ClientState {
	
	public WaitForServiceDiscoveryRes(V2GCommunicationSessionEVCC commSessionContext) {
		super(commSessionContext);
	}

	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, ServiceDiscoveryResType.class)) {
			ServiceDiscoveryResType serviceDiscoveryRes = 
					(ServiceDiscoveryResType) ((V2GMessage) message).getBody().getBodyElement().getValue();
			
			getCommSessionContext().getSelectedServices().getSelectedService().clear();  // just to be sure
			
			/*
			 * For every service whose details need to be requested, a ServiceDetailReq message must be sent.
			 * First by WaitForServiceDiscoveryRes, then by WaitForServiceDetailRes. In order to keep track of 
			 * which service details still need to be requested, we use this helper list.
			 */
			getCommSessionContext().getServiceDetailsToBeRequested().clear(); // just to be sure
			
			// Save the list containing information on all other services than charging services offered by the charging station
			getCommSessionContext().setOfferedServices(serviceDiscoveryRes.getServiceList());
			
			if (serviceDiscoveryRes.getChargeService() != null) {
				// Check if requested energy transfer mode is supported
				EnergyTransferModeType requestedEnergyTransferMode = getRequestedEnergyTransferMode();
				
				if (serviceDiscoveryRes.getChargeService().getSupportedEnergyTransferMode()
						.getEnergyTransferMode().contains(requestedEnergyTransferMode)) {
					getCommSessionContext().getOfferedServices().getService().add(serviceDiscoveryRes.getChargeService());
					addSelectedService(1, null); // Assumption: a charge service is always used
				} else {
					return new TerminateSession("Offered EnergyTransferModes not compatible with the requested one, which is " + requestedEnergyTransferMode.toString());
				}
			} else return new TerminateSession("No charge service available");
			
			getCommSessionContext().setSelectedPaymentOption(getSelectedPaymentOption(serviceDiscoveryRes.getPaymentOptionList()));
			
			// Check for the usage of value added services (VAS)
			if (useVAS(serviceDiscoveryRes)) {
				return getSendMessage(getServiceDetailReq(), V2GMessages.SERVICE_DETAIL_RES);
			} else {
				return getSendMessage(getPaymentServiceSelectionReq(), V2GMessages.PAYMENT_SERVICE_SELECTION_RES);
			}
		} else {
			return new TerminateSession("Incoming message raised an error");
		}
	}
	
	
	protected PaymentOptionType getSelectedPaymentOption(PaymentOptionListType authenticationOptions) {
		/*
		 * Note that although the type is called "PaymentOptionListType", it's not a list of payment options, but authorization options, 
		 * namely either Plug & Charge ("Contract") or external identification means (EIM) like an RFID card ("ExternalPayment"). This is 
		 * why the parameter for this function is called "authenticationOptions" for clarity.
		 */
		
		PaymentOptionType selectedPaymentOption = null;
		
		// Check if a PaymentOptionType has been requested in a previously paused session 
		if (getCommSessionContext().isOldSessionJoined()) 
			selectedPaymentOption = (PaymentOptionType) MiscUtils.getPropertyValue("authentication.mode");
		
		if (selectedPaymentOption == null) 
			selectedPaymentOption = getCommSessionContext().getEvController().getPaymentOption();
		
		// Contract payment option may only be chosen if offered by SECC AND if communication is secured by TLS
		if (selectedPaymentOption.equals(PaymentOptionType.CONTRACT) &&
			authenticationOptions.getPaymentOption().contains(PaymentOptionType.CONTRACT)) {
			
			if (!getCommSessionContext().isTlsConnection()) {
				getLogger().warn("SECC offered 'Contract' based payment although no TLS connectionis used. Choosing 'ExternalPayment' instead");
				getCommSessionContext().setSelectedPaymentOption(PaymentOptionType.EXTERNAL_PAYMENT);
				return PaymentOptionType.EXTERNAL_PAYMENT;
			} else {
				getCommSessionContext().setSelectedPaymentOption(PaymentOptionType.CONTRACT);
				return PaymentOptionType.CONTRACT; 
			}
		} else {
			getCommSessionContext().setSelectedPaymentOption(PaymentOptionType.EXTERNAL_PAYMENT);
			return PaymentOptionType.EXTERNAL_PAYMENT;
		}
	}
	
	
	/**
	 * According to [V2G2-422] a ServiceDetailsReq is needed in case VAS (value added services)
	 * such as certificate installation/update are to be used and offered by the SECC. 
	 * Furthermore, it must be checked if VAS are allowed (-> only if TLS connection is used)
	 */
	private boolean useVAS(ServiceDiscoveryResType serviceDiscoveryRes) {
		if (serviceDiscoveryRes.getServiceList() != null && 
			getCommSessionContext().getTransportLayerClient() instanceof TLSClient) {
			
			getLogger().debug("List of offered value added services: ");
			for (ServiceType service : serviceDiscoveryRes.getServiceList().getService()) {
				getLogger().debug("ID = " + service.getServiceID() + ", name = " + service.getServiceName());
			}
			
			// Check if certificate service is needed
			if (isCertificateServiceOffered(serviceDiscoveryRes.getServiceList())) { 
				getCommSessionContext().setContractCertStatus(SecurityUtils.getContractCertificateStatus());
				
				if (getCommSessionContext().getContractCertStatus().equals(ContractCertificateStatus.INSTALLATION_NEEDED))
					addSelectedService(2, (short) 1); 
				else if (getCommSessionContext().getContractCertStatus().equals(ContractCertificateStatus.UPDATE_NEEDED))
					addSelectedService(2, (short) 2); 
			}
			
			// Optionally, other value added services can be checked for here ...
		} else return false;
		
		return (getCommSessionContext().getServiceDetailsToBeRequested().size() > 0) ? true : false;
	}
	

	private void addSelectedService(int serviceID, Short parameterSetID) {
		/*
		 * The SelectedServiceType holds an optional parameter for parameterSetID. This parameterSetID 
		 * will be retrieved later by the ServiceDetailRes. However, in case of certificate installation/update
		 * a check for the needed parameterSetID is already done at this state (and the values are defined
		 * by Table 105 in ISO/IEC 15118) which is why we already save the parameterSetID for certificate here.
		 */
		SelectedServiceType selectedService = new SelectedServiceType();
		selectedService.setServiceID(serviceID);
		selectedService.setParameterSetID(parameterSetID);
		
		getCommSessionContext().getSelectedServices().getSelectedService().add(selectedService);
		
		/*
		 * There are no service details (a ServiceParameterList) yet defined for a charge service, 
		 * so don't send a ServiceDetailReq for a ChargeService
		 */
		if (serviceID != 1)
			getCommSessionContext().getServiceDetailsToBeRequested().add((short) serviceID);
	}
	
	
	private boolean isCertificateServiceOffered(ServiceListType offeredServiceList) {
		if (offeredServiceList == null) {
			getLogger().debug("No value added services offered by EVCC");
			return false;
		}
		
		for (ServiceType service : offeredServiceList.getService()) {
			if (service.getServiceCategory().equals(ServiceCategoryType.CONTRACT_CERTIFICATE))
				return true;
		}
		
		return false;
	}
}
