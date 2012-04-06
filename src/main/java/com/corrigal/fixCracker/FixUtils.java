package com.corrigal.fixCracker;

import quickfix.InvalidMessage;
import quickfix.Message;

public class FixUtils {

	public static Integer calculateCheckSumFor(String message) {
		Integer byteSum = 0;
		for (Character character : message.toCharArray()) {
			byteSum += (int) character;
		}
		
		return byteSum % 256;
	}
	
	public static boolean messageIsValid(String messageString) {
		try {
			new Message(messageString, true);
			return true;
		} catch (InvalidMessage e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
