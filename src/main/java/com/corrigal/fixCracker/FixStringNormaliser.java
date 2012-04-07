package com.corrigal.fixCracker;

import static com.corrigal.fixCracker.CommonConstants.COMMA;
import static com.corrigal.fixCracker.CommonConstants.PIPE;
import static com.corrigal.fixCracker.CommonConstants.SOH;

import org.apache.commons.lang.StringUtils;

public class FixStringNormaliser {

	public static String process(String rawFixString) {
		return forDelimiters(rawFixString);
	}
	
	private static String forDelimiters(String fixString) {
		if (fixString.contains(SOH)) {
			return fixString;
		} else if (fixString.contains(PIPE)) {
			return StringUtils.replace(fixString, PIPE, SOH);
		} else if (fixString.contains(COMMA)) {
			return StringUtils.replace(fixString, COMMA, SOH);
		} else {
			throw new RuntimeException("FIX String not delimited by recognised character");
		}
	}
	
}
