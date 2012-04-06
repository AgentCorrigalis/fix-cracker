package com.corrigal.fixCracker;

import static org.junit.Assert.*;

import org.junit.Test;


public class FixUtilsTest {

	@Test
	public void checkSumCorrectForSimpleString() {
		String message = "abc";
		assertEquals((int)38, (int)FixUtils.calculateCheckSumFor(message));
	}
	
	@Test
	public void validateFixMessageStringFail() {
		assertFalse(FixUtils.messageIsValid("abc"));
	}
	
	@Test
	public void validateFixMessageStringPass() {
		assertTrue(FixUtils.messageIsValid("8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096"));
	}
	
}
