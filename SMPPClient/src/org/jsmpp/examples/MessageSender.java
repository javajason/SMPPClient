package org.jsmpp.examples;

import java.io.IOException;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.SMPPSession;

public class MessageSender {
	
	private static final String DELIVER_MINUTES = "00";
	private static final String DELIVER_SECONDS = "05";
	private static final byte MSGREF = 0x4b;
	
	private Integer mostRecentPTTStatus = 0;
	private String mostRecentMTID = null;
	// ArrayList java.util.List Map HashSet
	
	public void addMTStatus(String messageID) {
		// status = 0
		// convert messageID from hex to dec, but still store as String (example: "11fb03fc")
		Integer messageIDint = 0;
		
		if (messageID == null) {
			mostRecentMTID = "0";
			return;
		}
		
		/*
		// Convert from hex to decimal
		try {
			messageIDint = Integer.parseInt(messageID, 16);
		} catch (NumberFormatException nfe) {
			System.err.println("Unable to parse \"" + messageID + "\" as an Integer.");
			nfe.printStackTrace();
			mostRecentMTID = null;
			return;
		}
		*/	
		try {
			messageIDint = Integer.parseInt(messageID);
		} catch (NumberFormatException nfe) {
			System.err.println("Unable to parse \"" + messageID + "\" as an Integer.");
			nfe.printStackTrace();
			mostRecentMTID = null;
			return;
		}

		// steps to prevent overwriting existing MT ID. Once we implement a list of pending MTs, we can just add another ID.
		if (mostRecentMTID == null) {
			mostRecentMTID = String.valueOf(messageIDint);
		}
	}
	
	public void updateMTStatus(String messageID, Integer pttCode) {
		// update stuff here
		// Assume messageID is in decimal format
		if (mostRecentMTID == null) { // Can't do anything. Just return without setting anything.
			return;
		}
		if (messageID.equals(mostRecentMTID)) {
			mostRecentPTTStatus = pttCode;
		}
		if (pttCode != 0 && pttCode != 3) { // PTT must be either 4, 6, or an error code. Either way, it is the final DR, so reset PTT status.
			mostRecentPTTStatus = 0;
			mostRecentMTID = null;
		}
	}
	
	public Integer getMTStatus(String messageID) {
		if (messageID.equals(mostRecentMTID)) {
			return mostRecentPTTStatus;
		} else {
			return -1;
		}
	}
	
	public static byte getMsgRef() {
		// Uses current time to generate a pseudo-random number
		long currentTime = System.currentTimeMillis();
		return (byte) ((currentTime / 1000) % 255);
	}
	
	public static byte [][] splitConcatMsg(byte[] body, Integer maxMsgLen) {
		
		Integer headerLength = 6;
		
		Integer maxSegmentLen = maxMsgLen-headerLength; // need room for UDH
		
		byte numParts = ((Integer) (body.length / maxSegmentLen)).byteValue();
		/*
		if (numPartsInt > Byte.MAX_VALUE) {
			System.out.println("Concatenated message contains too many parts. " + numPartsInt.toString() + " parts. Exiting...");
			return null;
			// System.exit(0);
		}
		*/		
		
		// if body length is not an even multiple of max length, add 1. 
		if (body.length % maxSegmentLen > 0) numParts++;
		
		// splitMsg = new Byte[numParts][maxMsgLen];
		byte [][] splitMsg = new byte[numParts][];
		
		byte concatMsgRef = getMsgRef();

		for (byte i = 0; i < numParts; i++) {
			
			Integer thisSegmentLen = 0;
			if (i == numParts-1) { //last iteration
				thisSegmentLen = (body.length % maxSegmentLen == 0 ? maxSegmentLen : body.length % maxSegmentLen);
			} else {
				thisSegmentLen = maxSegmentLen;
			}

			splitMsg[i] = new byte[thisSegmentLen+headerLength]; // add 6 to make room for UDH values
			
			// :05:00:03:ref:#parts:part#
			splitMsg[i][0] = 0x05;
			splitMsg[i][1] = 0x00;
			splitMsg[i][2] = 0x03;
			splitMsg[i][3] = concatMsgRef;
			splitMsg[i][4] = numParts;
			splitMsg[i][5] = (byte) (i+1);
			
			System.arraycopy(body, i*maxSegmentLen, splitMsg[i], headerLength, thisSegmentLen);
		}

		return splitMsg;
	}
	
	// Helper function, used for debugging
	public static void printBytes(byte[] msg) {
		System.out.print("DEBUG: ");
		for (int j = 0; j < msg.length; j++) System.out.print((char) (msg[j]));
		System.out.println();    		
	}
	
	public static String submitConcatMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, byte[] body, OptionalParameter[] op, boolean enableDRs)
			throws
				IOException,
				InvalidResponseException, 
				PDUException,
				ResponseTimeoutException,
				NegativeResponseException
	{  
        String DELIVERTIME =
        		"00000000" +
        		DELIVER_MINUTES +
        		DELIVER_SECONDS +
        		"000R";
        
        /*
         * FUNCTION PROTOTYPE:
         * 
        String org.jsmpp.session.SMPPSession.submitShortMessage(
        		String serviceType,
        		TypeOfNumber sourceAddrTon,
        		NumberingPlanIndicator sourceAddrNpi,
        		String sourceAddr,
        		TypeOfNumber destAddrTon,
        		NumberingPlanIndicator destAddrNpi,
        		String destinationAddr,
        		ESMClass esmClass,
        		byte protocolId,
        		byte priorityFlag,
        		String scheduleDeliveryTime,
        		String validityPeriod,
        		RegisteredDelivery registeredDelivery,
        		byte replaceIfPresentFlag,
        		DataCoding dataCoding,
        		byte smDefaultMsgId,
        		byte[] shortMessage,
        		OptionalParameter... optionalParameters)
        		
        			throws
        				PDUException,
        				ResponseTimeoutException,
        				InvalidResponseException,
        				NegativeResponseException,
        				IOException
        */
    	String lastMessageId = null;
    	
    	if (body == null) {
    		System.out.println("Message body is null. Exiting...");
    		return null;
    		// System.exit(0);
    	}
    	Integer maxMsgLen = 0;
    	if (dataCoding.value() == 0x08) {
    		maxMsgLen = 140;
    	} else if (dataCoding.value() == 0x00 || dataCoding.value() == 0x01) {
    		// maxMsgLen = 159;
    		maxMsgLen = 160;
    	} else if (dataCoding.value() == 0x03) {
    		maxMsgLen = 140;
    	} else { // just use default max message length
    		maxMsgLen = 140;
    	}
    	
    	if (body.length <= maxMsgLen) // not a concatenated message. Exit.
    	{
    		System.out.println("Message is too short to be concatenated. Exiting...");
    		return null;
    		// System.exit(0);
    	}
    	
    	if (maxMsgLen == 160) maxMsgLen = 159;
    	Integer numParts = 1;
    	byte [][] messageParts = splitConcatMsg(body, maxMsgLen);
    	if (messageParts != null) {
    		numParts = messageParts.length;
    		if (numParts <= 1) {
        		System.out.println("Message is too short (" + numParts.toString() + " parts) to be concatenated. Exiting...");
        		return null;
        		// System.exit(0);
    		}
    	}
    	    	
    	ESMClass ESMConcatenated = new ESMClass(0x40);
    	
    	for (int i = 0; i < numParts; i++) {    		
	    	if (op != null) { // vendor-specific ("optional") parameters to include, such as service ID, tariff, and operator ID
		    	lastMessageId = session.submitShortMessage(
		    			serviceType,
		    			TypeOfNumber.valueOf((byte) 3),
		    			NumberingPlanIndicator.valueOf((byte) 8),
		    			shortcode,
		    			TypeOfNumber.NATIONAL,
		    			NumberingPlanIndicator.valueOf((byte) 8),
		    			msisdn,
		    			// new ESMClass(),
		    			ESMConcatenated,
		    			(byte)0,
		    			(byte)1,
		    			DELIVERTIME,
		    			null,
		    			(enableDRs ?
		    	    			new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE) :
		    	    			new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT)),
		    			(byte)0, // replaceIfPresent flag
		    			dataCoding,
		    			// (byte)0,
		    			(byte)(23+i),
		    			messageParts[i],
		    			// body,
		    			op);
	    	} else { // op = null. No vendor-specific ("optional") parameters to include.
		    	lastMessageId = session.submitShortMessage(
		    			serviceType,
		    			TypeOfNumber.valueOf((byte) 3),
		    			NumberingPlanIndicator.valueOf((byte) 0),
		    			shortcode,
		    			TypeOfNumber.NATIONAL,
		    			NumberingPlanIndicator.valueOf((byte) 0),
		    			msisdn,
		    			ESMConcatenated,
		    			// new ESMClass(),
		    			(byte)0,
		    			(byte)1,
		    			DELIVERTIME,
		    			null,
		    			// new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
		    			(enableDRs ?
		    	    			new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE) :
		    	    			new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT)),
		    			(byte)0,
		    			dataCoding,
		    			(byte)(24+i),
		    			// body
		    			messageParts[i]
		    			);
	    	}
    	} // for loop
    	
    	// Convert messageID. which is formatted in Hex, to decimal, before returning.
    	Integer msgIDint = 0;
    	try {
    		msgIDint = Integer.parseInt(lastMessageId, 16);
    	} catch (NumberFormatException nfe) {
    		return null;
    	}
    	
    	return String.valueOf(msgIDint);
	}
	
	// public static String submitMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, GeneralDataCoding dataCoding, String body, OptionalParameter[] op)
	public static String submitMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, byte[] body, OptionalParameter[] op, boolean enableDRs)
			throws
				IOException,
				InvalidResponseException, 
				PDUException,
				ResponseTimeoutException,
				NegativeResponseException
	{  
        String DELIVERTIME =
        		"00000000" +
        		DELIVER_MINUTES +
        		DELIVER_SECONDS +
        		"000R";
        
        /*
         * FUNCTION PROTOTYPE:
         * 
        String org.jsmpp.session.SMPPSession.submitShortMessage(
        		String serviceType,
        		TypeOfNumber sourceAddrTon,
        		NumberingPlanIndicator sourceAddrNpi,
        		String sourceAddr,
        		TypeOfNumber destAddrTon,
        		NumberingPlanIndicator destAddrNpi,
        		String destinationAddr,
        		ESMClass esmClass,
        		byte protocolId,
        		byte priorityFlag,
        		String scheduleDeliveryTime,
        		String validityPeriod,
        		RegisteredDelivery registeredDelivery,
        		byte replaceIfPresentFlag,
        		DataCoding dataCoding,
        		byte smDefaultMsgId,
        		byte[] shortMessage,
        		OptionalParameter... optionalParameters)
        		
        			throws
        				PDUException,
        				ResponseTimeoutException,
        				InvalidResponseException,
        				NegativeResponseException,
        				IOException
        */
    	String messageId;
    	if (op != null) { // vendor-specific ("optional") parameters to include, such as service ID, tariff, and operator ID
	    	messageId = session.submitShortMessage(
	    			serviceType,
	    			TypeOfNumber.valueOf((byte) 3),
	    			NumberingPlanIndicator.valueOf((byte) 8),
	    			shortcode,
	    			TypeOfNumber.NATIONAL,
	    			NumberingPlanIndicator.valueOf((byte) 8),
	    			msisdn,
	    			new ESMClass(),
	    			(byte)0,
	    			(byte)1,
	    			DELIVERTIME,
	    			null,
	    			(enableDRs ?
	    	    			new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE) :
	    	    			new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT)),
	    			(byte)0, // replaceIfPresent flag
	    			dataCoding,
	    			// (byte)0,
	    			(byte)23,
	    			// body.getBytes(),
	    			body,
	    			// MsgBytes,
	    			op);
    	} else { // op = null. No vendor-specific ("optional") parameters to include.
	    	messageId = session.submitShortMessage(
	    			serviceType,
	    			TypeOfNumber.valueOf((byte) 3),
	    			NumberingPlanIndicator.valueOf((byte) 0),
	    			shortcode,
	    			TypeOfNumber.NATIONAL,
	    			NumberingPlanIndicator.valueOf((byte) 0),
	    			msisdn,
	    			new ESMClass(),
	    			(byte)0,
	    			(byte)1,
	    			DELIVERTIME,
	    			null,
	    			// new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
	    			(enableDRs ?
	    	    			new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE) :
	    	    			new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT)),
	    			(byte)0,
	    			dataCoding,
	    			(byte)24,
	    			// body.getBytes()
	    			body
	    			);
    	}
    	
    	// Convert messageID. which is formatted in Hex, to decimal, before returning.
    	Integer msgIDint = 0;
    	try {
    		msgIDint = Integer.parseInt(messageId, 16);
    	} catch (NumberFormatException nfe) {
    		// Exception likely due to int string being too long, as is common for message IDs returned by CBF/Atlas 
    		// return null;
    		return "CBF ID " + messageId;
    	}
    	
    	return String.valueOf(msgIDint);
	}
	
	public static String submitMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, byte[] body, OptionalParameter[] op)
			throws
				IOException,
				InvalidResponseException, 
				PDUException,
				ResponseTimeoutException,
				NegativeResponseException
	{
		return submitMessage(session, serviceType, shortcode, msisdn, dataCoding, body, op, false);
	}

	
	/*
	 * Three sendMessaage() signatures. All methods will eventually call the last one (with the OptionalParameter[] arg),
	 * which calls submitMessage() and handles output to the console in the event of any exceptions.
	 *   public String sendMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, String body)
	 *   public String sendMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, String body, String serviceID)
	 *   public String sendMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, String body, OptionalParameter[] op)
	 */
	public String sendMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, String operator, DataCoding dataCoding, String body)
	{
		// figure out service ID and call sendMessage with it. 
		String serviceID = null;
		
		if (operator != null) {
			if (operator.equals(SMPPUtilities.OPERATOR_TMO)) {
				serviceID = SMPPUtilities.TMO_SERVICEID;
			} else if (operator.equals(SMPPUtilities.OPERATOR_VZW)) {
				serviceID = SMPPUtilities.VZW_SERVICEID;
			}
		}
		
		return sendMessage(session, serviceType, shortcode, msisdn, operator, dataCoding, body, serviceID);
	}
	
	public String sendMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, String operator, DataCoding dataCoding, String body, String serviceID)
	{
		String smppg = null;
		OptionalParameter op[] = null;
		
		if (operator == null || operator.isEmpty() || (operator.length() == 1 && operator.equals("0"))) {
        	// Use SureRoute
			smppg = "smpp.mt.us.mblox.com";        	

        	if (serviceID != null && serviceID.length() > 1) {
		        // OptionalParameter op0;
		        OptionalParameter op0 = new OptionalParameter.OctetString(SMPPUtilities.SERVICEID_TAG, serviceID.getBytes(), 0, 5);
	        	op = new OptionalParameter[1];
	        	op[0] = op0;
	        }
        } else { // OPERATOR is not null, so use two-way/PSMS platform
        	smppg = "smpp.psms.us.mblox.com";
        	int optionalParamCount = 2;
        	boolean includeSID = false;
        	
        	/*
        	if (serviceID == null || serviceID.length == 0 || (serviceID.length == 1 && serviceID[0] == '0')) {
        		op = new OptionalParameter[2];
        	}
        	*/
        	
        	// service ID
        	if (serviceID != null && !serviceID.isEmpty() && !(serviceID.length() == 1 && serviceID.equals("0"))) {
        		includeSID = true;
        		optionalParamCount++;
        	}
        	
        	// program type
        	byte[] programType = (SMPPUtilities.PROGRAMTYPE == null ? null : SMPPUtilities.PROGRAMTYPE.getBytes());
        	if (programType != null && programType.length > 0) {
        		optionalParamCount++;
        	}
        	
        	op = new OptionalParameter[optionalParamCount];
        	OptionalParameter op0 = new OptionalParameter.OctetString(SMPPUtilities.OPERATOR_TAG, operator.getBytes(), 0, 5);
	        OptionalParameter op1 = new OptionalParameter.OctetString(SMPPUtilities.TARIFF_TAG, SMPPUtilities.TARIFF, 0, 5);
	        op[0] = op0;
	        op[1] = op1;
	        
        		// PROGRAM_TAG op = new OptionalParameter[3];
        		// op = new OptionalParameter[4]; // PROGRAM_TAG 
        		// OptionalParameter op2 = new OptionalParameter.OctetString(SERVICEID_TAG, SERVICEID, 0, 5);
	        if (optionalParamCount == 4) { // include service ID and PROGRAM_TAG
	        	// Can also check for includeSID == true, but would require additional error-handling if false. Possibly to do later.
	        	// Can also check for optionalParamCount > 4, but would require additional error-handling if true. Possibly to do later.
        		OptionalParameter op2 = new OptionalParameter.OctetString(SMPPUtilities.SERVICEID_TAG, serviceID.getBytes(), 0, 5);
        		op[2] = op2;
        		OptionalParameter op3 = new OptionalParameter.OctetString(SMPPUtilities.PROGRAM_TAG, programType, 0, 5); 
        		op[3] = op3;
	        } else if (optionalParamCount == 3) {
	        	if (includeSID) { // include service ID, but not PROGRAM_TAG
	        		OptionalParameter op2 = new OptionalParameter.OctetString(SMPPUtilities.SERVICEID_TAG, serviceID.getBytes(), 0, 5);
	        		op[2] = op2;
	        	} else { // include PROGRAM_TAG, but not service ID
	        		OptionalParameter op2 = new OptionalParameter.OctetString(SMPPUtilities.PROGRAM_TAG, programType, 0, 5);
	        		op[2] = op2;
	        	}
	        }
        }
				
		return sendMessage(session, serviceType, shortcode, msisdn, dataCoding, body, op);
	}
	
	public String sendMessage(SMPPSession session, String serviceType, String shortcode, String msisdn, DataCoding dataCoding, String body, OptionalParameter[] op)
	{
		// public String sendMessage() {
	    //YYMMDDhhmmss
	    //020610233429000R
		String messageId = null;
	                    
	    // Send SMS message
	    try {
	    	messageId = submitMessage(
	    			session,
	    			serviceType, // username
	    			shortcode,
	    			msisdn,
	    			dataCoding,
	    			body.getBytes(),
	    			op,
	    			true); // enable DRs
	        System.out.println(SMPPUtilities.getTimeStamp() + " Message submitted, message_id is " + messageId);
	    } catch (PDUException e) {
	        // Invalid PDU parameter
	        System.err.println("Invalid PDU parameter");
	        e.printStackTrace();
	        return messageId;
	    } catch (ResponseTimeoutException e) {
	        // Response timeout
	        System.err.println("Responsed timeout");
	        e.printStackTrace();
	        return messageId;
	    } catch (InvalidResponseException e) {
	        // Invalid response
	        System.err.println("Received invalid respose");
	        e.printStackTrace();
	        return messageId;
	    } catch (NegativeResponseException e) {
	        // Receiving negative response (non-zero command_status)
	    	String errorMsg = e.toString();
	    	if (errorMsg.startsWith("org.jsmpp.extra.NegativeResponseException: Negative response"))
	    	{
	    		String errorCode = errorMsg.split(" ", 5)[3];
	    		if (errorCode.equals("00000408")) {
	    			System.out.println("\nInvalid phone number entered");
	    			System.out.println("Usage:");
	    			System.out.println("smpp [user] [pass] [port] [short code] [msisdn] [message] [operator | 0] [serviceID | 0]");
	    		}
	    		// else // cover other error responses here
	    	} else {			
	            System.err.println("Received negative response");
	            e.printStackTrace();
	    	}
	    	return messageId;
	    } catch (IOException e) {
	        System.err.println("IO error occurred");
	        e.printStackTrace();
	        return messageId;
	    } catch (NullPointerException e) {
	        System.err.println("NullPointerException");
	        e.printStackTrace();
	        return messageId;
	    }
	    return messageId;
    }
}
