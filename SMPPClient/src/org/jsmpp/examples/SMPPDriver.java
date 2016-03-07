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
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

// import org.jsmpp.examples.ReceiveSubmittedMessageExample;
// import org.jsmpp.examples.SMPPResponder;
// SimpleReceiveExample;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import java.nio.charset.Charset;

// import org.jsmpp.bean.

// TODO: Create native executable file for this application, and handle command-line arguments
// See:
// http://wadeawalker.wordpress.com/2010/10/24/tutorial-creating-native-binary-executables-for-multi-platform-java-apps-with-opengl-and-eclipse-rcp/
// http://stackoverflow.com/questions/5293321/create-a-java-executable-with-eclipse


/**
 * @author uudashr
 *
 */
/*
 * Usage: java -jar SMPPClient.jar -d -port 3208 -user mBloxAlertsUS -pass zA24k6X3 -msisdn 12063802788 -operator 31010 -dcs 8 -x -message 00480065006c006c006f
 * 		  java -jar SMPPClient.jar -d -port 3210 -user mBloxAlertsMT -pass noUJ98zv -msisdn 12063802788 -dcs 8 -x -message "00480065006c006c006f" -host smpp.mt.us.mblox.com
 * 		  java -jar SMPPClient.jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 12063802788 -dcs 8 -x -message "00480065006c006c006f" -host smpp.mt.us.mblox.com
 *        java -jar SMPPClient.jar -d -port 3204 -user mBloxDemoUS -pass P7asT6fr -msisdn 12063802788 -message Test_message -operator 31010 -systemtype mBloxDemo1 -resp
 *        	(system type: mBloxDemo1, keyword: ALLBLOX)
 *        java -jar SMPPClient.jar -d -port 3209 -user KBMGUS -pass vTjRd6Q5 -shortcode 28444 -msisdn 16505206212 -message Hi_there -operator 31005  -sidtmo 63750 -sidvzw 63750 -systemtype KBMG1 -response
 *        java -jar SMPPClient.jar -d -port 3100 -user GoogleUS-Prod -pass tz53XLpy -systemtype "" -notariff -operator 31005 -msisdn 16503154828 -message Test_message -host 1e100.us.lb.mblox.com
 *        java -jar SMPPClient.jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 16509540388 -dcs 0 -message "Hey there" -host smpp.mt.us.mblox.com
 *        java -jar SMPPClient.jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 12063802788 -dcs 1 -message 48656c6c6f31 -x -host smpp.mt.us.mblox.com
 *        java -jar SMPPClient.jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 16505548904 -sid 26952 -dcs 1 -x -message 000102030405060708090a0b0c0d0e0f -host smpp.mt.us.mblox.com
 *        java -jar SMPPClient.jar -d -port 3204 - user mBloxDemoUS -pass P7asT6fr -msisdn 16503159224 -operator 31005 -sid 26952 -dcs 1 -x -message 000102030405060708090a0b0c0d0e0f -host smpp.mt.us.mblox.com
 *        java -jar SMPPClient.jar -d -port 3208 -user mBloxAlertsUS -pass zA24k6X3 -msisdn 14085551212 -operator 31005 -dcs 1 -x -message 000102030405060708090a0b0c0d0e0f1011
 *        MESSAGE CONCATENATION TESTS:
 *        java -jar SMPPClient.jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 12063802788 -dcs 8 -x -sid 26952 -concat -message a3a4a5a7bfc4c5c6c7c9d1d6d8dcdfe0e4e5e6e8e9ecf1f2f6f8f9fca3a4a5a7bfc4c5c6c7c9d1d6d8dcdfe0e4e5e6e8e9ecf1f2f6f8f9fca3a4a5a7bfc4c5c6c7c9d1d6d8dcdfe0e4e5e6e8e9ecf1f2f6f8f9fca3a4a5a7bfc4c5c6c7c9d1d6d8dcdfe0e4e5e6e8e9ecf1f2f6f8f9fca3a4a5a7bfc4c5c6c7c9d1d6d8dcdfe0e4e5e6e8e9ecf1f2f6f8f9fca3a4a5a7bfc4c5c6c7c9d1d6d8dcdfe0e4e5e6e8e9ecf1f2f6f8f9fc -host smpp.mt.us.mblox.com
 *        java -jar SMPPClient.jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 12063802788 -dcs 8 -sid 26952 -concat -message "This is a long test message. It exceeds the standard 160 characters, and is, in fact longer. It is actually over the standard message length and takes up an entire 180 characters."
 *        java -jar -d -port 3203 -user mBloxTestMT -pass 5Pa8AWru -msisdn 16504681077 -dcs 8 -sid 26952 -x -concat -message "Hi. This msg should be followed by: ? Google ????? xyz123. Here:You should not see any of this" -host smpp.mt.us.mblox.com
 *        java -jar -d -port 3100 -user GoogleUS-UAT -pass wWSfyVyL -notariff -operator 31005 -msisdn 16504681077 -dcs 8 -concat -message "Hi. This msg should be followed by: ? Google ????? xyz123. Here:You should not see any of this" -host 1e100.us.lb.mblox.com
 *        -d -port 3206 -user EpsilonUS -pass tSGBzHBq  -msisdn 16785592064 -operator 31002 -dcs 0 -message "EpsilonUS" -shortcode
 *        java -jar -d -bindtime 60 -port 3204 -user mBloxDemoUS -pass P7asT6fr -msisdn 12063802788 -message Test_message -operator 31010 -systemtype mBloxDemo1
 *        -d -port 3208 -user mBloxAlertsUS -pass zA24k6X3 -bindtime 300 -msisdn 12063802788 -operator 31010 -shortcode 41543 -dcs 0 -message "Test message." -host smpp.psms.us.mblox.com
 *        -d -port 9000 -user mbloxja1 -pass SiGX7tHL -host sms1.mblox.com -notariff -msisdn 447879997725 -message Test_msg
 *        -d -port 9000 -user mbloxja1 -pass SiGX7tHL -host sms1.mblox.com -notariff -responder
 */

public class SMPPDriver {
// 48 65 6c 6c 6f
// -sid 60522 -message 0x48656c6c6f
	/*JE
	private static final String SYSTEMID = "mBloxAlertsUS";
	private static final String PASSWORD = "zA24k6X3"; // MBX
	/*
	// mBloxAlertUS service IDs:
	// private static final String SERVICEID = "60521"; // TMO
	// private static final String SERVICEID = "60522"; // VZW
	private static final String SERVICEID = null; // ATT, SPR, etc.
	// Other SIDs for mBloxAlertsUS/VZW 60522, 60516
	// Valid TMO SIDs for mBloxAlertsUS: 60519,60520,60521

	private static final String SHORTCODE = "28444";
	// private static final String MSISDN = "14042329644"; // DOUG - TMO
	private static final String MSISDN = "12063802788"; // DeviceAnywhere - TMO
    private static final String OPERATOR = "31010";
    */
    
    // ClairmailCA sWe3ec8a 3208 62569 14168925821 "Test here: ôêÊ" 30213 0
	// private static final String SYSTEMID = "ClairmailCA";
	// private static final String PASSWORD = "sWe3ec8a"; // MBX
	// mBloxAlertUS service IDs:
	// private static final String SERVICEID = "60521"; // TMO
	// private static final String SERVICEID = "60522"; // VZW
	/*JE
	private static final String SERVICEID = null; // ATT, SPR, etc.
	// Other SIDs for mBloxAlertsUS/VZW 60522, 60516
	// Valid TMO SIDs for mBloxAlertsUS: 60519,60520,60521
	private static final String SHORTCODE = "28444";
	// private static final String MSISDN = "14042329644"; // DOUG - TMO
	private static final String MSISDN = "12063802788"; // DeviceAnywhere - ATT
    private static final String OPERATOR = "31010";
    JE*/
    // Telus: 30213
    // Rogers: 30210
    // Bell: 30211

    // private static final String MESSAGE_BODY = "Test 2 - DCS 8"; // "Flash SMS message?";
    // private static final String MESSAGE_BODY = "\ud83c" + "\udf55" + "";
    // private static final String MESSAGE_BODY = 0xd83c + "" + 0xdf55;
    private static final String MESSAGE_BODY = "Test";

    // private static final byte[] MsgBytes = {(byte) 0x00, (byte) 0x01, (byte) 0xf3, (byte) 0x55};
    // private static final byte[] MsgBytes = {(byte) 0x00, (byte) 0x32};
    // private static final byte[] MsgBytes = {(byte) 0x00, (byte) 'T', (byte) 0x00, (byte) 'e', (byte) 0x00, (byte) 's', (byte) 0x00, (byte) 't'};
    // private static final byte[] MsgBytes = {(byte) 0x00, (byte) 'T', (byte) 0x00, (byte) 'e', (byte) 0xd8, (byte) 0x3c};   
    private static final byte[] MsgBytes = {(byte) 0xd8, (byte) 0x3c, (byte) 0xdf, (byte) 0x55};
	// private static final byte DATACODING = (byte) 0x08; // 0x08 is Unicode, 0x00 is default
	// private static final String PROGRAMTYPE = "stdrt"; // for use with PROGRAM_TAG
	private static final String PROGRAMTYPE = null;

	private static final Integer PORT = 3208; // MBX
	// private static final Integer PORT = 3203; // RN
	private static final String SYSTEMTYPE = "mbloxjsmpp";
	private static final String SERVICETYPE = "-1"; // "31884";
	private static final String INVALID_NUMBER_ERROR = "00000408";

	private static final Integer MAX_DR_WAIT_TIME = 240;
	private static final Integer MAX_DRs = 3;
	private static final Integer MAX_BIND_ATTEMPTS = 4;
	
	private static String SMPPG = null;
	
	private static final byte[] TARIFF = {'0','0','0','0','0'};

	private static final short OPERATOR_TAG = 0x1402;
	private static final short TARIFF_TAG = 0x1403;	
	private static final short SESSIONID_TAG = 0x1404;
	private static final short SERVICEID_TAG = 0x1407;
	private static final short PROGRAM_TAG = 0x1542;
	
	private static final String VERIZON_ID = "31003";
	private static final String TMOBILE_ID = "31004";
	
	// private static final String MESSAGE_BODY = "@£$¥èéùì";
	// private static final String MESSAGE_BODY = "SMPP test, with comma.";
	// private static final String MESSAGE_BODY = "Test text message II.";
	// private static final String MESSAGE_BODY = "sendmemo 0";

	private static final String DELIVER_MINUTES = "00";
	private static final String DELIVER_SECONDS = "05";	
	// private byte [] messageBytes;
	
    String DEFAULT_SERVICEID = null;
    String DEFAULT_OPERATOR = null;
    String DEFAULT_SHORTCODE = "28444";

    static boolean receiveDRs = false;
    static boolean useTariff = true;
    static boolean isResponder = false;
    static boolean isHexMessage = false;
    static boolean isConcatenated = false;
    static boolean useSSL = false;
	
	//private int message_type = 0;
	
	@SuppressWarnings("deprecation") // suppress warnings about Data methods being deprecated
	private static String getTimeStamp()
	{
		long currentTime = System.currentTimeMillis();
		Date d = new Date();
		d.setTime(currentTime);

		/*
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTime);
		*/
		// 1431455663
		Calendar cal = Calendar.getInstance();
		long millis = cal.getTimeInMillis();
		cal.setTimeInMillis(millis);
		
		// cal.setTime(d);
		String rightNow = String.valueOf(cal.HOUR_OF_DAY) + ":" + String.valueOf(cal.MINUTE) + ":" + String.valueOf(cal.SECOND);

		/*
		return (rightNow + " OR " + String.valueOf(d.getHours()) + ":" +
				String.valueOf(d.getMinutes()) + ":" +
				String.valueOf(d.getSeconds()));
		*/
		return (String.valueOf(d.getHours()) + ":" +
				String.valueOf(d.getMinutes()) + ":" +
				String.valueOf(d.getSeconds()));

	}
	
	private static void displayVersionInfo() {
		System.out.println("alpha 1 (12/26/2015) Initial release. Handles command-line arguments and\n\ttDelivery Receipts.");
		System.out.println("alpha 2 (01/22/2015) Follow-up alpha version. Contains minor bug fixes.");
		System.out.println("1.0a    (04/14/2015) Added option to wait for Delivery Receipts or just send\n\ttand exit.\n\t\tAdded support for Unicode messages and custom hostnames for\n\t\tSMPPG\n\t\tChanged command-line argument format to use switches, so\n\t\targuments can be in any order.");
		System.out.println("1.0b    (04/16/2015) Added MO-handling and autoresponder functionality");
		System.out.println("1.5     (04/21/2015) Added support for text messages in binary (hex) format\n\t\tModified format for hexadecimal and Unicode messages to\n\t\texclude leading \"0x\".");
		System.out.println("2.0     (04/23/2015) Fixed bug in which application would hang if it received certain message rejections.\n\t\tAdded message format checking when hexadecimal mode (-x) is used");
		System.out.println("2.1     (04/24/2015) Added error text to be written to sdtout when submit fails\n\t\tAdded ability back in to parse Unicode message in 0x1234abcd format for backwards compatibility\n\t\tImproved efficiency in submitting Uncode-formatted Latin-based\n\t\ttext message bodies.");
		System.out.println("2.2     (04/28/2015) Refactored code to merge two packages into one (to use\n\t\tcommon methods for sending one-time MTs and autoresponder MTs).");
		System.out.println("2.3     (04/29/2015) Added support for concatenated messages.");
		System.out.println("2.3.1   (04/30/2015) Added pseudo-randomized reference number to concatenated\n\t\tmessages.\n\t\tAdded Unicode support for concatenated messages.");
		System.out.println("2.3.2   (05/06/2015) Fixed concatenation bug. When total message length was an even multiple of 153 (for 7-bit chars), last segment would get dropped."); 
		System.out.println("2.4.0   (05/11/2015) Added support for user-specified bind/unbind times (-bindtime switch).");
		System.out.println("2.4.1   (10/08/2015) Fixed bug in -bindtime option that caused application to exit when final DR was received instead of keeping bind open.");
		System.out.println("2.5.0   (11/15/2015) Bug with application missing some MOs and DRs when in autoresponder mode appears to be fixed. (Please notify Jason if you notice missing DRs or MOs.)");
		System.out.println("2.5.1   (11/??/2015) Added -systemType to help menu.");
		System.out.println("2.6.0   (12/??/2015) Added support for Atlas platform (primarily the ability to handle Atlas-style message IDs in submit responses, and treating PTT 0 as a final delivery status (Atlas uses this to mean successful delivery)).");
		System.out.println("Possible future features:\n- Configurable autoresponder MT message bodies, based on MO keyword.\n- Ability for autoresponder to send MTs as Unicode.\n- Support for SSL encryption.\n- Support for configurable reference number in concatenated messages.\n- Fix bindtime handling to unbind when it is supposed to (rather than holding onto bind for longer than specified).\n- Fix bug involving IO exception when DR is sent between bind and MT submission.");
	}
	
	private static void displayHelpOptions() {
		System.out.println("Syntax: SMPPclient [option]");
		System.out.println("Options");
		System.out.println(" -d: enable delivery receipts and wait for them after sending MT");
		System.out.println(" -bindtime [sec]: keep bind open for specified time, then unbind and close connection. A value of 0 causes the SMPP client to drop the bind immediately after sending the message. This is similar to omitting the -d switch.");
		// System.out.println(" -bindmax [sec]: keep bind open for specified time unless all DRs have been received.");
		System.out.println(" -notariff: do not include tariff parameter in submission");
		// System.out.println(" -ssl: use SSL encryption for all SMPP traffic between this application and the SMPPG.");
		System.out.println(" -responder: run in autoresponder mode - listen for MOs and reply with MTs");
		System.out.println(" -host [hostname]: specify the hostname (optional). If left out, the standard hostname for either two-way or Sure Route (depending on whether an operator ID has been included) will be used.");
		System.out.println(" -port* [port number]: specify the port number");
		System.out.println(" -user* [user name]: specify the user name (system ID)");
		System.out.println(" -pass* [password]: specify the password");
		System.out.println(" -operator [operator ID]: specify the operator ID (optional). If an operator is specified SMPPclient automatically selects the PSMS platform host (smpp.psms.us.mblox.com) as the SMPP gateway. If no operator is specified or operator is set to \"0\", SMPPclient selects the Sure Route platform host (smpp.mt.us.mblox.com) as the SMPP gateway.");
		System.out.println(" -sid [service ID]: specify the service ID (optional)");
		System.out.println(" -sidvzw [Verizon service ID]: specify the service ID to be used for MTs to Verizon phones");
		System.out.println(" -sidtmo [T-Mobile service ID]: specify the service ID to be used for MTs to T-Mobile phones");			System.out.println(" -shortcode [short code]: specify the short code");
		System.out.println(" -msisdn* [phone number of recipient]: specify the destination phone number");
		System.out.println(" -dcs [data coding for message]: specify the datacoding (DCS) value for encoding the message");
		System.out.println(" -x: message is encoded in hexadecimal.");
		System.out.println(" -b: binary. Same as -x.");
		System.out.println(" -concat: send as concatenated message. Note: message must be longer than standard message length to use this option.");
		System.out.println(" -systemtype [system type]: include a specific system type in the bind request.");			
		System.out.println(" -message [message body]: specify the body of the text message");
		System.out.println(" -version: version history and notes on current version, including bug fixes and new features");
		System.out.println("* Required value");
		System.out.println("\nExample:\nSMPPclient -d -port 3208 -user MbloxClientUS -pass abcd1234 -shortcode 28444 -msisdn 14085551212 -operator 31002 -message \"Test Message\"");
		System.out.println("To send a Unicode message with non-Latin characters, use DCS 8 and the -x switch, and include the message in hex binary format. For example, use \"-x -message 00480065006c006c006f\" for \"Hello\".");
	}
	
	private static String receiveMessage(SMPPSession session, Integer timeout) {
		return receiveMessage(session, timeout, null, null);
	}

	private static String receiveMessage(SMPPSession session, long endTime) {
		return receiveMessage(session, endTime, null, null);
	}

	// private static String receiveMessage(SMPPSession session, Integer timeout,
	// 		String serviceType, String shortcode) {
	private static String receiveMessage(SMPPSession session, long endTime,
			String serviceType, String shortcode) {
		
		// boolean continueListening = true;
		long startTime = System.currentTimeMillis() / 1000;

		// MessageReceiverListenerImpl messageReceiverListener = new MessageReceiverListenerImpl();
		MessageReceiverDR messageReceiverListener = new MessageReceiverDR();
		// messageReceiverListener.onAcceptDeliverSm(deliverSm)

		session.setMessageReceiverListener(messageReceiverListener);

		String message = null;
		// Integer counter = timeout + 10;
		// Integer counter = 60*60*24*2; // seconds in 2 full days
		System.out.println("Waiting for inbound MO or DR message");
		
		// while (message == null && counter-- > 0 && (System.currentTimeMillis() / 1000) < timeout) {
		// while (message == null && counter-- > 0) {
		// while (message == null && (System.currentTimeMillis() / 1000) < startTime + (long) timeout && counter-- > 0) {
		// while (continueListening) {
		while (message == null && (System.currentTimeMillis() / 1000) < endTime) {
        	SMPPUtilities.sleepSec(1);
	 		System.out.print('.');
			// TODO: Separate them out
	 		// message = "type:" + messageReceiverListener.messageType + " " + messageReceiverListener.receiveMsg;
	 		message = messageReceiverListener.receiveMsg;
			// message_type = messageReceiverListener.messageType;
		}

		System.out.println();
		if (message == null) {
			System.out.println("Wait timed out. No message received.");
			// if (counter <= 0) System.out.println("Counter reached 0.");
			if (System.currentTimeMillis() / 1000 >= endTime) System.out.println("Bindtime expired.");
		} else {
			message = messageReceiverListener.receiveMsg + " type:" + String.valueOf(messageReceiverListener.messageType);
			//// message = messageReceiverListener.receiveMsg;
			// message = " type:" + String.valueOf(messageReceiverListener.messageType) + messageReceiverListener.receiveMsg;
			// debug:
			// System.out.println("Inbound message type: " + messageReceiverListener.messageType + ", bytes: " + MessageType.MASK_BYTE);
			
			//// System.out.println("Inbound message (DR/MO) received: " + message);
			//// System.out.println("Inbound " + (messageReceiverListener.messageType == MessageReceiverDR.DR_MSG ? "DELIVERY RECEIPT " : (messageReceiverListener.messageType == MessageReceiverDR.MO_MSG ? "MOBILE ORIGINATED " : "TYPE UNKNOWN ")) + "message received: " + message);
			// System.out.println("Delivery Receipt received: " + message);
		}
		return message;
	}
	
	// private static String waitForDeliveryReceipt(SMPPSession session, Integer timeout) {
	private static String waitForDeliveryReceipt(SMPPSession session, long endTime) {
        // Wait for and accept Delivery Receipt
        String PTTStatus;
        String PTTCodeString;
        String msgTypeString;
    	// String DR_message = receiveMessage(session, SERVICETYPE, shortCode);
        String message = receiveMessage(session, endTime);

        // Sample returned string:
    	// id:1671112327 sub:001 dlvrd:001 submit date:1410102256 done date:1410102255 stat:ACKED   err:003 text: 
        
        // First, check returned message
        if (message == null)
        {
        	return ("000,Timeout");
        }
       
        // If we got to this point, message is not null, so parse message
        // int msgID  = DR_message.indexOf("id:") + 3;
        int msgTypeStart = message.indexOf("type:");
        int msgTypeEnd = -1;
        if (msgTypeStart > -1) { // if a "type" was found in the string
        	msgTypeStart += 5;
        	msgTypeEnd = message.indexOf(" ", msgTypeStart + 1); // need + 1???
        }
        
    	int PTTstart = message.indexOf("err:") + 4;
    	int PTTend = message.indexOf(" ", PTTstart + 1);
    	int StatusStart = message.indexOf("stat:") + 5;
    	int StatusEnd = message.indexOf(" ", StatusStart);
    	
    	msgTypeString = (msgTypeStart > 0 ?
    						(msgTypeEnd > msgTypeStart ?
		    					message.substring(msgTypeStart, msgTypeEnd) :
		    					message.substring(msgTypeStart)) :
		    			"");
        // Check to make sure it is a DR. If it is an MO, just print it out and ignore.
        if (msgTypeString.equals(SMPPUtilities.MO.toString())) {
        	//// System.out.println("Ignoring MO: \"" + message.substring(0, msgTypeStart-6) + "\"");
        	System.out.println("Received MO: \"" + message.substring(0, msgTypeStart-6) + "\"");
        	return null;
        }

    	PTTCodeString = message.substring(PTTstart, PTTend);
    	PTTStatus = message.substring(StatusStart, StatusEnd);
    	
    	// sample return format: "003,ACKED"
    	// return PTTCodeString + "," + PTTStatus;
    	return PTTCodeString + "," + PTTStatus + "," + msgTypeString;
    	
    	/*
    	try {
    		PTTCode = Integer.parseInt(DR_message.substring(PTTstart, PTTend));
    	} catch (NumberFormatException nfe) {
    		PTTCode = -1;
    		nfe.printStackTrace();
    	}
    	*/
	}
	
	private static Integer getFinalDeliveryStatus(SMPPSession session) {
		// return getFinalDeliveryStatus(session, MAX_DR_WAIT_TIME);
		return getFinalDeliveryStatus(session, -1);
		
		// START HERE for bindtime
	}
	
	private static Integer getFinalDeliveryStatus(SMPPSession session, Integer timeout) {
        // Wait for and accept Delivery Receipt
        String PTTStatus = null;
        Integer PTTCode = -1;
        Integer DRsReceived = 0;
        long endTime;
        boolean extendedListening = false;
        boolean keepListening = true;
        
        if (timeout > 0) {
        	extendedListening = true;
        	endTime = System.currentTimeMillis() / 1000 + timeout;
        } else {
        	// extendedListening = false; (implied)
        	endTime = System.currentTimeMillis() / 1000 + MAX_DR_WAIT_TIME;
        }

        // while (((PTTCode == 0 || PTTCode == 3) && DRsReceived < MAX_DRs && (System.currentTimeMillis() / 1000) < endTime)
        //		|| (extendedListening == true && (System.currentTimeMillis() / 1000) < endTime)) {
        while (keepListening) {
        	if (extendedListening == false) { // bindTime option is NOT being used
        		if ((PTTCode != -1 && PTTCode != 3) || DRsReceived >= MAX_DRs || (System.currentTimeMillis() / 1000) >= endTime) {
        		// Changed PTTCode != 0 to PTTCode != -1 and changed rest of code to set PTT code to -1
        		// when none received, since PTT 0 is a final delivery status for Atlas.
        			keepListening = false;
        			break;
        		}
        	} else { // extendedListening is true (bindTime option was used)
        		if ((System.currentTimeMillis() / 1000) >= endTime) {
        			keepListening = false;
        			break;
        		}
        	}

        	// Continue receiving DRs until DR with PTT 4, PTT 6, or a valid PTT error code has been received        	
	        // String PTTCodeAndStatus = waitForDeliveryReceipt(session, timeout);
	        String PTTCodeAndStatus = waitForDeliveryReceipt(session, endTime);

	        // MUST detect -1 PTT code (no DR yet) and continue.
	        // Parse out messageType for now. Might use later.
	        if (PTTCodeAndStatus != null) {
	        	String [] tempArray = PTTCodeAndStatus.split(",", 3);
	        	if (tempArray.length >= 3) {
			    	try {
			    		PTTCode = Integer.parseInt(tempArray[0]);
			    	} catch (NumberFormatException nfe) {
			    		PTTCode = -1;
			    		nfe.printStackTrace();
			    	}
					PTTStatus = tempArray[1];
					System.out.println(getTimeStamp() + " Message status: PTT code " + PTTCode.toString() + ", Status " + PTTStatus);
			    	String messageTypeString = tempArray[2];
			    	DRsReceived++;
	        	}
	        }
        }
        return PTTCode;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		To add:
			-originator (if [0-9]* then { if < 8 digits, TON = shortcode, else TON = numeric } else { if [0-9a-zA-Z]* TON = alpha, else invalid. }
			-serviceID (same as -sid)
		*/
		
        String DEFAULT_SERVICEID = null;
        String DEFAULT_OPERATOR = null;
        String DEFAULT_SHORTCODE = "28444";
        String DEFAULT_MESSAGE = "Test message";
        Integer DEFAULT_DATACODING = 0x00;
        Integer DEFAULT_BIND_TIME = 60;

		String systemID = null;
		String userPass = null;
		Integer port = 0;
		String shortCode = DEFAULT_SHORTCODE;
		// String data_coding = DEFAULT_DATACODING;
		String message = DEFAULT_MESSAGE;
		Integer data_coding = DEFAULT_DATACODING;
		String msisdn = null;
		// byte[] operator;
		String operator = null;
		// byte[] serviceID;
		String serviceID = null;
		String VZWserviceID = null;
		String TMOserviceID = null;
		String programType;
		String systemType = null;
		String serviceType = null;
		Integer bindTime = -1;
		Integer bindStart = 0;
		byte [] messageBytes;
        OptionalParameter op[] = null;

		System.out.println("SMPPclient by Jason Epstein, Mblox. Version 2.5.1 beta (Feb. 2016)\n" +
						   "Note: This version uses the legacy Mblox platform (2015 and earlier). This version is not compatible with the Atlas platform.\n" +
						   "Find most recent version at https://drive.google.com/a/mblox.com/folderview?id=0B8tJQW-kZiPKVUU1S2pjZTc1Z0U&usp=sharing \n");

		if (args.length == 0) { // no args were included
			System.out.println("Error: missing arguments. For help, run command with -h. For list of versions, run with -versions.");
			return;
		}
		if (args.length == 1 && (args[0].equals("-version") || args[0].equals("-versions") || args[0].equals("-ver") || args[0].equals("-ver") || args[0].equals("-f") || args[0].equals("-features"))) {
			displayVersionInfo();
			return;
		}
		
		if (args.length == 1 && (args[0].equals("-h") || args[0].equals("-help"))) {
			// display help options
			displayHelpOptions();
			return;
		}        
        
		SMPPSession session = new SMPPSession();
        session.setTransactionTimer(10000L);
        session.setEnquireLinkTimer(30000);
        
        // Parse arguments
        for (int i = 0; i < args.length; i++) {
        	// System.out.println("DEBUG: arg " + String.valueOf(i) + ": " + args[i]);
        	// System.out.println("DEBUG: arg " + String.valueOf(i+1) + ": " + args[i+1]);
        	if (args[i].startsWith("-")) {
        		if (args[i].equals("-d")) {
        			receiveDRs = true;
        		} else if (args[i].equals("-notariff") || args[i].equals("-notar")) {        			
        			useTariff = false;
        		} else if (args[i].equals("-ssl") || args[i].equals("-SSL")) {
        			useSSL = true;
        		} else if (args[i].equals("-responder") || args[i].equals("-respond") || args[i].equals("-resp") || args[i].equals("-response")) {        			
        			isResponder = true;        		
        		} else if (args[i].equals("-x") || args[i].equals("-hex") || args[i].equals("-b") || args[i].equals("-bin")) {        			
        			isHexMessage = true;        		
        		} else if (args[i].equals("-concat") || args[i].equals("-concatenated")) {
        			isConcatenated = true;
        		} else if (args[i].equals("-prop")) {
        			Properties prop = System.getProperties();
        			System.out.println(prop.toString());
        		} else if (args[i].equals("-port")) {
        			i++;
        			if (i == args.length || args[i].startsWith("-")) {
        				System.out.println("Error: Missing value for \"port\".");
        				return;
        			}
        			try {
        				port = Integer.parseInt(args[i]);
        				// i++;
        			} catch (NumberFormatException nfe) {
        				// reset port to default
        				System.out.println("Invalid value for port: " + args[i]);
        				System.out.println(nfe.toString());
        				return;
        			}
        		} else if (args[i].equals("-user")) {
        			i++;
        			if (i == args.length || args[i].startsWith("-")) {
        				System.out.println("Error: Missing value for \"user\".");
        				return;
        			}
        			systemID = args[i];
        			// i++;
        		} else if (args[i].equals("-pass")) {
        			i++;
        			// if (i == args.length || args[i].startsWith("-")) continue;
        			// userPass = args[i];
        			if (i == args.length || args[i].startsWith("-")) {
        				System.out.println("Error: Missing value for \"pass\".");
        				return;
        			}
        			userPass = args[i];
        		} else if (args[i].equals("-msisdn")) {
        			i++;
        			if (i == args.length || args[i].startsWith("-")) {
        				System.out.println("Error: Missing value for \"msisdn\".");
        				return;
        			}
        			if (args[i].matches("[0-9]*")) {       				
        				msisdn = args[i];
        				// i++;
        			} else {
        				System.out.println("Invalid format for -msisdn argument: " + args[i]);
        				return;
        			}
        		} else if (args[i].equals("-operator") || args[i].equals("-operatorID") || args[i].equals("-operatorid")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			if (args[i].matches("[0-9]*")) {       				
        				operator = args[i];
        				// i++;
        			} else {
        				operator = DEFAULT_OPERATOR;
        			}
        		} else if (args[i].equals("-sid") || args[i].equals("-serviceID") || args[i].equals("-serviceid")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			if (args[i].matches("[0-9]*")) {       				
        				serviceID = args[i];
        				// i++;
        			} else {
        				serviceID = DEFAULT_SERVICEID;
        			}        			
        		} else if (args[i].equals("-sidvzw") || args[i].equals("-vzwsid") || args[i].equals("-verizonserviceid")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			if (args[i].matches("[0-9]*")) {       				
        				VZWserviceID = args[i];
        				// i++;
        			} else {
        				VZWserviceID = DEFAULT_SERVICEID;
        			}        			
        		} else if (args[i].equals("-sidtmo") || args[i].equals("-tmosid") || args[i].equals("-tmobileserviceid")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			if (args[i].matches("[0-9]*")) {       				
        				TMOserviceID = args[i];
        				// i++;
        			} else {
        				TMOserviceID = DEFAULT_SERVICEID;
        			}
        		} else if (args[i].equals("-shortcode")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			if (args[i].matches("[0-9]*")) {       				
        				shortCode = args[i];
        				// i++;
        			} else {
        				shortCode = DEFAULT_SHORTCODE;
        			}
        		} else if (args[i].equals("-dcs") || args[i].equals("-datacoding") || args[i].equals("-DCS")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			/*
        			if (args[i].matches("[0-9]*")) {       				
        				data_coding = args[i];
        				// i++;
        			} else {
        				data_coding = DEFAULT_DATACODING;
        			}
        			*/
	    			try {
	    				data_coding = Integer.parseInt(args[i]);
	    				// i++;
	    			} catch (NumberFormatException nfe) {
	    				// reset port to default
	    				System.out.println("Invalid data coding value: " + args[i]);
	    				System.out.println(nfe.toString());
	    				return;
	    			}
        		} else if (args[i].equals("-message")) {
        			// i++;
        			// if (args[i].startsWith("-")) {
        			// 	message = DEFAULT_MESSAGE;
        			// 	continue;
        			// }
        			if (i+1 == args.length || args[i+1].startsWith("-")) {
        				message = DEFAULT_MESSAGE;
        			} else {
        				i++;
        				message = args[i];
        			}
        			// i++;
        		} else if (args[i].equals("-host") || args[i].equals("-hostname")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			// System.out.println("Error: Missing value for \"host\".");
        			i++;
        			// Validate hostname
        			// if (args[i].matches("[0-9a-zA-Z._]*")) {
        				SMPPG = args[i];
        			// }
        			// if not, just leave SMPPG as null.
        		} else if (args[i].equals("-systemtype") || args[i].equals("-systemType")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			// System.out.println("Error: Missing value for \"host\".");
        			i++;
        			systemType = args[i];
        		} else if (args[i].equals("-servicetype") || args[i].equals("-serviceType") || args[i].equals("-profileid") || args[i].equals("-profileID")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			// System.out.println("Error: Missing value for \"host\".");
        			i++;
        			serviceType = args[i];
        		} else if (args[i].equals("-bindtime") || args[i].equals("-bindTime")) {
        			if (i+1 == args.length || args[i+1].startsWith("-")) continue;
        			i++;
        			if (args[i].matches("[0-9]*")) {
        				try {
        					bindTime = Integer.parseInt(args[i]);
        				} catch (NumberFormatException nfe) {
    	    				// reset port to default
    	    				System.out.println("Invalid bind time value: " + args[i] + ". Setting bind time to default value of " + DEFAULT_BIND_TIME.toString() + " seconds.");
        					bindTime = DEFAULT_BIND_TIME;
        				}
        				// Long currentTime = System.currentTimeMillis() / 1000;
        				// bindStart = currentTime.intValue();
        			} else {
        				System.out.println("Invalid bind time value: " + args[i] + ". Setting bind time to default value of " + DEFAULT_BIND_TIME.toString() + " seconds.");
        				bindTime = DEFAULT_BIND_TIME;
        			}
        		} else { // default case
        			System.out.println("Unexpected switch: " + args[i]);
        		}
        	} else {
        		System.out.println("Unexpected argument: " + args[i]);
        	}
        }

        // check for required values
        if (port == 0) {
        	System.out.println("Error: missing port number");
        	return;
        }
        if (systemID == null) {
           	System.out.println("Error: missing account username (system ID)");
           	return;
        }
        if (userPass == null) {
        	System.out.println("Error: missing account password");
        	return;
        }
        if (!isResponder) { // if just sending MT message (not in autoresponder mode), need destination phone number
        	if (msisdn == null) {
	        	System.out.println("Error: missing recipient phone number");
	        	return;
	        }
        }
        if (isHexMessage) { // check message format
        	if (!message.matches("[0-9A-Fa-f]*")) {
        		System.out.println("Error: hex format specified, but message is not in hex format: " + message);
        		return;
        	}
        }
        /*
        if (isResponder && bindTime == 0) {
        	error
        }
        */
        
        if (bindTime == 0) { // if bindtime has been set to 0, that means fire-and-forget. In that case, do not request delivery receipts in MTs.
        	receiveDRs = false;
        }

        // fill in default values for missing parameters that are optional
        if (systemType == null) systemType = SYSTEMTYPE;
        if (serviceType == null) serviceType = SERVICETYPE;

        /*
        // Convert message to Unicode bytes if required
        if (data_coding == 0x08) { // Unicode
        	if (message.matches("0[xX][0-9A-Fa-f]*")) {
        		String messageHex = message.substring(2);
        		// convert each pair of hex digits to 8-bit value
        		Integer numChars = messageHex.length() / 2;
        		messageBytes = new byte[numChars];
        		      		
        		try {
        			for (int i = 0; i < numChars; i++) {
        				byte b = (byte) Integer.parseInt("" + messageHex.charAt(i*2) + messageHex.charAt((i*2)+1), 16);
        				messageBytes[i] = b;
        			}
        		} catch (NumberFormatException nfe) {
        			// "HI"
        			messageBytes[0] = 0x48;
        			messageBytes[1] = 0x49;
        		}
        	} else { // DCS is 8, but message is not in 0x123456 format.        		
        		if (message.isEmpty()) { // make sure message is not empty.
        			messageBytes = message.getBytes();
        		} else {
        			Integer numChars = message.length();
	        		messageBytes = new byte[numChars*2]; // need two bytes (16 bits) per char, since this is in UTF-16.
	        		for (int i = 0; i < numChars; i++) {
	        			messageBytes[i*2] = 0;
	        			messageBytes[(i*2)+1] = (byte) message.charAt(i);
	        		}
        		}
        	}
        } else { // DCS is not 8
        	messageBytes = message.getBytes();
        }
        */
        
        // For backwards compatibility. Remove eventually.
        if (data_coding == 0x08) { // Unicode
        	if (message.matches("0[xX][0-9A-Fa-f]*")) {
        		isHexMessage = true;
        		message = message.substring(2);
        	}
        }
        
        // if (isHexMessage && message.matches("[0-9A-Fa-f]*")) { // shouldn't be needed
        if (isHexMessage) {
        	// Message example: 3031A1A2        	
        	String messageHex = message;
    		// convert each pair of hex digits to 8-bit value
    		Integer numChars = messageHex.length() / 2;
    		messageBytes = new byte[numChars];

    		try {
    			for (int i = 0; i < numChars; i++) {
    				// messageBytes[i] = Byte.valueOf(message.substring(i*2,(i*2)+2), 16);    				
    				messageBytes[i] = (byte) Integer.parseInt("" + messageHex.charAt(i*2) + messageHex.charAt((i*2)+1), 16);
    			}
    		} catch (NumberFormatException nfe) {
    			// This should never happen since the 'if (message.matches("0[xX][0-9A-Fa-f]*"))'
    			// statement above checks that all characters are valid hex before entering this code path.
    			// If this happens, post an error and exit.
    			// messageBytes[0] = 0x48; // 'H'
    			// messageBytes[1] = 0x49; // 'I'
    			nfe.printStackTrace();
    			System.out.println("Error: NumberFormat Exception parsing hex-formatted message: " + message);
    			return;
    		}
    	} else if (data_coding == 0x08) { // Unicode text string
    		Integer numChars = message.length();
    		String charSet = "UTF-16";
    		if (Charset.isSupported(charSet)) {
    			try {
					messageBytes = message.getBytes(charSet);
				} catch (UnsupportedEncodingException e) {
					// This should never happen since we just checked that the charset is supported
					// in the if statement above. However, the compiler requires this catch block.
					System.out.println("Error: Unsupported format exception. DCS value set to " + data_coding.toString() + ", attempting to decode as " + charSet);
					e.printStackTrace();
					return;
				}
    		} else {
    			// This code should never be entered, unless this application is somehow running on
    			// a system that doesn't support the UTF-16 charset.
    			System.out.println("Warning: Running on a system that does not appear to support the UTF-16 character set.");
				messageBytes = new byte[numChars*2]; // need two bytes (16 bits) per char, since this is in UTF-16.
	    		
	    		for (int i = 0; i < numChars; i++) {
	    			messageBytes[i*2] = 0;
	    			messageBytes[(i*2)+1] = (byte) message.charAt(i);
	    		}
    		}
    	} else { // DCS is a value other than 8, and message body is in text format
        	messageBytes = message.getBytes();
        }
        	
		if (operator == null || operator.isEmpty() || operator.equals("0")) {
        	// Use SureRoute
			if (SMPPG == null) {
				SMPPG = "smpp.mt.us.mblox.com";
			}

        	if (serviceID != null && serviceID.length() > 1) {
		        OptionalParameter op0;
		        op0 = new OptionalParameter.OctetString(SERVICEID_TAG, serviceID.getBytes(), 0, 5);
	        	op = new OptionalParameter[1];
	        	op[0] = op0;
	        }
        } else { // OPERATOR is not null, so use two-way/PSMS platform
			if (SMPPG == null) {
				SMPPG = "smpp.psms.us.mblox.com";
			}
        	programType = (PROGRAMTYPE == null ? null : PROGRAMTYPE);
        	
        	int optionalParamCount = 1;
        	boolean includeSID = false;
        	        	
        	/*
        	if (serviceID == null || serviceID.length == 0 || (serviceID.length == 1 && serviceID[0] == '0')) {
        		op = new OptionalParameter[2];
        	}
        	*/
        	
        	if (useTariff) optionalParamCount++;

        	// TO DO: Look at this. Shouldn't be setting sid to null.
        	if (operator.equals(VERIZON_ID)) {
            	if (VZWserviceID != null) {
            		serviceID = VZWserviceID;
            	}
            	/*
            	  else {
            		serviceID = DEFAULT_SERVICEID;
            	}
            	*/
        	} else if (operator.equals(TMOBILE_ID)) {
            	if (TMOserviceID != null) {
            		serviceID = TMOserviceID;
            	}
            	/*
            	  else {
            		serviceID = DEFAULT_SERVICEID;
            	}
            	*/
            }

        	if (serviceID != null && serviceID.length() > 1 && !serviceID.equals("0")) {
        		includeSID = true;
        		optionalParamCount++;
        	}
        	if (programType != null && programType.length() > 0) {
        		optionalParamCount++;
        	}
        	
        	op = new OptionalParameter[optionalParamCount];
        	OptionalParameter op0 = new OptionalParameter.OctetString(OPERATOR_TAG, operator.getBytes(), 0, 5);
	        op[0] = op0;
        	if (useTariff) {
        		OptionalParameter op1 = new OptionalParameter.OctetString(TARIFF_TAG, TARIFF, 0, 5);
        		op[1] = op1;
        	}
	        
        		// PROGRAM_TAG op = new OptionalParameter[3];
        		// op = new OptionalParameter[4]; // PROGRAM_TAG 
        		// OptionalParameter op2 = new OptionalParameter.OctetString(SERVICEID_TAG, SERVICEID, 0, 5);
	        if (optionalParamCount == 4) { // include service ID and PROGRAM_TAG
	        	// Can also check for includeSID == true, but would require additional error-handling if false. Possibly to do later.
	        	// Can also check for optionalParamCount > 4, but would require additional error-handling if true. Possibly to do later.
        		OptionalParameter op2 = new OptionalParameter.OctetString(SERVICEID_TAG, serviceID.getBytes(), 0, 5);
        		op[2] = op2;
        		OptionalParameter op3 = new OptionalParameter.OctetString(PROGRAM_TAG, programType.getBytes(), 0, 5); 
        		op[3] = op3;
	        } else if (optionalParamCount == 3) {
	        	if (includeSID) { // include service ID, but not PROGRAM_TAG
	        		OptionalParameter op2 = new OptionalParameter.OctetString(SERVICEID_TAG, serviceID.getBytes(), 0, 5);
	        		op[2] = op2;
	        	} else { // include PROGRAM_TAG, but not service ID
	        		OptionalParameter op2 = new OptionalParameter.OctetString(PROGRAM_TAG, programType.getBytes(), 0, 5);
	        		op[2] = op2;
	        	}
	        }
        }

    	if (useSSL) {
    		// do something here to enable SSL
    		
    	}
    	
    	Integer bindAttempts = MAX_BIND_ATTEMPTS;
    	while (bindAttempts-- > 0) {
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
	        	
	        	/*
	        	if (useSSL) {
	        		// do something here to enable SSL
	        		
	        	}
	        	*/
	        	
	            session.connectAndBind(SMPPG, port,
	            		new BindParameter(
	            				// BindType.BIND_TX, // transmitter
	            				// BindType.BIND_TX,
	            				BindType.BIND_TRX,
	            				systemID, // user name
	            				userPass,
	            				systemType,
	            				TypeOfNumber.NATIONAL, // TypeOfNumber.UNKNOWN,
	            				NumberingPlanIndicator.NATIONAL,
	            				null));
	
	            		// new BindParameter(BindType.BIND_TX, "test", "test", "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
	        } catch (IOException e) {
	        	String errorDescription = e.getLocalizedMessage();
	        	System.err.println("Failed to connect and bind to host " + SMPPG);        
	            System.err.println("(\"" + errorDescription + "\")");	            
	            if (errorDescription.contains("unexpected deliver_sm")) {
	            	// unexpected DR received. Print condition, but recover.
	            	System.err.println("... Re-attempting bind.");
	            	SMPPUtilities.sleepSec(3);
	            	continue;
	            } else if (errorDescription.contains("Failed connecting")) {
	            	
    	        e.printStackTrace();
    	        // TO DO: Remove above

	            	System.err.println("... Re-attempting bind.");
	            	SMPPUtilities.sleepSec(3);
	            	continue;
	            // TODO add more conditions that are OK to retry.
	            } else {
	            	/*
	            	java.io.IOException: Failed connecting
		            	at org.jsmpp.session.SMPPSession.connectAndBind(SMPPSession.java:218)
		            	at org.jsmpp.session.SMPPSession.connectAndBind(SMPPSession.java:200)
		            	at org.jsmpp.examples.SMPPDriver.main(SMPPDriver.java:947)
	            	*/
	            	System.err.println("Unable to recover from exception.");
		            e.printStackTrace();
		            return;
	            }
	            // need to handle the following exception when pending DRs. This prevents bind for some reason.
	            /*
	            java.io.IOException: Receive invalid response of bind: Receive unexpected deliver_sm
		        	at org.jsmpp.session.SMPPSession.connectAndBind(SMPPSession.java:251)
		        	at org.jsmpp.session.SMPPSession.connectAndBind(SMPPSession.java:200)
		        	at org.jsmpp.examples.SMPPDriver.main(SMPPDriver.java:831)
	        	*/
	        }
	        /*
	        catch (java.net.SocketException s) {
	        	
	        }
	        */
           	// TODO catch more possible exceptions and either continue (retry) or return (quit).

    	} // while bindAttempts
        
        //YYMMDDhhmmss
        //020610233429000R
        /*
        String DELIVERTIME =
        		"00000000" +
        		DELIVER_MINUTES +
        		DELIVER_SECONDS +
        		"000R";
        */
        
        DataCoding dataCoding = GeneralDataCoding.newInstance(data_coding);
        //	DataCoding dataCoding = GeneralDataCoding.newInstance(1);
        //	messageBytes[0] = 0;
        
        // DataCoding dataCoding = GeneralDataCoding.newInstance(0xF0); // 0xF0 = Flash SMS
        // GeneralDataCoding dataCoding = (DataCoding) GeneralDataCoding.newInstance(0xF0);
		// GeneralDataCoding.newInstance(dc_value), // use in for loop
		// GeneralDataCoding.newInstance(0x08), // 0x08 = unicode
		// GeneralDataCoding.newInstance(Alphabet.ALPHA_UCS2),
		// GeneralDataCoding.newInstance(0xF5),
        
        String messageId = null;
        
        if (isResponder) {
        	// MessageResponder.launchResponderDaemon(session, port, systemID, userPass, systemType);
        	// if (bindTime > 0) launch with a bindTime parameter
        	MessageResponder.launchResponderDaemon(session, port, systemID, userPass, systemType, TMOserviceID, VZWserviceID);
        }
        else { // Just send one MT message and exit
        	Integer PTTFinalResult = 0;
        	// String messageId = null;
        	String rejectionReason = null;

	        try {
	        	// MessageSender.submitMessage(session, serviceType, shortcode, msisdn, dataCoding, body, op, enableDRs)
	        	if (isConcatenated) {
		        	messageId =
		        			MessageSender.submitConcatMessage(
		        			session,
		        			// systemType, // username
		        			serviceType,
		        			shortCode,
		        			msisdn,
		        			dataCoding,
		        			// message,
		        			// message.getBytes(),
		        			messageBytes,
		        			op,
		        			// receiveDRs);
		        			true);
		            System.out.println(getTimeStamp() + " Message submitted, message_id is " + messageId);
	        	} else {
		        	messageId =
		        			MessageSender.submitMessage(
		        			session,
		        			// systemType, // username
		        			serviceType,
		        			shortCode,
		        			msisdn,
		        			dataCoding,
		        			// message,
		        			// message.getBytes(),
		        			messageBytes,
		        			op,
		        			// receiveDRs);
		        			true);
		            System.out.println(getTimeStamp() + " Message submitted, message_id is " + messageId);
	        	}
		        //if (receiveDRs && messageId != null) {
		        //	PTTFinalResult = getFinalDeliveryStatus(session);
		        	// PTTFinalResult can be used at some point in the future, if needed.
		        //}
	        } catch (PDUException e) {
	            // Invalid PDU parameter
	        	rejectionReason = "Invalid PDU parameter";
	            System.err.println(rejectionReason);
	            e.printStackTrace();
	            // return;
	        } catch (ResponseTimeoutException e) {
	            // Response timeout
	        	rejectionReason = "Responsed timeout";
	            System.err.println(rejectionReason);
	            e.printStackTrace();
	            // return;
	        } catch (InvalidResponseException e) {
	            // Invalid response
	        	rejectionReason = "Received invalid respose";
	            System.err.println(rejectionReason);
	            e.printStackTrace();
	            // return;
	        } catch (NegativeResponseException e) {
	            // Receiving negative response (non-zero command_status)
	        	rejectionReason = "Negative response from gateway";
	        	String errorMsg = e.toString();
	        	if (errorMsg.startsWith("org.jsmpp.extra.NegativeResponseException: Negative response"))
	        	{
	        		String errorCode = errorMsg.split(" ", 5)[3];
	        		if (errorCode.equals(INVALID_NUMBER_ERROR)) {
	        			System.out.println("\nInvalid phone number entered");
	        			System.out.println("Usage:");
	        			System.out.println("smpp [user] [pass] [port] [short code] [msisdn] [message] [operator | 0] [serviceID | 0]");
	        		}
	        		// else // cover other error responses here
	        	} else {			
		            System.err.println("Received negative response");
		            e.printStackTrace();
	        	}
	        	// return;
	        } catch (IOException e) {
	        	rejectionReason = "IO error occurred";
	            System.err.println(rejectionReason);
	            e.printStackTrace();
	            // return;
	        } catch (NullPointerException e) {
	        	rejectionReason = "NullPointerException";
	            System.err.println(rejectionReason);
	            e.printStackTrace();
	            // return;
	        }
	        if (messageId == null) {
	        	System.out.println("Message submission has been rejected. Reason: " + (rejectionReason == null ? "unknown" : rejectionReason));
	        }
        }

        if (messageId != null && receiveDRs) { // if above submit call returned a non-null value and DRs are enabled
	        if (bindTime < 0) {
	        	Integer PTTFinalResult = getFinalDeliveryStatus(session);
	        } else if (bindTime > 0) {
	       		Integer PTTFinalResult = getFinalDeliveryStatus(session, bindTime);
	       	}
	        // if bindTime == 0, just fire and forget. Do not call getFinalDeliveryStatus(), as no DRs are wanted.
        }

        /*
        if (bindTime > 0) {
			Long currentTime = System.currentTimeMillis() / 1000;
			Integer PTTFinalResult;
			while (bindStart + bindTime > currentTime.intValue()) {
				// Later: launch a new thread that waits for timeout and then unbinds and kills application.
		        if (receiveDRs) {
		        	PTTFinalResult = getFinalDeliveryStatus(session);
		        } else { // No DRs, so just sleep for 5 sec
		        	try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
		        }		        	
				currentTime = System.currentTimeMillis() / 1000;				
			}
        }
        */

        System.out.println(getTimeStamp() + " Unbinding and exiting.");
    	session.unbindAndClose();
    }   
}