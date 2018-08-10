package com.catascopic.template.expr;

import java.io.EOFException;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import com.catascopic.template.Keyword;
import com.catascopic.template.TemplateParseException;
import com.google.common.base.CharMatcher;

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

	public Token peek() {
		if (peeked == null) {
			peeked = parse();
		}
		return peeked;
	}

	public Token next() {
		if (peeked == null) {
			return parse();
		}
		Token result = peeked;
		peeked = null;
		return result;
	}

	public void consume(Symbol symbol) {
		Token next = next();
		if (next.symbol() != symbol) {
			throw new TemplateParseException(
					"expected %s, got %s", symbol, next);
		}
	}

	public boolean tryConsume(Symbol allowed) {
		if (peek() == allowed) {
			next();
			return true;
		}
		return false;
	}

	public boolean tryConsume(String identifier) {
		Token token = peek();
		if (token.type() == TokenType.IDENTIFIER
				&& token.identifier().equals(identifier)) {
			next();
			return true;
		}
		return false;
	}

	public void end() {
		Token next = next();
		if (next.type() != TokenType.END) {
			throw new TemplateParseException(
					"expected end of tokens, got %s", next);
		}
	}

	public void consumeIdentifier(String value) {
		Token next = next();
		if (!next.identifier().equals(value)) {
			throw new TemplateParseException(
					"expected identifier %s, got %s", value, next);
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

	public Keyword parseKeyword() {
		return Keyword.get(parseIdentifier());
	}

	public String parseIdentifier() {
		Token next = next();
		if (next.type() != TokenType.IDENTIFIER) {
			throw new TemplateParseException("expected identifier, got %s",
					next);
		}
		return next.identifier();
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
		case '(': return Symbol.LEFT_PARENTHESIS;
		case ')': return Symbol.RIGHT_PARENTHESIS;
		case '[': return Symbol.LEFT_BRACKET;
		case ']': return Symbol.RIGHT_BRACKET;
		case '{': return Symbol.LEFT_CURLY_BRACKET;
		case '}': return Symbol.RIGHT_CURLY_BRACKET;
		case ',': return Symbol.COMMA;
		case '+': return Symbol.PLUS;
		case '-': return Symbol.MINUS;
		case '*': return Symbol.STAR;
		case '/': return Symbol.SLASH;
		case '?': return Symbol.QUESTION_MARK;
		case ':': return Symbol.COLON;
		case '.': return parseDot();
		case '&': require('&'); return Symbol.AND;
		case '|': require('|'); return Symbol.OR;
		case '%': return tryRead('>') ? end(Mode.TAG) : Symbol.PERCENT;
		case '=': return tryRead('=') ? Symbol.EQUALS : Symbol.ASSIGNMENT;
		case '!': return tryRead('=') ? Symbol.NOT_EQUAL : Symbol.NOT;
		case '<': return tryRead('=')
				? Symbol.LESS_THAN_OR_EQUAL
				: Symbol.LESS_THAN;
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
				"unexpected char '%c' (%s)", ch, Character.getName(ch));
	}

	private Token parseString(char end) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (;;) {
			int next = reader.read();
			switch (next) {
			case -1:
				throw new TemplateParseException("unclosed string");
			case '\\':
				readEscapeChar(builder);
				break;
			case '\'':
			case '"':
				if (next == end) {
					return Tokens.valueOf(builder.toString());
				}
				// fallthrough
			default:
				builder.append((char) next);
			}
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
		return Symbol.DOT;
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
		return Tokens.valueOf(Integer.parseInt(builder.toString()));
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
		return Tokens.valueOf(Double.parseDouble(builder.toString()));
	}

	private Token parseIdentifier(char first) throws IOException {
		StringBuilder builder = new StringBuilder().append(first);
		readUntil(builder, IDENTIFIER_PART);
		String identifier = builder.toString();
		switch (identifier) {
		case "true":
			return Tokens.TRUE;
		case "false":
			return Tokens.FALSE;
		default:
			return Tokens.identifier(identifier);
		}
	}

	private void readUntil(StringBuilder builder, CharMatcher matcher)
			throws IOException {
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
			throw new TemplateParseException(
					"expected '%c', got '%c'", required, ch);
		}
	}

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
			return Symbol.GREATER_THAN_OR_EQUAL;
		default:
			reader.unread(ch);
			// fallthrough
		case -1:
			return Symbol.GREATER_THAN;
		}
	}

	private Token end(Mode check) {
		if (mode != check) {
			throw new TemplateParseException(
					"expected end of %s but was end of %s", mode, check);
		}
		return Tokens.END;
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
			builder.append((char) ch);
			break;
		case 'u':
			int codePoint = Integer.parseInt(
					new String(readFully(reader, 4)), 16);
			builder.append(Character.toChars(codePoint));
			break;
		case -1:
			throw new TemplateParseException("unclosed string");
		default:
			throw new TemplateParseException("unexpected escaped '%c'", ch);
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
