package au.com.codeka.carrot.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;

import au.com.codeka.carrot.CarrotException;

/**
 * Converts an input {@link Reader} into a stream of {@link Token}s.
 */
public class Tokenizer {

	private final PushbackReader reader;
	private Token next;

	public Tokenizer(Reader reader) throws CarrotException {
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
	@Deprecated
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
	@Deprecated
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
	@Nonnull
	public Token get(TokenType type) throws CarrotException {
		Token token = tryGet(type);
		if (token == null) {
			throw new CarrotException(
					"Expected token of type " + type + ", got " + next.getType());
		}
		return token;
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
	@Nonnull
	public Token get(Set<TokenType> types) throws CarrotException {
		Token token = tryGet(types);
		if (token == null) {
			throw new CarrotException(
					"Expected token of type " + types + ", got " + next.getType());
		}
		return token;
	}

	/**
	 * Returns the next token if it matches the given type, or else leaves it in
	 * place and returns null.
	 *
	 * @param type the type to match against
	 * @return the next token
	 * @throws CarrotException if there's an error parsing the token
	 */
	@Nullable
	public Token tryGet(TokenType type) throws CarrotException {
		if (type == next.getType()) {
			return advance();
		}
		return null;
	}

	/**
	 * Returns the next token if it matches one of the given types, or else
	 * leaves it in place and returns null.
	 *
	 * @param types the types to match against
	 * @return the next token
	 * @throws CarrotException if there's an error parsing the token
	 */
	@Nullable
	public Token tryGet(Set<TokenType> types) throws CarrotException {
		if (types.contains(next.getType())) {
			return advance();
		}
		return null;
	}

	/**
	 * Consumes the next token and returns true if it matches the given type, or
	 * else returns false.
	 *
	 * @param type the type to match against
	 * @return true if the next token matches the given type
	 * @throws CarrotException if there's an error parsing the token
	 */
	public boolean tryConsume(TokenType type) throws CarrotException {
		return tryGet(type) != null;
	}

	private Token advance() throws CarrotException {
		Token token = next;
		next();
		return token;
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

	private void next() throws CarrotException {
		try {
			int ch;
			do {
				ch = reader.read();
			} while (CharMatcher.whitespace().matches((char) ch));
			next = getToken(ch);
		} catch (IOException e) {
			throw new CarrotException(e);
		}
	}

	private Token getToken(int ch) throws CarrotException, IOException {
		switch (ch) {
			// @formatter:off
			case -1:  return Token.EOF;
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
			case '&': return required('&', Token.LOGICAL_AND);
			case '|': return required('|', Token.LOGICAL_OR);
			case '=': return either('=', Token.EQUAL, Token.ASSIGNMENT);
			case '!': return either('=', Token.NOT_EQUAL, Token.NOT);
			case '<': return either('=', Token.LESS_THAN_OR_EQUAL, Token.LESS_THAN);
			case '>': return either('=', Token.GREATER_THAN_OR_EQUAL, Token.GREATER_THAN);
			// @formatter:on
			case '"':
			case '\'':
				return readString((char) ch);
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

	private Token required(char required, Token token) throws IOException, CarrotException {
		if (reader.read() != required) {
			throw new CarrotException("expected " + required);
		}
		return token;
	}

	private Token either(char match, Token ifMatch, Token ifNotMatch) throws IOException {
		int ch = reader.read();
		if (ch == match) {
			return ifMatch;
		}
		if (ch != -1) {
			reader.unread(ch);
		}
		return ifNotMatch;
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
