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
package org.eclipse.risev2g.evcc.states;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.eclipse.risev2g.evcc.session.V2GCommunicationSessionEVCC;
import org.eclipse.risev2g.evcc.transportLayer.TLSClient;
import org.eclipse.risev2g.shared.enumerations.GlobalValues;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.messageHandling.TerminateSession;
import org.eclipse.risev2g.shared.utils.MiscUtils;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.CertificateChainType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EnergyTransferModeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.PaymentOptionType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SelectedServiceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceCategoryType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceDiscoveryResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceListType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ServiceType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

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
			
			// Save offered charge service and optional value added services
			getCommSessionContext().setOfferedServices(serviceDiscoveryRes.getServiceList());
			
			if (serviceDiscoveryRes.getChargeService() != null) {
				// Check if requested energy transfer mode is supported
				EnergyTransferModeType requestedEnergyTransferMode = getRequestedEnergyTransferMode();
				
				if (serviceDiscoveryRes.getChargeService().getSupportedEnergyTransferMode()
						.getEnergyTransferMode().contains(requestedEnergyTransferMode)) {
					getCommSessionContext().setRequestedEnergyTransferMode(requestedEnergyTransferMode);
					getCommSessionContext().getOfferedServices().getService().add(serviceDiscoveryRes.getChargeService());
					addSelectedService(1, null); // Assumption: a charge service is always used
				} else {
					return new TerminateSession("Offered EnergyTransferModes not compatible with the requested one");
				}
			} else return new TerminateSession("No charge service available");
			
			/*
			 * The payment options offered by the SECC should probably be displayed on a HMI in the EV.
			 * A request to the EVController should then be initiated here in order to let the user
			 * choose which offered payment option to use.
			 * 
			 * TODO check [V2G2-828] (selecting payment option related to state B, C)
			 */
			PaymentOptionType userPaymentOption = 
					getCommSessionContext().getEvController().getPaymentOption(serviceDiscoveryRes.getPaymentOptionList());
			getCommSessionContext().setSelectedPaymentOption(userPaymentOption);
			
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
	
	
	/**
	 * According to [V2G2-422] a ServiceDetailsReq is needed in case VAS (value added services)
	 * such as certificate installation/update are to be used and offered by the SECC. 
	 * Furthermore, it must be checked if VAS are allowed (-> only if TLS connection is used)
	 */
	private boolean useVAS(ServiceDiscoveryResType serviceDiscoveryRes) {
		if (getCommSessionContext().getTransportLayerClient() instanceof TLSClient) {
			// Check if certificate service is needed
			if (isCertificateServiceOffered(serviceDiscoveryRes.getServiceList())) { 
				KeyStore evccKeyStore = SecurityUtils.getKeyStore(
						GlobalValues.EVCC_KEYSTORE_FILEPATH.toString(),
						GlobalValues.PASSPHRASE_FOR_CERTIFICATES_AND_KEYS.toString());
				
				CertificateChainType contractCertificateChain = 
						SecurityUtils.getCertificateChain(evccKeyStore, GlobalValues.ALIAS_CONTRACT_CERTIFICATE.toString());
				
				if (contractCertificateChain != null) {
					if (!SecurityUtils.isCertificateChainValid(contractCertificateChain)) {
						addSelectedService(2, (short) 1); 
					} else {
						if (isContractCertificateUpdateNeeded(contractCertificateChain)) {
							addSelectedService(2, (short) 2); 
						} 
					}
				} else {
					addSelectedService(2, (short) 1); 
				}
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
		getCommSessionContext().getServiceDetailsToBeRequested().add((short) serviceID);
	}
	
	private boolean isCertificateServiceOffered(ServiceListType offeredServiceList) {
		for (ServiceType service : offeredServiceList.getService()) {
			if (service.getServiceCategory().equals(ServiceCategoryType.CONTRACT_CERTIFICATE))
				return true;
		}
		
		return false;
	}
	
	
	private boolean isContractCertificateUpdateNeeded(CertificateChainType contractCertificateChain) {
		Date today = new Date();
		X509Certificate contractCertificate = SecurityUtils.getCertificate(contractCertificateChain.getCertificate());
		long validityDays = contractCertificate.getNotAfter().getTime() - today.getTime();
		
		if (contractCertificate != null && validityDays < 
			( ((long) (int) MiscUtils.getPropertyValue("ContractCertificateUpdateTimespan")) * 24 * 60 * 60 * 1000 )) {
			
			getLogger().info("Contract certificate with distinguished name '" + 
							 contractCertificate.getSubjectX500Principal().getName() + 
							 "' is only valid for " + validityDays / (1000 * 60 * 60 * 24) + 
							 " days and needs to be updated");
			return true;
		} else return false;
	}
}
