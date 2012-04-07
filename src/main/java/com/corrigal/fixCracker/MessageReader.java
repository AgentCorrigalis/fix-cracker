package com.corrigal.fixCracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.InvalidMessage;
import quickfix.Message;

public class MessageReader {

	private static final Logger LOGGER = Logger.getLogger(MessageReader.class.getName());
	
	private DataDictionary dataDictionary;
	
	public MessageReader() throws ConfigError {
		dataDictionary = new DataDictionary("FIX44.xml");
	}
	
	public Map<Integer, String> parseFixString(String fixString) throws InvalidMessage, ConfigError, RuntimeException {
		
		String normalisedFixString = FixStringNormaliser.process(fixString);
		
		LOGGER.info("Received fix string [" + normalisedFixString + "]");
		Map<Integer, String> fixMessageMap = new HashMap<Integer, String>();
		Message message = null;
		try {
			message = new Message(normalisedFixString, true);
			fixMessageMap.putAll(iterateMessageParts(message.getHeader().iterator()));
			fixMessageMap.putAll(iterateMessageParts(message.iterator()));
			fixMessageMap.putAll(iterateMessageParts(message.getTrailer().iterator()));
			return fixMessageMap;
		} catch (InvalidMessage e) {
			LOGGER.warning("-- failed validation");
			throw e;
		}
	}
	
	private Map<Integer, String> iterateMessageParts(Iterator<Field<?>> iterator) {
		Map<Integer, String> tagValueMap = new HashMap<Integer, String>();
		while (iterator.hasNext()) {
			Field<?> field = iterator.next();
			String fieldValue = (String) field.getObject();
			tagValueMap.put(field.getField(), fieldValue);
		}
		return tagValueMap;
	}

	public String fieldNameForTag(int fieldTag) {
		return dataDictionary.getFieldName(fieldTag);
	}

	public String meaningfulFieldValue(int fieldTag, String fieldValue) {
		String dictionaryValue = dataDictionary.getValueName(fieldTag, fieldValue);
		if (dictionaryValue == null) {
			return fieldValue;
		} else {
			return dictionaryValue;
		}
	}

	public List<String> parseFixLogFile(BufferedReader logFile) throws IOException, InvalidMessage, ConfigError {
		//LOGGER.info("Reading log file: " + logFile.getAbsolutePath());
		List<String> logRecords = new ArrayList<String>();
		String logRecord;
		while ((logRecord = logFile.readLine()) != null) {
			LOGGER.info("-- got log record: " + logRecord);
			logRecords.add(logRecord);
		}
		return logRecords;
	}

	public String extractFixString(String fixLogRecord) {
		String[] logRecordAsArray = fixLogRecord.split(" ");
		String fixRecord = null;
		for (String fixStringCandidate : logRecordAsArray) {
			if (fixStringCandidate.startsWith("8=FIX.4.4") 
					&& fixStringCandidate.endsWith(new Character('\u0001').toString())) {
				fixRecord = fixStringCandidate;
			}
		}
		return fixRecord;
	}
	
}
