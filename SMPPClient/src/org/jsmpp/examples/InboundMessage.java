package org.jsmpp.examples;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.OctetString;
import org.jsmpp.util.InvalidDeliveryReceiptException;

// InboundMessage class is just a storage class to store received MOs and DRs

public class InboundMessage {
	
    public Integer messageType = 0;
    public String messageBody = null;
    public Integer PTTCode = 0;
    public String PTTStatus = null;   
    public Integer messageID = 0;
    public String shortCode = null;
    public String msisdn = null;
    public String operator = null;    
    
    public DeliveryReceipt deliveryReceipt = null;
    public DeliverSm localDeliverSm = null;
    
    // private final static short OPERATORTAG = 0x1402;

    public void setDeliverSm(DeliverSm deliverSm)
    {
    	localDeliverSm = deliverSm;
    }
    
    public void setInboundMessage(DeliverSm deliverSm)
    {
    	localDeliverSm = deliverSm;
    	shortCode = deliverSm.getDestAddress();
    	msisdn = deliverSm.getSourceAddr();
    	messageBody = new String(deliverSm.getShortMessage());
    	
    	if ((deliverSm.getEsmClass() & 0x04) > 0) {
	    	/*
	    	// REMOVE THIS. getShortMessageAsDeliveryReceipt() doesn't seem to work. Throws exception every time.
    		try {
				deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
			} catch (InvalidDeliveryReceiptException e) {
				// TODO Auto-generated catch block
				System.err.println("DR exception. Message body: " + messageBody);
				e.printStackTrace();
			}
	    	*/
	    	messageType = SMPPUtilities.DR;
	    	// pull more things out of DR here - though this function shouldn't really be called when a DR has been received.
    	} else if ((deliverSm.getEsmClass() & 0x04) == 0) {
    		messageType = SMPPUtilities.MO;
    	}
    	
    	OptionalParameter[] op = deliverSm.getOptionalParametes();

    	if (op != null && op.length > 0) {
    		// parse optional parameters
    		for (int i = 0; i < op.length; i++) {    			
       			byte opBytes[] = ((OptionalParameter.OctetString) op[i]).getValue();
    			if (op[i].tag == SMPPUtilities.OPERATOR_TAG) {
    				operator = new String(opBytes);
    			} else if (op[i].tag == 0x0000) {
    				// Add more possible tags here    				
    			}    			
    		}
    	}
    }
    
    public void setInboundMessage(Integer msgType, String msgBody, String PTTCodeString, String PTTStatusString, String msgID)
    {
    	Integer PTTCodeInt;
    	
    	try {
    		messageID = Integer.parseInt(msgID);
    	} catch (NumberFormatException nfe) {
    		messageID = 0;
    	}

    	try {
    		PTTCodeInt = Integer.parseInt(PTTCodeString);
    	} catch (NumberFormatException nfe) {
    		PTTCodeInt = 0;
    	}

    	//setInboundMessage(Integer msgType, String msgBody, Integer PTTCode, String PTTString)

    	setInboundMessage(msgType, msgBody, PTTCodeInt, PTTStatusString);
    }

    public void setInboundMessage(String msgType, String msgBody, String PTTCodeString, String PTTStatusString, String msgID)
    {
    	try {
    		messageID = Integer.parseInt(msgID);
    	} catch (NumberFormatException nfe) {
    		messageID = 0;
    	}
    	
    	setInboundMessage(msgType, msgBody, PTTCodeString, PTTStatusString);
    }
    
    public void setInboundMessage(Integer msgType, String msgBody, Integer PTTCodeInt, String PTTStatusString, Integer msgID)
    {
    	messageID = msgID;
    	setInboundMessage(msgType, msgBody, PTTCodeInt, PTTStatusString);    	
    }
    
    public void setInboundMessage(String msgType, String msgBody, String PTTCodeString, String PTTStatusString)
    {
    	Integer msgTypeInt;
    	Integer PTTCodeInt;
    	
    	try {
    		msgTypeInt = Integer.parseInt(msgType);
    	} catch (NumberFormatException nfe) {
    		msgTypeInt = SMPPUtilities.NOMSG;
    	}    	
    	try {
    		PTTCodeInt = Integer.parseInt(PTTCodeString);
    	} catch (NumberFormatException nfe) {
    		PTTCodeInt = 0;
    	}
    	setInboundMessage(msgTypeInt, msgBody, PTTCodeInt, PTTStatusString);
    }
    
    public void setInboundMessage(Integer msgType, String msgBody, Integer PTTCodeInt, String PTTStatusString)
    {
    	messageType = msgType;
    	messageBody = msgBody;
    	PTTCode = PTTCodeInt;
    	PTTStatus = PTTStatusString; 
    }
}
