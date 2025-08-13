package com.smn.restapitool.util;

public class StringUtil {

	public static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}

	public static String toCamelCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		char firstChar = text.charAt(0);
		char lowerFirstChar = Character.toLowerCase(firstChar);
		return lowerFirstChar + text.substring(1);
	}
}
