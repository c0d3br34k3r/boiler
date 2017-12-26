package au.com.codeka.carrot.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Set;

import com.google.common.base.CharMatcher;

import au.com.codeka.carrot.CarrotException;

/**
 * Converts an input {@link Reader} into a stream of {@link Token}s.
 */
public class Tokenizer {

	public enum EndMode {
		STREAM,
		TAG,
		ECHO;
	}

	private PushbackReader reader;
	private EndMode endMode;
	private Token next;

	public Tokenizer(Reader reader) throws CarrotException {
		this(new PushbackReader(reader));
	}

	public Tokenizer(PushbackReader reader) throws CarrotException {
		this.reader = new PushbackReader(reader);
		next();
	}

	/**
	 * Returns true if the next token matches the given type. Leaves the token
	 * in place.
	 *
	 * @param type the type to match against
	 * @return true if the next token matches the given type
	 */
	public boolean check(TokenType type) {
		return type == next.getType();
	}

	/**
	 * Returns true if the next token matches one of the given types. Leaves the
	 * token in place.
	 *
	 * @param types the types to match against
	 * @return true if the next token matches one of the given types
	 */
	public boolean check(Set<TokenType> types) {
		return types.contains(next.getType());
	}

	/**
	 * Returns the next token and asserts that it matches the given type.
	 *
	 * @param type the type to match against
	 * @return the next token
	 * @throws CarrotException if there's an error parsing the token, or if it
	 *         doesn't match the given type
	 */
	public Token get(TokenType type) throws CarrotException {
		if (check(type)) {
			return next();
		}
		throw new CarrotException(
				"Expected token of type " + type + ", got " + next.getType());
	}

	/**
	 * Returns the next token and asserts that it matches one of the given
	 * types.
	 *
	 * @param types the types to match against
	 * @return the next token
	 * @throws CarrotException if there's an error parsing the token, or if it
	 *         doesn't matches any of the given types
	 */
	public Token get(Set<TokenType> types) throws CarrotException {
		if (check(types)) {
			return next();
		}
		throw new CarrotException(
				"Expected token of type " + types + ", got " + next.getType());
	}

	public Token next() throws CarrotException {
		// TODO: ???
		if (next.getType() == TokenType.END) {
			return next;
		}
		Token token = next;
		try {
			int ch;
			do {
				ch = reader.read();
			} while (CharMatcher.whitespace().matches((char) ch));
			next = getToken(ch);
		} catch (IOException e) {
			throw new CarrotException(e);
		}
		return token;
	}

	public Term parseExpression() throws CarrotException {
		return ExpressionParser.parse(this);
	}

	public String parseIdentifier() throws CarrotException {
		return (String) get(TokenType.IDENTIFIER).getValue();
	}

	private static final CharMatcher NUMBER;
	private static final CharMatcher IDENTIFIER_START;
	private static final CharMatcher IDENTIFIER_PART;

	static {
		CharMatcher digit = CharMatcher.inRange('0', '9');
		NUMBER = digit.or(CharMatcher.anyOf("."));
		IDENTIFIER_START = CharMatcher.inRange('a', 'z')
				.or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.anyOf("$_"));
		IDENTIFIER_PART = IDENTIFIER_START.or(digit);
	}

	private Token getToken(int ch) throws CarrotException, IOException {
		switch (ch) {
			// @formatter:off
			case '(': return Token.LEFT_PARENTHESIS;
			case ')': return Token.RIGHT_PARENTHESIS;
			case '[': return Token.LEFT_BRACKET;
			case ']': return Token.RIGHT_BRACKET;
			case ',': return Token.COMMA;
			case ';': return Token.SEMICOLON;
			case '.': return Token.DOT;
			case '+': return Token.PLUS;
			case '-': return Token.MINUS;
			case '*': return Token.MULTIPLY;
			case '/': return Token.DIVIDE;
			case '&': return requireNext('&', Token.LOGICAL_AND);
			case '|': return requireNext('|', Token.LOGICAL_OR);
			case '=': return checkNext('=', Token.EQUAL, Token.ASSIGNMENT);
			case '!': return checkNext('=', Token.NOT_EQUAL, Token.NOT);
			case '<': return checkNext('=', Token.LESS_THAN_OR_EQUAL, Token.LESS_THAN);
			case '>': return checkNext('=', Token.GREATER_THAN_OR_EQUAL, Token.GREATER_THAN);
			// TODO: Modulo
			case '%': return checkEndTag();
			// @formatter:on
			case '"':
			case '\'':
				return readString((char) ch);
			case -1:
				return eof(EndMode.STREAM);
			default:
		}
		if (NUMBER.matches((char) ch)) {
			return readNumber((char) ch);
		}
		if (IDENTIFIER_START.matches((char) ch)) {
			return readIdentifier((char) ch);
		}
		throw new CarrotException(
				"unexpected char " + (char) ch + ", (" + Character.getName(ch) + ")");
	}

	private Token readString(char end) throws CarrotException, IOException {
		StringBuilder builder = new StringBuilder();
		for (;;) {
			int next = reader.read();
			if (next == end) {
				return new Token(TokenType.STRING_LITERAL, builder.toString());
			}
			if (next == -1) {
				throw new CarrotException("unexpected EOF waiting for closing " + end);
			}
			builder.append((char) next);
		}
	}

	private Token readNumber(char first) throws IOException {
		String numberStr = readUntil(first, NUMBER);
		Number value;
		if (numberStr.contains(".")) {
			value = Double.parseDouble(numberStr);
		} else {
			value = Integer.parseInt(numberStr);
		}
		return new Token(TokenType.NUMBER_LITERAL, value);
	}

	private Token readIdentifier(char first) throws IOException {
		String identifier = readUntil(first, IDENTIFIER_PART);
		switch (identifier) {
			case "true":
				return Token.TRUE;
			case "false":
				return Token.FALSE;
			// case "null":
			case "in":
				return Token.IN;
			default:
				return new Token(TokenType.IDENTIFIER, identifier);
		}
	}

	private String readUntil(char first, CharMatcher matcher) throws IOException {
		StringBuilder builder = new StringBuilder();
		builder.append(first);
		for (;;) {
			int next = reader.read();
			if (next == -1) {
				break;
			}
			if (!matcher.matches((char) next)) {
				reader.unread(next);
				break;
			}
			builder.append((char) next);
		}
		return builder.toString();
	}

	private Token requireNext(char required, Token token) throws IOException, CarrotException {
		if (reader.read() != required) {
			throw new CarrotException("expected " + required);
		}
		return token;
	}

	private Token checkNext(char match, Token ifMatch, Token ifNotMatch) throws IOException {
		int ch = reader.read();
		if (ch == match) {
			return ifMatch;
		}
		if (ch != -1) {
			reader.unread(ch);
		}
		return ifNotMatch;
	}

	private Token checkEndTag() throws IOException, CarrotException {
		if (reader.read() != '>') {
			throw new CarrotException("expected " + '>');
		}
		return eof(EndMode.TAG);
	}

	private Token eof(EndMode end) throws CarrotException {
		if (end != endMode) {
			throw new CarrotException("expected end mode " + endMode + " but got " + end);
		}
		return Token.EOF;
	}

	// private char readEscapeCharacter() throws IOException, CarrotException {
	// int ch = reader.read();
	// switch (ch) {
	// case -1:
	// throw new CarrotException("");
	// case 'u':
	// return readUnicode();
	// case 't':
	// return '\t';
	// case 'b':
	// return '\b';
	// case 'n':
	// return '\n';
	// case 'r':
	// return '\r';
	// case 'f':
	// return '\f';
	// case '\n':
	// case '\'':
	// case '"':
	// case '\\':
	// case '/':
	// return (char) ch;
	// default:
	// throw new CarrotException("");
	// }
	// }
	//
	// private char readUnicode() throws IOException, CarrotException {
	// StringBuilder buf = new StringBuilder();
	// for (int i = 0; i < 4; i++) {
	// int ch = reader.read();
	// if (ch == -1) {
	// throw new CarrotException("");
	// }
	// buf.append((char) ch);
	// }
	// return (char) Integer.parseInt(buf.toString());
	// }

}
