package org.jsmpp.examples;

import java.io.UnsupportedEncodingException;

import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.util.InvalidDeliveryReceiptException;

public class MessageReceiverDR extends MessageReceiverListenerImpl {
	
	public static final Integer MO_MSG = 0x00;
	public static final Integer DR_MSG = 0x04;

	public String PTTCode;
	public String receiveMsg = null;
	public int messageType = 0;
	
	public void onAcceptDeliverSm(org.jsmpp.bean.DeliverSm deliverSm) throws org.jsmpp.extra.ProcessRequestException
	{
		// DeliveryReceipt DR;
		byte[] receivedBytes;
		PTTCode = null;

		receivedBytes = deliverSm.getShortMessage();
		messageType = deliverSm.getEsmClass();
		
		try {
			receiveMsg = new String(receivedBytes, "UTF-8");
			receiveMsg = receiveMsg.replace((char) 0, '-');
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// set DR_msg to some default error value
			receiveMsg = "Error in deliver receipt. Parsing exception. stat:ENCODINGEXCEPTION err:000";
		}
		/*
		try {
			DR = deliverSm.getShortMessageAsDeliveryReceipt();
			PTTCode = DR.getError();
		} catch (InvalidDeliveryReceiptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

		return;
	 }
	 
}
