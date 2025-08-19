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

    public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email != null && email.matches(emailRegex);
    }

    public static boolean isValidVersion(String version) {
		String versionRegex = "^\\d\\.\\d$";
		return version != null && version.matches(versionRegex);
    }

    public static String trim(String title) {
		if (title == null) {
			return null;
		}
		return title.trim();
    }
}
