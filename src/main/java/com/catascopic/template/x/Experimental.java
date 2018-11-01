package com.catascopic.template.x;

public class Experimental {

	private static final int RADIX = 10;

	public static Integer parseIntOrNull(String str) {
		
		if (str.isEmpty()) {
			return null;
		}
		boolean negative = false;
		int i = 0;
		int limit = -Integer.MAX_VALUE;
		char firstChar = str.charAt(0);
		if (firstChar < '0') {
			if (firstChar == '-') {
				negative = true;
				limit = Integer.MIN_VALUE;
			} else if (firstChar != '+') {
				return null;
			}
			if (str.length() == 1) {
				return null;
			}
			i++;
		}
		int result = 0;
		int multmin = limit / RADIX;
		while (i < str.length()) {
			int digit = Character.digit(str.charAt(i++), RADIX);
			if (digit < 0) {
				return null;
			}
			if (result < multmin) {
				return null;
			}
			result *= RADIX;
			if (result < limit + digit) {
				return null;
			}
			result -= digit;
		}
		return negative ? result : -result;
	}

}
