package org.jsmpp.examples;

/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
// package SMPPCLient;

import java.io.IOException;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;

// TODO: Create native executable file for this application, and handle command-line arguments
// See:
// http://wadeawalker.wordpress.com/2010/10/24/tutorial-creating-native-binary-executables-for-multi-platform-java-apps-with-opengl-and-eclipse-rcp/
// http://stackoverflow.com/questions/5293321/create-a-java-executable-with-eclipse

// Example, using command-line arguments in Run Configuration:
// mBloxDemoUS P7asT6fr 3204 28444 12063802788 Test_message 31010 0
// 	(system_type is mBloxDemo1, keyword is ALLBLOX)
// mBloxAlertsUS zA24k6X3 3208 28444 12063802788 Hello 31010 0
// [user] [pass] [port] [short code] [msisdn] [message] [operator | 0] [serviceID | 0]
// KBMGUS vTjRd6Q5 3209 28444 16505551212 Hello 31010 0
// KBMGUS vTjRd6Q5 3209 28444 16505206212 Hi_there 31005  0 KBMG1


/**
 * @author uudashr, Jason Epstein
 *
 */
// public class SimpleSubmitExample {
public class MessageResponder {		
	// private static final String SYSTEMID = "mBloxAlertsUS";
	// private static final String PASSWORD = "zA24k6X3";
	// mBloxAlertUS service IDs:
	// private static final String SERVICEID = "60521"; // TMO
	// private static final String SERVICEID = "60522"; // VZW
	// private static final String SERVICEID = null; // ATT, SPR, etc.
	// Other SIDs for mBloxAlertsUS/VZW 60522, 60516
	// Valid TMO SIDs for mBloxAlertsUS: 60519,60520,60521
	// private static final Integer PORT = 3208;

	/*
	private static final String SYSTEMID = "SteveTestUS";
	private static final String PASSWORD = "Testing";
	// mBloxAlertUS service IDs:
	// private static final String SERVICEID = "60521"; // TMO
	// private static final String SERVICEID = "60522"; // VZW
	private static final String SERVICEID = null; // ATT, SPR, etc.
	// Other SIDs for mBloxAlertsUS/VZW 60522, 60516
	// Valid TMO SIDs for mBloxAlertsUS: 60519,60520,60521
	private static final Integer PORT = 3205;
	*/
	
	private static final String SYSTEMID = "mBloxDemoUS";
	private static final String PASSWORD = "P7asT6fr";
	private static final String SYSTEMTYPE = "mBloxDemo1";
	// private static final String SYSTEMTYPE = "FOO";
	private static final String SERVICEID = null; // ATT, SPR, etc.
	private static final Integer PORT = 3204;

	private static final String SHORTCODE = "28444";
	private static final String MSISDN = null; // "16505047391";
    private static final String OPERATOR = "unknown"; // "31002";
    private static final String TMOBILE_ID = "31004";
    private static final String VERIZON_ID = "31003";
    
    // private static final String MESSAGE_BODY = "Test 2 - DCS 8"; // "Flash SMS message?";
    // private static final String MESSAGE_BODY = "\ud83c" + "\udf55" + "";
    // private static final String MESSAGE_BODY = 0xd83c + "" + 0xdf55;
    private static final String MESSAGE_BODY = "Test message";
	private static final byte DATACODING = (byte) 0x00; // 0x08 is Unicode
		
	// private static final Integer MAX_DR_WAIT_TIME = 240;
	private static final Integer MAX_DRs = 3;
	private static final Integer MAXBINDRETRIES = 1000;

	/*
	private static final String SYSTEMID = "mBloxTestMT";
	private static final String PASSWORD = "5Pa8AWru";
	private static final String SHORTCODE = "28444";
	private static final byte[] OPERATOR = null;
	private static final byte[] SERVICEID = null;
	private static final String SYSTEMTYPE = "";
	private static final String SERVICETYPE = null; // profile ID
	private static final String MSISDN = "16508633686"; // ATT
	private static final Integer PORT = 3203;
	private static final String MESSAGE_BODY = "Normal SMS message.";
	*/
	
	// TODO: change this, so SMPPG can be dynamically assigned later
	// private static String SMPPG;
	private static String SMPPG = "smpp.mt.us.mblox.com";
	// private static String SMPPG = "smpp.psms.us.mblox.com";
	
	// public static Integer getNextInboundMessage(SMPPSession session) {
	public static InboundMessage getNextInboundMessage(SMPPSession session) {
		return getNextInboundMessage(session, -1);
	}
	
	public static InboundMessage getNextInboundMessage(SMPPSession session, Integer timeout) {
        // Wait for and accept Delivery Receipt
        String PTTStatus = null;
        Integer PTTCode = 0;
        Integer DRsReceived = 0;
        // String PTTCodeAndStatus = null;
        InboundMessage inMsg = null;
        
		MessageReceiverListener messageReceiverListener = new MessageReceiverListener();
		session.setMessageReceiverListener(messageReceiverListener);

		if (timeout < 0) {
			inMsg = messageReceiverListener.waitForInboundMessage(session);
		} else {
			inMsg = messageReceiverListener.waitForInboundMessage(session, timeout);
		}
		
		/*
    	if (inMsg == null)
    	{
    		inMsg = new InboundMessage();
    		inMsg.setInboundMessage(SMPPUtilities.NOMSG, null, 0, null);
    	}
    	*/
		
		return inMsg;    	
	}
	
	/**
	 * @param args
	 */

	public static void _main(String[] args) {
		/*
		 * args:
		 * 1: Partner name
		 * 2: Password
		 * 3: Port
		 * 4: Short code
		 * 5: Subscriber number
		 * 6: Message
		 * 7: Operator (use 0 if SureRoute account)
		 * 8: Service ID (use 0 if no service ID)
		 */
		String systemID;
		String userPass;
		Integer port;
		String shortCode;
		String message;
		String msisdn;
		String systemType = null;
		byte[] operator;
		byte[] serviceID;
		byte[] programType;
		
		/*
        Integer bindRetries = MAXBINDRETRIES;
        Integer retryInterval = 30;
        Integer idleInterval = 30;
        */


		SMPPSession session = new SMPPSession();
        session.setTransactionTimer(10000L);
        session.setEnquireLinkTimer(30000);
        SessionState state;

        /* OptionalParameter op[] = null; */

		if (args.length < 8) {
			if (args.length > 0) {
				// Time to school the user
				System.out.println("Usage:");
				System.out.println("smpp [user] [pass] [port] [short code] [msisdn] [message] [operator | 0] [serviceID | 0]");
				return;
			} else { // no args, so assign default settings
				systemID = SYSTEMID;
				userPass = PASSWORD;
				port = PORT;
				shortCode = SHORTCODE;
				msisdn = MSISDN;
				message = MESSAGE_BODY;
				// operator = OPERATOR;
				operator = (OPERATOR == null ? null : OPERATOR.getBytes());
				// serviceID = SERVICEID;
				serviceID = (SERVICEID == null ? null : SERVICEID.getBytes());
				systemType = SYSTEMTYPE;
			}
		} else { // args specified on command line, so read them in
			systemID = args[0];
			userPass = args[1];
			try {
				port = Integer.parseInt(args[2]);
			} catch (NumberFormatException ne) {
				port = PORT;
			}			
			shortCode = args[3];
			msisdn = args[4];
			message = args[5];
			operator = args[6].getBytes();
			serviceID = args[7].getBytes();
			if (args.length > 8) {
				systemType = args[8];
			}
		}

        state = session.getSessionState();
        if (state == null || !state.isBound()) { // If not already bound in, bind in.
	        try {
	        	/*
	        	 * FUNCTION PROTOTYPE:
	        	 * 
	        	org.jsmpp.session.BindParameter.BindParameter(
	        			BindType bindType,
	        			String systemId,
	        			String password,
	        			String systemType,
	        			TypeOfNumber addrTon,
	        			NumberingPlanIndicator addrNpi,
	        			String addressRange)
	        	*/
	        	// int enquire_link_timer = session.getEnquireLinkTimer();
	        	// session.setEnquireLinkTimer(90000);       	
	            session.connectAndBind(SMPPG, port,
	            		new BindParameter(
	            				// BindType.BIND_TX, // transmitter
	            				// BindType.BIND_RX,
	            				BindType.BIND_TRX,
	            				systemID, // user name
	            				userPass,
	            				systemType,
	            				TypeOfNumber.NATIONAL, // TypeOfNumber.UNKNOWN,
	            				NumberingPlanIndicator.NATIONAL,
	            				null));
	            // DEBUG:
	            System.out.println("Bound in as " + systemID + ", Sytem Type " + systemType);
	
	            		// new BindParameter(BindType.BIND_TX, "test", "test", "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
	        } catch (IOException e) {
	            System.err.println("Failed connect and bind to host.");
	            e.printStackTrace();
	        }
        }
        
        // DataCoding dataCoding = GeneralDataCoding.newInstance(DATACODING);
        
        // SENDER
        // MessageSender sender = new MessageSender();
        // PUT BACK IN LATER:
        // String sentMT = sender.sendMessage(session, SERVICETYPE, shortCode, msisdn, dataCoding, message, op);
        // if (sentMT != null) sender.addMTStatus(sentMT);
        
        // RECEIVER
        // InboundMessage inMsg;
        launchResponderDaemon(session, port, systemID, userPass, systemType);
        
        // Integer PTTFinalResult = getNextInboundMessage(session);		
		// Integer PTTFinalResult = messageReceiverListener.getFinalDeliveryStatus(session); // comment this out for "fire and forget" MTs (don't wait for DRs)

    	// session.unbindAndClose();
	}
	
	public static void launchResponderDaemon(SMPPSession session, Integer port, String systemID, String userPass, String systemType) {
		launchResponderDaemon(session, port, systemID, userPass, systemType, null, null);
	}
	
	public static void launchResponderDaemon(SMPPSession session, Integer port, String systemID, String userPass, String systemType, String tmosid, String vzwsid) {
        
        InboundMessage inMsg;
        DataCoding dataCoding = GeneralDataCoding.newInstance(DATACODING);
        MessageSender sender = new MessageSender();

        SessionState state;        
        Integer bindRetries = MAXBINDRETRIES;
        Integer retryInterval = 30;
        Integer idleInterval = 30;

        if (session == null) {
    		// session = new SMPPSession();
    		System.err.println("Null session. Exiting...");
    		return;
    	}

        while (true) { // Run forever, until interrupted externally, or bind is lost and failed to reconnect after MAXBINDRETRIES retries.        	
        	// HANDLE BIND        	
        	state = session.getSessionState();
            if (state == null || !state.isBound()) { // If disconnected...
            	// If bind somehow gets disconnected, re-bind.            	
            	if (bindRetries-- <= 0) {
            		System.err.println("Giving up after " + bindRetries.toString() + " attempts to bind in, each with an interval of " + retryInterval.toString() + ". Quitting...");
            		return;
            	}
            	// Need to completely drop and re-initialize session here.
            	// Possible bug in connectAndBind method seems to prevent session from re-binding once connection
            	// has been forcefully terminated by external process (such as unplugging and re-plugging the network cable)
            	// so need to create new session.
        		session = new SMPPSession(); // relying on GC to clean up old assignment of session
        		session.removeSessionStateListener(null);
                session.setTransactionTimer(10000L);
                session.setEnquireLinkTimer(30000);

            	// session.unbindAndClose();
            	try {
	                session.connectAndBind(SMPPG, port,
	                		new BindParameter(
	                				BindType.BIND_TRX,
	                				systemID, // user name
	                				userPass,
	                				systemType,
	                				TypeOfNumber.NATIONAL, // TypeOfNumber.UNKNOWN,
	                				NumberingPlanIndicator.NATIONAL,
	                				null));
		            // DEBUG:
		            System.out.println("Bound in as " + systemID + ", Sytem Type " + systemType);
            	} catch (IOException e) {
            		System.err.println("IOException: " + e.toString());
            			e.printStackTrace();
	                System.err.println("Failed to bind to host. Sleeping for " + retryInterval.toString() + " seconds, then retrying..."); // " Retrying " + bindRetries.toString() + " more times.");	                
	    			SMPPUtilities.sleepSec(retryInterval);
	    			continue;
            	}
            	bindRetries = MAXBINDRETRIES;
            }
            
            // HANDLE INBOUND MESSAGES
            inMsg = getNextInboundMessage(session);
        	if (inMsg != null) {
        		// process message here
        		if (inMsg.messageType == SMPPUtilities.DR) {
        			// handle as a DR
        			// String phoneNum = inMsg.localDeliverSm.getSourceAddr();
        			String phoneNum = inMsg.msisdn;
        			Integer pttCode = inMsg.PTTCode;
        			System.out.println("DR received. PTT " + pttCode.toString() + " to phone " + phoneNum);
        			if (inMsg.messageID > 0) {
        				String messageIDString = String.valueOf(inMsg.messageID);
        				System.out.println("Previous status: " + sender.getMTStatus(messageIDString) + ". New status: " + pttCode.toString());
        				sender.updateMTStatus(messageIDString, pttCode);
        			}        			
        		} else if (inMsg.messageType == SMPPUtilities.MO) {
        			// handle as an MO
        			String phoneNum = inMsg.msisdn;
        			String msgOperator = inMsg.operator;
        			// String SC = inMsg.localDeliverSm.getDestAddress();
        			String SC = inMsg.shortCode;
        			String msg = inMsg.messageBody;
        			String serviceID = null;
        			if (inMsg.operator.equals(TMOBILE_ID)) {
        				serviceID = tmosid;
        			} else if (inMsg.operator.equals(VERIZON_ID)) {
        				serviceID = vzwsid;
        			}
        			System.out.println("MO received. From " + phoneNum + ", to short code " + SC + ", message: " + msg);
        			// Now, respond to subscriber
        			// TODO: add response logic here - specific response based on keyword
        			// TO DO: find a way to get data coding as well as the message from lookupResponse(), so it can be included in sendMessage().
        			String message = SMPPUtilities.lookupResponse(msg, SC);
        			System.out.println("Responding to subscriber with message: \"" + message + "\"");
        			// sender.sendMessage(session, SERVICETYPE, shortCode, phoneNum, dataCoding, message, op);
        			String sentMT = sender.sendMessage(session, SMPPUtilities.SERVICETYPE, SC, phoneNum, msgOperator, dataCoding, message, serviceID);
        			// add sentMT to outstandingMTs list.
        			if (sentMT != null) sender.addMTStatus(sentMT);
        		} else { // some other message type received
        			System.out.println("Unknown inbound message type received. ESM Class: " + Byte.toString(inMsg.localDeliverSm.getEsmClass()) + ", deliver_sm string: " + inMsg.localDeliverSm.toString());
        		}
        	} else {
    			try {
    				Thread.sleep(idleInterval * 1000);
    			} catch (InterruptedException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
        	}
        } // end of while loop
	} // end of method
}