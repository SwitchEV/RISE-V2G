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
package org.eclipse.risev2g.secc.states;

import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.risev2g.secc.session.V2GCommunicationSessionSECC;
import org.eclipse.risev2g.shared.enumerations.V2GMessages;
import org.eclipse.risev2g.shared.messageHandling.ReactionToIncomingMessage;
import org.eclipse.risev2g.shared.utils.SecurityUtils;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ACEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.DCEVSEStatusType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.EVSENotificationType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeterInfoType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeteringReceiptReqType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.MeteringReceiptResType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.ResponseCodeType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.SignatureType;
import org.eclipse.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class WaitForMeteringReceiptReq extends ServerState {

	private MeteringReceiptResType meteringReceiptRes;
	
	public WaitForMeteringReceiptReq(V2GCommunicationSessionSECC commSessionContext) {
		super(commSessionContext);
		meteringReceiptRes = new MeteringReceiptResType();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ReactionToIncomingMessage processIncomingMessage(Object message) {
		if (isIncomingMessageValid(message, MeteringReceiptReqType.class, meteringReceiptRes)) {
			V2GMessage v2gMessageReq = (V2GMessage) message;
			MeteringReceiptReqType meteringReceiptReq = 
					(MeteringReceiptReqType) v2gMessageReq.getBody().getBodyElement().getValue();
			
			if (isResponseCodeOK(meteringReceiptReq, v2gMessageReq.getHeader().getSignature())) {
				setEVSEStatus(meteringReceiptRes);
				
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.POWER_DELIVERY_REQ);
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.CHARGING_STATUS_REQ);
				((ForkState) getCommSessionContext().getStates().get(V2GMessages.FORK))
					.getAllowedRequests().add(V2GMessages.CURRENT_DEMAND_REQ);
				
				return getSendMessage(meteringReceiptRes, V2GMessages.FORK);
			} else {
				getLogger().error("Response code '" + meteringReceiptRes.getResponseCode() + "' will be sent");
				setMandatoryFieldsForFailedRes();
			}
		} else {
			setMandatoryFieldsForFailedRes();
		}
		
		return getSendMessage(meteringReceiptRes, V2GMessages.NONE);
	}

	
	private boolean isResponseCodeOK(
			MeteringReceiptReqType meteringReceiptReq,
			SignatureType signature) {
		/*
		 * Check if previously sent MeterInfo from ChargingStatusRes (AC charging) / 
		 * CurrentDemandRes (DC charging) is equal to the received MeterInfo
		 */
		if (!meterInfoEquals(getCommSessionContext().getSentMeterInfo(), meteringReceiptReq.getMeterInfo())) {
			meteringReceiptRes.setResponseCode(ResponseCodeType.FAILED_METERING_SIGNATURE_NOT_VALID);
			return false;
		}
		
		// Verify signature
		HashMap<String, byte[]> verifyXMLSigRefElements = new HashMap<String, byte[]>();
		verifyXMLSigRefElements.put(meteringReceiptReq.getId(), SecurityUtils.generateDigest(meteringReceiptReq));

		if (!SecurityUtils.verifySignature(
				signature, 
				verifyXMLSigRefElements, 
				getCommSessionContext().getContractSignatureCertChain().getCertificate())) {
			meteringReceiptRes.setResponseCode(ResponseCodeType.FAILED_METERING_SIGNATURE_NOT_VALID);
			return false;
		}
		
		return true;
	}
	
	
	private boolean meterInfoEquals(MeterInfoType meterInfoSentBySECC, MeterInfoType meterInfoReceivedFromEVCC) {
		if (meterInfoSentBySECC == null) {
			getLogger().error("MeterInfo sent by SECC is not saved in session context, value is null");
			return false;
		} else if (meterInfoReceivedFromEVCC == null) {
			getLogger().error("MeterInfo received from EVCC is null");
			return false;
		} else {	
			// Only meterID is mandatory field, thus check for null values as well
			if (!meterInfoSentBySECC.getMeterID().equals(meterInfoReceivedFromEVCC.getMeterID()) ||
				(meterInfoSentBySECC.getMeterReading() != null && !meterInfoSentBySECC.getMeterReading().equals(meterInfoReceivedFromEVCC.getMeterReading())) ||
				(meterInfoSentBySECC.getMeterStatus() != null && !meterInfoSentBySECC.getMeterStatus().equals(meterInfoReceivedFromEVCC.getMeterStatus())) ||
				(meterInfoSentBySECC.getSigMeterReading() != null && !Arrays.equals(meterInfoSentBySECC.getSigMeterReading(), meterInfoReceivedFromEVCC.getSigMeterReading())) ||
				(meterInfoSentBySECC.getTMeter() != null && !meterInfoSentBySECC.getTMeter().equals(meterInfoReceivedFromEVCC.getTMeter()))
				) return false;
			else return true;
		}
	}
	
	
	private void setEVSEStatus(MeteringReceiptResType meteringReceiptRes) {
		if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("AC")) {
			/*
			 * The MiscUtils method getJAXBElement() cannot be used here because of the difference in the
			 * class name (ACEVSEStatus) and the name in the XSD (AC_EVSEStatus)
			 */
			JAXBElement jaxbEVSEStatus = new JAXBElement(new QName("urn:iso:15118:2:2013:MsgDataTypes", "AC_EVSEStatus"), 
					ACEVSEStatusType.class, 
					getCommSessionContext().getACEvseController().getACEVSEStatus(EVSENotificationType.NONE));
			meteringReceiptRes.setEVSEStatus(jaxbEVSEStatus);
		} else if (getCommSessionContext().getRequestedEnergyTransferMode().toString().startsWith("DC")) {
			/*
			 * The MiscUtils method getJAXBElement() cannot be used here because of the difference in the
			 * class name (DCEVSEStatus) and the name in the XSD (DC_EVSEStatus)
			 */
			JAXBElement jaxbACEVSEStatus = new JAXBElement(new QName("urn:iso:15118:2:2013:MsgDataTypes", "DC_EVSEStatus"), 
					DCEVSEStatusType.class, 
					getCommSessionContext().getDCEvseController().getDCEVSEStatus(EVSENotificationType.NONE));
			meteringReceiptRes.setEVSEStatus(jaxbACEVSEStatus);
		} else {
			getLogger().warn("RequestedEnergyTransferMode '" + getCommSessionContext().getRequestedEnergyTransferMode().toString() + 
										"is neither of type AC nor DC");
		}
	}

	
	@Override
	protected void setMandatoryFieldsForFailedRes() {
		setEVSEStatus(meteringReceiptRes); 		
	}
}