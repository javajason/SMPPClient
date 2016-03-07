package org.jsmpp.examples;

import java.util.Date;

// import smsresponder.SMSutilities;
// import smsresponder.ServletContextClass;

public class SMPPUtilities {
		
	public static final short OPERATOR_TAG = 0x1402;
	public static final short TARIFF_TAG = 0x1403;	
	public static final short SESSIONID_TAG = 0x1404;
	public static final short SERVICEID_TAG = 0x1407;
	public static final short PROGRAM_TAG = 0x1542;
	// private static final String PROGRAMTYPE = "stdrt"; // for use with PROGRAM_TAG
	public static final String PROGRAMTYPE = null;
	
	public static final String OPERATOR_TMO = "31004";
	public static final String OPERATOR_VZW = "31003";

	// private static final String SYSTEMTYPE = "mblox";
	public static final String SERVICETYPE = "-1";
	public static final byte[] TARIFF = {'0','0','0','0','0'};
	private static final Integer US_CHAR_LIMIT = 160;
	
	public static final String TMO_SERVICEID = "60521"; // for mbloxAlerts, SC 28444
	public static final String VZW_SERVICEID = "60522";	// for mbloxAlerts, SC 28444
	private static final String KEYWORD = "ALLBLOX";
	public static final String DEFAULT_SHORTCODE = "28444";
	
	public static final Integer DR = 4;
	public static final Integer MO = 0;
	public static final Integer NOMSG = -1;

	@SuppressWarnings("deprecation") // suppress warnings about Data methods being deprecated
	public static String getTimeStamp()
	{
		long currentTime = System.currentTimeMillis();
		Date d = new Date();
		d.setTime(currentTime);

		/*
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTime);
		*/
		
		// Integer.toString(d.getMinutes())
		return (String.valueOf(d.getHours()) + ":" +
				(d.getMinutes() > 9 ? "" : "0") + String.valueOf(d.getMinutes()) + ":" +
				(d.getSeconds() > 9 ? "" : "0") + String.valueOf(d.getSeconds()));
		/*
		return ("Yo1 " + String.valueOf(cal.HOUR_OF_DAY) + ":" +
				String.valueOf(cal.MINUTE) + ":" +
				String.valueOf(cal.SECOND));
		*/

		/*
		return (String.valueOf(Calendar.HOUR_OF_DAY) + ":" +
				String.valueOf(Calendar.MINUTE) + ":" +
				String.valueOf(Calendar.SECOND));
		*/
	}
	
	public static void sleepSec(Integer seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static String lookupResponse(String inboundMessage, String shortCode)
	{
		// TODO: parse inboundMessage and determine appropriate response
		boolean gotKeyword = false;
		// String response = null;
		
		if (shortCode == null || shortCode.isEmpty()) {
			return "Message response.";
		}
		
		// trim initial message
		inboundMessage = inboundMessage.trim();
		
		// Save off original message for later use. 
		String origMessage = inboundMessage;
	
		inboundMessage = inboundMessage.toUpperCase();
		String mainMessage = null;
		
		String[] msgParts = inboundMessage.split(" ", 2);
		if (msgParts[0].equals(KEYWORD)) gotKeyword = true;
		// handle keyword-specific functions here

		if (shortCode.equals(DEFAULT_SHORTCODE)) {
			if (gotKeyword) {
				if (msgParts.length < 2 || msgParts[1].trim().isEmpty()) {
					return "Blank message received after keyword. How about giving us something we can work with, OK?";
				} else {
					mainMessage = msgParts[1].trim();
				}				
			} else { // no keyword used
				mainMessage = msgParts[0].trim();
			}
			
			if (mainMessage.startsWith("HELP")) {
				return "You have contacted Jason's SMPP autoresponder. Welcome!";
			}
			if (mainMessage.startsWith("STOP")) {
				return "What? You don't want any more messages???";
			} else {
				String responseBeginning = "Unknown message received. You sent: ";
				if (responseBeginning.length() + origMessage.length() > US_CHAR_LIMIT) {
					return responseBeginning + origMessage.substring(0, US_CHAR_LIMIT - (responseBeginning.length() + 3)) + "...";
				} else {
					return responseBeginning + origMessage;
				}			
			}
		} else { // handle other short codes here
			return "Message received on short code " + shortCode + ".";
		}
		// return response;
	}
}
