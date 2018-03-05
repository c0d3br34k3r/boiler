package au.com.codeka.carrot;

public class Experimental {

	static final int bigDecimalExponent = 324;

	public static Number readNumber(String in) throws NumberFormatException {
		boolean isNegative = false;
		boolean signSeen = false;
		int decExp;

		int i = 0;
		char c = in.charAt(i);
		switch (c) {
		case '-':
			isNegative = true;
			// fallthrough
		case '+':
			i++;
			signSeen = true;
		default:
		}

		int len = in.length();
		c = in.charAt(i);

		for (; i < len; i++) {
			c = in.charAt(i);

		}

	}

	private static final int RADIX = 10;

	public static int parseInt(String str, int start) throws NumberFormatException {
		int result = 0;
		boolean negative = false;
		int i = start;
		int limit = -Integer.MAX_VALUE;
		int multmin;
		int digit;

		int len = str.length();
		char firstChar = str.charAt(0);
		switch (firstChar) {
		case '-':
			negative = true;
			limit = Integer.MIN_VALUE;
			// fallthrough
		case '+':
			i++;
			// Cannot have lone "+" or "-"
			if (i == len) {
				throw new NumberFormatException(str);
			}
		default:
		}

		multmin = limit / RADIX;
		while (i < len) {
			// Accumulating negatively avoids surprises near MAX_VALUE
			digit = Character.digit(str.charAt(i++), RADIX);
			// return both int and pos
			if (digit == -1) {
				throw new NumberFormatException(str);
			}
			if (result < multmin) {
				throw new NumberFormatException(str);
			}
			result *= RADIX;
			if (result < limit + digit) {
				throw new NumberFormatException(str);
			}
			result -= digit;
		}

		return negative ? result : -result;
	}

}
