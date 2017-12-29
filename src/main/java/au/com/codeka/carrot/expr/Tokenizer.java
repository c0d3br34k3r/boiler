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

	private PushbackReader reader;
	private Token peeked;

	public Tokenizer(Reader reader) {
		this(new PushbackReader(reader));
	}

	public Tokenizer(PushbackReader reader) {
		this.reader = new PushbackReader(reader, 1);
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
		if (peeked != null) {
			Token result = peeked;
			peeked = null;
			return result;
		}
		return parse();
	}

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
			throw new CarrotException(
					"Expected token of type " + type + ", got " + next.getType());
		}
	}

	private Token parse() throws CarrotException {
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
		return (String) next.getValue();
	}

	private static final CharMatcher NUMBER;
	private static final CharMatcher IDENTIFIER_START;
	private static final CharMatcher IDENTIFIER_PART;

	static {
		CharMatcher digit = CharMatcher.inRange('0', '9');
		NUMBER = digit.or(CharMatcher.anyOf("."));
		IDENTIFIER_START = CharMatcher.inRange('a', 'z')
				.or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.is('_'));
		IDENTIFIER_PART = IDENTIFIER_START.or(digit);
	}

	private Token parseToken(int ch) throws CarrotException, IOException {
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
			case '&': require('&'); return Token.LOGICAL_AND;
			case '|': require('|'); return Token.LOGICAL_OR;
			case '=': return matchEquals() ? Token.EQUAL: Token.ASSIGNMENT;
			case '!': return matchEquals() ? Token.NOT_EQUAL : Token.NOT;
			case '<': return matchEquals() ? Token.LESS_THAN_OR_EQUAL : Token.LESS_THAN;
			case '>': return matchEquals() ? Token.GREATER_THAN_OR_EQUAL : Token.GREATER_THAN;
			// TODO: Modulo
			case '%': return checkEndTag();
			// @formatter:on
			case '"':
			case '\'':
				return parseString((char) ch);
			case -1:
				// TODO:
				throw new CarrotException("");
			default:
		}
		if (NUMBER.matches((char) ch)) {
			return parseNumber((char) ch);
		}
		if (IDENTIFIER_START.matches((char) ch)) {
			return parseIdentifier((char) ch);
		}
		throw new CarrotException(
				"unexpected char " + (char) ch + ", (" + Character.getName(ch) + ")");
	}

	private Token parseString(char end) throws CarrotException, IOException {
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

	private Token parseNumber(char first) throws IOException {
		String numberStr = readUntil(first, NUMBER);
		Number value;
		if (numberStr.contains(".")) {
			value = Double.parseDouble(numberStr);
		} else {
			value = Integer.parseInt(numberStr);
		}
		return new Token(TokenType.NUMBER_LITERAL, value);
	}
	
//	private Token parseNumber2(char first) {
//		boolean dot = false;
//		StringBuilder builder = new StringBuilder();
//		builder.append(first);
//		loop: for (;;) {
//			int ch = reader.read();
//			if (ch == -1) {
//				break;
//			}
//			if (
//		}
//		return builder.toString();
//	}

	private Token parseIdentifier(char first) throws IOException {
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

	private void require(char required) throws IOException, CarrotException {
		if (reader.read() != required) {
			throw new CarrotException("expected " + required);
		}
	}

	private boolean matchEquals() throws IOException {
		int ch = reader.read();
		switch (ch) {
			case '=':
				return true;
			default:
				reader.unread(ch);
				// fallthrough
			case -1:
				return false;
		}
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
