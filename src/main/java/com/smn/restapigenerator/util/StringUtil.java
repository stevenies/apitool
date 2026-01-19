package com.smn.restapigenerator.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

	public static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}

    public static String trim(String text) {
		if (text == null) {
			return null;
		}
		return text.trim();
    }

	public static String toCamelCase(String text) {
       if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            if (Character.isWhitespace(currentChar) || currentChar == '_' || currentChar == '-') {
                capitalizeNext = true;
            } else {
                capitalizeNext |= (i > 0 && Character.isUpperCase(currentChar));
                buffer.append(capitalizeNext ? Character.toUpperCase(currentChar) : Character.toLowerCase(currentChar));
                capitalizeNext = false;
           }
        }
        String result = buffer.toString();
        return result;
	}

    public static String toPascalCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String camelCase = StringUtil.toCamelCase(text);
        return Character.toUpperCase(camelCase.charAt(0)) + (camelCase.length() > 1 ? camelCase.substring(1) : "");
    }

	public static String toKebabCase(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

        StringBuilder buffer = new StringBuilder();
        boolean allowHyphen = true;

        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);

            if (currentChar == '-' || currentChar == '_' || Character.isWhitespace(currentChar)) {
                if (allowHyphen) {
                    buffer.append('-');
                    allowHyphen = false;
                }
            } else if (Character.isUpperCase(currentChar)) {
                if (i > 0 && allowHyphen) {
                    buffer.append('-');
                    allowHyphen = false;
                }
                buffer.append(Character.toLowerCase(currentChar));
            } else {
                buffer.append(currentChar);
                allowHyphen = true;
           }
        }
        String result = buffer.toString();
        return result;
    }

    public static String toHTML(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                   .replace("\"", "&quot;").replace("'", "&#39;").replace("\n", "<br/>");
    }

	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email != null && email.matches(emailRegex);
    }

    public static boolean isValidVersion(String version) {
		String versionRegex = "^\\d\\.\\d$";
		return version != null && version.matches(versionRegex);
    }

    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$";
        String internationalPhoneRegex = "^\\+\\d{1,3}[- ]?\\(?\\d{1,4}\\)?[- ]?\\d{1,4}[- ]?\\d{1,4}[- ]?\\d{0,4}$";
        return phone != null && (phone.matches(phoneRegex) || phone.matches(internationalPhoneRegex));
    }

    public static int makeKey(String text) {
        if (text == null) {
            return 0;
        }
        text += "SMNRestApiGenerator";
        
        int sum = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isLetter(ch)) {
                sum += (int) ch;
            }
        }
        return sum;
    }

    public static long makeId() {
        long prevId = System.currentTimeMillis();
        long newId = System.currentTimeMillis();
        while (newId == prevId) {
            newId = System.currentTimeMillis();
        }
        return newId;
    }

    public static Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        return sdf.parse(dateStr);
    }

    public static synchronized String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        return sdf.format(date);
    }

    public static String tabs(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

}
