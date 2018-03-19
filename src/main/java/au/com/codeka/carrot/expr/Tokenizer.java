package au.com.codeka.carrot.expr;

import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import com.google.common.base.CharMatcher;

import au.com.codeka.carrot.TemplateParseException;

/**
 * Converts an input {@link Reader} into a stream of {@link Token}s.
 */
public class Tokenizer {

	private PushbackReader reader;
	private Token peeked;
	private final Mode mode;

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
	 * @throws TemplateParseException if there's an error parsing the token
	 */
	public TokenType peek() {
		if (peeked == null) {
			peeked = parse();
		}
		return peeked.getType();
	}

	public Token next() {
		if (peeked == null) {
			return parse();
		}
		Token result = peeked;
		peeked = null;
		return result;
	}

	/**
	 * Consumes the next token and asserts that it matches the given type.
	 *
	 * @param type the type to match against
	 * @throws TemplateParseException if there's an error parsing the token, or
	 *         if it doesn't match the given type
	 */
	public void consume(TokenType type) {
		Token next = next();
		if (next.getType() != type) {
			throw new TemplateParseException(
					"Expected token of type %s, got %s", type, next.getType());
		}
	}

	public boolean tryConsume(TokenType type) {
		if (peek() == type) {
			next();
			return true;
		}
		return false;
	}

	public void end() {
		Token next = next();
		if (next.getType() != TokenType.END) {
			throw new TemplateParseException(
					"expected end of tokens, got %s", next);
		}
	}

	public void consumeIdentifier(String value) {
		Token next = next();
		if (next.getType() != TokenType.IDENTIFIER || !next.getIdentifier().equals(value)) {
			throw new TemplateParseException("expected identifier %s, got %s", value, next);
		}
	}

	private Token parse() {
		try {
			int ch;
			do {
				ch = reader.read();
			} while (CharMatcher.whitespace().matches((char) ch));
			return parseToken(ch);
		} catch (IOException e) {
			throw new TemplateParseException(e);
		}
	}

	public Term parseExpression() {
		return ExpressionParser.parse(this);
	}

	public String parseIdentifier() {
		Token next = next();
		if (next.getType() != TokenType.IDENTIFIER) {
			throw new TemplateParseException("expected identifier, got %s", next);
		}
		return next.getIdentifier();
	}

	private static final CharMatcher DIGIT;
	private static final CharMatcher IDENTIFIER_START;
	private static final CharMatcher IDENTIFIER_PART;

	static {
		DIGIT = CharMatcher.inRange('0', '9');
		IDENTIFIER_START = CharMatcher.inRange('a', 'z')
				.or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.is('_'));
		IDENTIFIER_PART = IDENTIFIER_START.or(DIGIT);
	}

	private Token parseToken(int ch) throws IOException {
		switch (ch) {
		// @formatter:off
		case '(': return Token.LEFT_PARENTHESIS;
		case ')': return Token.RIGHT_PARENTHESIS;
		case '[': return Token.LEFT_BRACKET;
		case ']': return Token.RIGHT_BRACKET;
		case ',': return Token.COMMA;
		case '+': return Token.PLUS;
		case '-': return Token.MINUS;
		case '*': return Token.MULTIPLY;
		case '/': return Token.DIVIDE;
		case '?': return Token.QUESTION_MARK;
		case ':': return Token.COLON;
		case '.': return parseDot();
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
		char c = (char) ch;
		if (DIGIT.matches(c)) {
			return parseNumber(c);
		}
		if (IDENTIFIER_START.matches(c)) {
			return parseIdentifier(c);
		}
		throw new TemplateParseException(
				"unexpected char %c (%s)", ch, Character.getName(ch));
	}

	private Token parseString(char end) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (;;) {
			int next = reader.read();
			char c;
			switch (next) {
			case -1:
				throw new TemplateParseException("unclosed string");
			case '\\':
				readEscapeChar(builder);
				continue;
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

	private Token parseDot() throws IOException {
		int ch = reader.read();
		if (ch != -1) {
			char c = (char) ch;
			if (DIGIT.matches(c)) {
				return parseDouble(new StringBuilder().append('.').append(c));
			}
			reader.unread(ch);
		}
		return Token.DOT;
	}

	private Token parseNumber(char digit) throws IOException {
		return parseNumber(new StringBuilder().append(digit));
	}

	private Token parseNumber(StringBuilder builder) throws IOException {
		for (;;) {
			int ch = reader.read();
			if (ch == -1) {
				break;
			}
			char c = (char) ch;
			if (DIGIT.matches(c)) {
				builder.append(c);
			} else if (c == '.') {
				return parseDouble(builder.append('.'));
			} else {
				reader.unread(ch);
				break;
			}
		}
		return Token.valueToken(Integer.parseInt(builder.toString()));
	}

	private Token parseDouble(StringBuilder builder) throws IOException {
		for (;;) {
			int ch = reader.read();
			if (ch != -1) {
				break;
			}
			char c = (char) ch;
			if (DIGIT.matches(c)) {
				builder.append(c);
			} else {
				reader.unread(ch);
				break;
			}
		}
		return Token.valueToken(Double.parseDouble(builder.toString()));
	}

	private Token parseIdentifier(char first) throws IOException {
		StringBuilder builder = new StringBuilder().append(first);
		readUntil(builder, IDENTIFIER_PART);
		String identifier = builder.toString();
		switch (identifier) {
		case "true":
			return Token.TRUE;
		case "false":
			return Token.FALSE;
		default:
			return Token.identifierToken(identifier);
		}
	}

	private void readUntil(StringBuilder builder, CharMatcher matcher) throws IOException {
		for (;;) {
			int ch = reader.read();
			if (ch == -1) {
				break;
			}
			char c = (char) ch;
			if (matcher.matches(c)) {
				builder.append(c);
			} else {
				reader.unread(ch);
				break;
			}
		}
	}

	private void require(char required) throws IOException {
		int ch = reader.read();
		if (ch != required) {
			throw new TemplateParseException("expected [%c], got [%c]", required, ch);
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

	private Token parseGreaterThan() throws IOException {
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

	private Token end(Mode check) {
		if (mode != check) {
			throw new TemplateParseException(
					"expected end of %s but was end of %s", mode, check);
		}
		return Token.END;
	}

	private void readEscapeChar(StringBuilder builder) throws IOException {
		int ch = reader.read();
		switch (ch) {
		case 't':
			builder.append('\t');
			break;
		case 'n':
			builder.append('\n');
			break;
		case 'r':
			builder.append('\r');
			break;
		case '\'':
		case '"':
		case '\\':
			builder.append(ch);
			break;
		case 'u':
			int codePoint = Integer.parseInt(new String(readFully(reader, 4)), 16);
			builder.append(Character.toChars(codePoint));
			break;
		case -1:
			throw new TemplateParseException("unclosed string");
		default:
			throw new TemplateParseException("bad escaped char: %c", ch);
		}
	}

	public static char[] readFully(Reader in, int size) throws IOException {
		char[] buf = new char[size];
		int total = 0;
		while (total < buf.length) {
			int result = in.read(buf, total, buf.length - total);
			if (result == -1) {
				throw new EOFException();
			}
			total += result;
		}
		if (total < size) {
			throw new EOFException();
		}
		return buf;
	}

}
