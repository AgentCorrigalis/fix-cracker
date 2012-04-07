package com.corrigal.fixCracker;

import static com.corrigal.fixCracker.CommonConstants.PIPE;
import static com.corrigal.fixCracker.CommonConstants.SOH;
import static com.corrigal.fixCracker.CommonConstants.COMMA;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import quickfix.ConfigError;
import quickfix.InvalidMessage;

import com.sun.tools.corba.se.idl.InvalidArgument;

public class MessageReaderTest {

	private MessageReader fixViewer;
	
	@Before
	public void setUp() throws ConfigError {
		fixViewer = new MessageReader();
	}
	
	@Test
	public void canParseFixStringDelimitedBySOH() throws InvalidMessage, ConfigError, InvalidArgument {
		assertFixStringParsed(SOH);
	}
	
	@Test
	public void canParseFixStringsWithOtherDelimiters() throws InvalidMessage, ConfigError, InvalidArgument {
		assertFixStringParsed(PIPE);
		assertFixStringParsed(COMMA);
	}
	
	private void assertFixStringParsed(String delimiter) throws InvalidMessage, ConfigError, RuntimeException {
		String fixMessageString = "8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
		Map<Integer, String> fixMessageMap = fixViewer.parseFixString(StringUtils.replace(fixMessageString, SOH, delimiter));
		assertEquals(7, fixMessageMap.size());
		assertTrue(fixMessageMap.get(8).equals("FIX.4.4"));
		assertTrue(fixMessageMap.get(9).equals("30"));
		assertTrue(fixMessageMap.get(35).equals("D"));
		assertTrue(fixMessageMap.get(49).equals("SEND"));
		assertTrue(fixMessageMap.get(56).equals("TARGET"));
		assertTrue(fixMessageMap.get(11).equals("123"));
		assertTrue(fixMessageMap.get(10).equals("096"));
	}
	
	@Test
	public void canNotParseFixStringDelimitedByUnknowCharacter() throws InvalidMessage, ConfigError {
		String fixString = "8=FIX.4.4?9=30?35=D?49=SEND?56=TARGET?11=123?10=096?";
		try {
			fixViewer.parseFixString(fixString);
		} catch (RuntimeException e) {
			e.printStackTrace();
			assertEquals("FIX String not delimited by recognised character", e.getMessage());
		}
	}
	
	@Test
	public void invalidFixStringThrowsInvalidMessge() {
		String fixMessageString = "NER NER NER 8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
		try {
			fixViewer.parseFixString(fixMessageString);
		} catch (Exception e) {
			assertEquals(InvalidMessage.class.getName(), e.getClass().getName());
		}
	}
	
	@Test
	public void fieldTagsCanBeInterpreted() {
		int fieldTag = 8;
		assertEquals("BeginString", fixViewer.fieldNameForTag(fieldTag));
	}
	
	@Test
	public void fieldValuesCanBeInterpretedIfEnumerated() {
		int enumeratedFieldTag = 35;
		String enumeratedFieldValue = "D";
		int nonEnumeratedFieldTag = 49;
		String nonEnumeratedFieldValue = "SEND";
		assertEquals("NewOrderSingle", fixViewer.meaningfulFieldValue(enumeratedFieldTag, enumeratedFieldValue));
		assertEquals("SEND", fixViewer.meaningfulFieldValue(nonEnumeratedFieldTag, nonEnumeratedFieldValue));
	}
	
	@Test
	public void logFileReaderTest() throws IOException, InvalidMessage, ConfigError {
		BufferedReader logFileReader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("fixLogs/shortFixLog.txt")));
		//File logFile = new File("/home/alexco/workspace/fixMe/fix-me/src/test/resources/fixLogs/shortFixLog.txt");
		List<String> fixLogRecords = fixViewer.parseFixLogFile(logFileReader);
		assertEquals(10, fixLogRecords.size());
		for (String fixLogRow : fixLogRecords) {
			assertTrue(fixLogRow.contains("8=FIX.4.4"));
		}
	}
	
	@Test
	public void stripFixStringFromLogRecord() {
		String fixLogRecord = "[some text at front] 8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096 [text at end]";
		String expectedFixString = "8=FIX.4.49=3035=D49=SEND56=TARGET11=12310=096";
		assertEquals(expectedFixString, fixViewer.extractFixString(fixLogRecord));
	}
	

	
}
