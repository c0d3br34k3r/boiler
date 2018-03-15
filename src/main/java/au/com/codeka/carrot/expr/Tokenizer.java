package au.com.codeka.carrot.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import com.google.common.base.CharMatcher;

import au.com.codeka.carrot.CarrotException;

/**
 * Converts an input {@link Reader} into a stream of {@link Token}s.
 */
public class Tokenizer {

	private PushbackReader reader;
	private Token peeked;
	private final Mode mode;
	private boolean end; // = false

	public Tokenizer(PushbackReader reader, Mode mode) {
		this.reader = reader;
		this.mode = mode;
	}

	public enum Mode {
		STREAM, TAG, ECHO;
	}

	/**
	 * Returns the type of the next token without consuming it.
	 * 
	 * @return the type of the next token
	 * @throws CarrotException if there's an error parsing the token
	 */
	public TokenType peek() throws CarrotException {
		if (peeked == null) {
			peeked = parse();
		}
		return peeked.getType();
	}

	public Token next() throws CarrotException {
		if (peeked == null) {
			return parse();
		}
		Token result = peeked;
		peeked = null;
		return result;
	}

	// public BinaryOperator binaryOperator() throws CarrotException {
	// return next().getType().binaryOperator();
	// }
	//
	// public UnaryOperator unaryOperator() throws CarrotException {
	// return next().getType().unaryOperator();
	// }
	//
	// public Object value() throws CarrotException {
	// return next().getValue();
	// }

	/**
	 * Consumes the next token and asserts that it matches the given type.
	 *
	 * @param type the type to match against
	 * @throws CarrotException if there's an error parsing the token, or if it
	 *         doesn't match the given type
	 */
	public void consume(TokenType type) throws CarrotException {
		Token next = next();
		if (next.getType() != type) {
			throw new CarrotException("Expected token of type " + type
					+ ", got " + next.getType());
		}
	}

	public boolean tryConsume(TokenType type) throws CarrotException {
		if (peek() == type) {
			next();
			return true;
		}
		return false;
	}

	public void end() throws CarrotException {
		consume(TokenType.END);
	}

	public void consumeIdentifier(String value) throws CarrotException {
		Token next = next();
		if (next.getType() != TokenType.IDENTIFIER || !next.getIdentifier().equals(value)) {
			throw new CarrotException("Expected identifier " + value
					+ ", got " + next);
		}
	}

	private Token parse() throws CarrotException {
		if (end) {
			return Token.END;
		}
		try {
			int ch;
			do {
				ch = reader.read();
			} while (CharMatcher.whitespace().matches((char) ch));
			return parseToken(ch);
		} catch (IOException e) {
			throw new CarrotException(e);
		}
	}

	public Term parseExpression() throws CarrotException {
		return ExpressionParser.parse(this);
	}

	public String parseIdentifier() throws CarrotException {
		Token next = next();
		if (next.getType() != TokenType.IDENTIFIER) {
			throw new CarrotException("expected identifier, got " + next);
		}
		return next.getIdentifier();
	}

	private static final CharMatcher DIGIT;
	private static final CharMatcher DIGIT_OR_DOT;
	private static final CharMatcher IDENTIFIER_START;
	private static final CharMatcher IDENTIFIER_PART;

	static {
		DIGIT = CharMatcher.inRange('0', '9');
		DIGIT_OR_DOT = DIGIT.or(CharMatcher.anyOf("."));
		IDENTIFIER_START = CharMatcher.inRange('a', 'z')
				.or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.is('_'));
		IDENTIFIER_PART = IDENTIFIER_START.or(DIGIT);
	}

	private Token parseToken(int ch) throws CarrotException, IOException {
		switch (ch) {
		// @formatter:off
		case '(': return Token.LEFT_PARENTHESIS;
		case ')': return Token.RIGHT_PARENTHESIS;
		case '[': return Token.LEFT_BRACKET;
		case ']': return Token.RIGHT_BRACKET;
		case ',': return Token.COMMA;
		case '.': return Token.DOT;
		case '+': return Token.PLUS;
		case '-': return Token.MINUS;
		case '*': return Token.MULTIPLY;
		case '/': return Token.DIVIDE;
		case '&': require('&'); return Token.LOGICAL_AND;
		case '|': require('|'); return Token.LOGICAL_OR;
		case '%': return tryRead('>') ? end(Mode.TAG) : Token.MODULO;
		case '=': return tryRead('=') ? Token.EQUAL : Token.ASSIGNMENT;
		case '!': return tryRead('=') ? Token.NOT_EQUAL : Token.NOT;
		case '<': return tryRead('=') ? Token.LESS_THAN_OR_EQUAL : Token.LESS_THAN;
		case '>': return parseGreaterThan();
		// @formatter:on
		case -1:
			return end(Mode.STREAM);
		case '"':
		case '\'':
			return parseString((char) ch);
		default:
		}
		if (DIGIT.matches((char) ch)) {
			return parseNumber((char) ch);
		}
		if (IDENTIFIER_START.matches((char) ch)) {
			return parseIdentifier((char) ch);
		}
		throw new CarrotException("unexpected char " + (char) ch + ", ("
				+ Character.getName(ch) + ")");
	}

	private Token parseString(char end) throws CarrotException, IOException {
		StringBuilder builder = new StringBuilder();
		for (;;) {
			int next = reader.read();
			char c;
			switch (next) {
			case -1:
				throw new CarrotException("unclosed string");
			case '\\':
				c = readEscapeChar();
				break;
			case '\'':
			case '"':
				if (next == end) {
					return Token.valueToken(builder.toString());
				}
				// fallthrough
			default:
				c = (char) next;
			}
			builder.append(c);
		}
	}

	private Token parseNumber(char first) throws IOException {
		boolean dot = false;
		StringBuilder builder = new StringBuilder();
		builder.append(first);
		CharMatcher matcher = DIGIT_OR_DOT;
		for (;;) {
			int ch = reader.read();
			if (ch == -1) {
				break;
			}
			if (matcher.matches((char) ch)) {
				builder.append((char) ch);
				if (ch == '.') {
					dot = true;
					matcher = DIGIT;
				}
			} else {
				reader.unread(ch);
				break;
			}
		}
		String str = builder.toString();
		Number number = dot
				? (Number) Double.parseDouble(str)
				: (Number) Integer.parseInt(str);
		return Token.valueToken(number);
	}

	private Token parseIdentifier(char first) throws IOException {
		String identifier = readUntil(first, IDENTIFIER_PART);
		switch (identifier) {
		case "true":
			return Token.TRUE;
		case "false":
			return Token.FALSE;
		default:
			return Token.identifierToken(identifier);
		}
	}

	private String readUntil(char first, CharMatcher matcher)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(first);
		for (;;) {
			int next = reader.read();
			if (next == -1) {
				break;
			}
			if (matcher.matches((char) next)) {
				builder.append((char) next);
			} else {
				reader.unread(next);
				break;
			}
		}
		return builder.toString();
	}

	private void require(char required) throws IOException, CarrotException {
		if (reader.read() != required) {
			throw new CarrotException("expected " + required);
		}
	}

	/**
	 * "Peeks" at the next character and consumes it if it matches the given
	 * character.
	 * 
	 * @param match the character to match
	 * @return whether the character was consumed
	 * @throws IOException
	 */
	private boolean tryRead(char match) throws IOException {
		int ch = reader.read();
		if (ch == match) {
			return true;
		}
		if (ch != -1) {
			reader.unread(ch);
		}
		return false;
	}

	private Token parseGreaterThan() throws CarrotException, IOException {
		int ch = reader.read();
		switch (ch) {
		case '>':
			return end(Mode.ECHO);
		case '=':
			return Token.GREATER_THAN_OR_EQUAL;
		default:
			reader.unread(ch);
			// fallthrough
		case -1:
			return Token.GREATER_THAN;
		}
	}

	private Token end(Mode check) throws CarrotException {
		if (mode != check) {
			throw new CarrotException(
					String.format("expected end of %s but was end of %s", mode,
							check));
		}
		end = true;
		return Token.END;
	}

	private char readEscapeChar() throws IOException, CarrotException {
		int ch = reader.read();
		switch (ch) {
		// @formatter:off
		case 't':
			return '\t';
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case '\'':
		case '"':
		case '\\':
			return (char) ch;
		case 'u':
			return readUnicode();
		case -1:
			throw new CarrotException("unclosed string");
		default:
			throw new CarrotException("bad escaped char: " + (char) ch);
			// @formatter:on
		}
	}

	private char readUnicode() throws IOException, CarrotException {
		char[] buf = new char[4];
		if (readFully(reader, buf) != 4) {
			throw new CarrotException(
					"unicode escapes must contain 4 hexidecimal digits");
		}
		return (char) Integer.parseInt(new String(buf), 16);
	}

	public static int readFully(Reader in, char[] buf) throws IOException {
		int total = 0;
		while (total < buf.length) {
			int result = in.read(buf, total, buf.length - total);
			if (result == -1) {
				break;
			}
			total += result;
		}
		return total;
	}

}
