package org.jsmpp.examples;

import java.io.UnsupportedEncodingException;

import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.InvalidDeliveryReceiptException;

public class MessageReceiverListener extends MessageReceiverListenerImpl {
	
	// private static final Integer MO_MSG = 0x00;
	// private static final Integer DR_MSG = 0x04;
	private static final Integer MAX_DR_WAIT_TIME = 240;

	// public String PTTCode;
	public String receiveMsg = null;
	private DeliverSm mrlDeliverSm = null;
	// public int messageType = 0;
	private int msgESMClass = 0;
	private static boolean newMsg = false;

	private void setNewMsgStatus(boolean isNewMsg) {
		// should lock this variable to be thread-safe
		newMsg = isNewMsg;
	}
	private boolean getNewMsgStatus() {
		// should lock this variable to be thread-safe
		return newMsg;
	}
	
	public InboundMessage waitForInboundMessage(SMPPSession session) {
		return waitForInboundMessage(session, MAX_DR_WAIT_TIME, null, null);
	}

	public InboundMessage waitForInboundMessage(SMPPSession session, Integer timeout) {
		return waitForInboundMessage(session, timeout, null, null);
	}

	public InboundMessage waitForInboundMessage(SMPPSession session, Integer timeout,
			String serviceType, String shortcode) {

		// String message = null;
		Integer counter;
		// String deliver_sm_type = null;
		// String deliver_sm_msg = null;
		Integer msgType = SMPPUtilities.NOMSG;
		InboundMessage inMsg = null;
		boolean isNewMsg = getNewMsgStatus();
		
		 if (timeout < 0) {
			 counter = MAX_DR_WAIT_TIME;
		 } else {
			 counter = timeout;
		 }
		
		System.out.println("Waiting for inbound MO or DR message");
		
		// If timeout is 0, loop infinitely, until a message has been received (receiveMsg != null).
		// Otherwise (timeout is not 0), loop until counter has run out, or a message has been received. 
		// while (receiveMsg == null && (session.getSessionState()).isBound() && (counter-- > 0 || timeout == 0)) {
		while (!getNewMsgStatus() && (session.getSessionState()).isBound() && (counter-- > 0 || timeout == 0)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 		System.out.print('.');
			// TODO: Separate them out
	 		// message = receiveMsg;
	 		//message = messageReceiverListener.receiveMsg;
			// message_type = messageReceiverListener.messageType;
		}

		System.out.println();
		if (receiveMsg == null) {
			System.out.println("Wait timed out. No message received.");
			return null;
		} else if ((msgESMClass & 0x04) > 0) { // Delivery Receipt
			msgType = SMPPUtilities.DR;
			System.out.println("Inbound Delivery Receipt received.");
			inMsg = parseInboundMessage(receiveMsg, msgType);
			
			// lock mrlDeliverSm here
			inMsg.setInboundMessage(mrlDeliverSm);
			// inMsg.setDeliverSm(mrlDeliverSm);
		} else { // Mobile Originated
			msgType = SMPPUtilities.MO;
			System.out.println("Inbound Mobile Originated message received.");
			inMsg = parseInboundMessage(receiveMsg, msgType);
			
			// lock mrlDeliverSm here
			inMsg.setInboundMessage(mrlDeliverSm);
		}
		System.out.println("Message: " + receiveMsg);
		setNewMsgStatus(false);
		
		return inMsg;
		// return deliver_sm_msg;
	}
	
	private InboundMessage parseInboundMessage(String message, Integer messageType) {
        // Wait for and accept Delivery Receipt
        String IDString = null;
		String PTTStatus = null;
        String PTTCodeString = null;
        // String msgTypeString = null;
        InboundMessage inMsg = new InboundMessage();
        
        // Sample returned string:
    	// id:1671112327 sub:001 dlvrd:001 submit date:1410102256 done date:1410102255 stat:ACKED   err:003 text: 
        
        // First, check returned message
        if (message == null)
        {
        	return inMsg;
        }
        
        // If we got to this point, message is not null, so parse message
        if (messageType == SMPPUtilities.DR)
        {
	    	int IDstart = message.indexOf("id:") + 3;
	    	int IDend = message.indexOf(" ", IDstart + 1);
	    	int PTTstart = message.indexOf("err:") + 4;
	    	int PTTend = message.indexOf(" ", PTTstart + 1);
	    	int StatusStart = message.indexOf("stat:") + 5;
	    	int StatusEnd = message.indexOf(" ", StatusStart);
	    	
	    	// msgTypeString = message.substring(msgTypeStart, msgTypeEnd);
	    	IDString = message.substring(IDstart, IDend);
	    	PTTCodeString = message.substring(PTTstart, PTTend);
	    	PTTStatus = message.substring(StatusStart, StatusEnd);
        }
        // if (messageType == SMPPUtilities.MO) {}
        
    	// inMsg.setInboundMessage(msgType, msgBody, PTTCode, PTTString)
    	inMsg.setInboundMessage(messageType, message, PTTCodeString, PTTStatus, IDString);
    	
    	// sample return format: "003,ACKED"
    	// return PTTCodeString + "," + PTTStatus;
    	
    	return inMsg;
	}	
	
	public void onAcceptDeliverSm(org.jsmpp.bean.DeliverSm deliverSm) throws org.jsmpp.extra.ProcessRequestException
	{
		byte[] receivedBytes;
		// PTTCode = null;

		setNewMsgStatus(true);
		receivedBytes = deliverSm.getShortMessage();
		msgESMClass = deliverSm.getEsmClass();
		
		try {
			receiveMsg = new String(receivedBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// set DR_msg to some default error value
			receiveMsg = "Error in deliver receipt. Parsing exception. stat:ENCODINGEXCEPTION err:000";
		}

		// while mrlDeliverSm is locked, sleep 1000
		
		// lock mrlDeliverSm here
		mrlDeliverSm = deliverSm;
		
		return;
	 }
	 
}
