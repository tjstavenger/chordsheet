package com.google.code.chordsheet.utility;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

public class StringUtility {
	public static final String FILE_ENCODING = "ISO-8859-1";
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final String CLEAN = "[^ \\t\\n\\x0B\\f\\ra-zA-Z0-9!\"#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>\\?@\\[\\\\\\]\\^_`\\{|\\}~\\xA9]";

	/**
	 * Trim the string and remove special characters.
	 * 
	 * @param value
	 *            String to clean
	 * @return cleaned string
	 */
	public static String clean(String value) {
		String clean = StringUtils.stripToEmpty(value);
		clean = StringUtils.trim(clean);
		clean = clean.replaceAll(CLEAN, StringUtils.EMPTY);

		return clean;
	}

	/**
	 * Convert the given String into bytes using the {@link #FILE_ENCODING}.
	 * 
	 * @param value
	 *            String to convert
	 * @return byte array of String
	 */
	public static byte[] getBytes(String value) {
		byte[] bytes = null;
		try {
			bytes = value.getBytes(FILE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// Ignore
		}

		return bytes;
	}

	/**
	 * Cannot instantiate
	 */
	private StringUtility() {
		super();
	}
}
