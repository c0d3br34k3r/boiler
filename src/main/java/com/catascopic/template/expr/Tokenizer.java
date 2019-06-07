package com.catascopic.template.expr;

import java.io.IOException;

import com.catascopic.template.Context;
import com.catascopic.template.Trackable;
import com.catascopic.template.Location;
import com.catascopic.template.TrackingReader;
import com.catascopic.template.TemplateRenderException;
import com.catascopic.template.TemplateParseException;
import com.google.common.base.CharMatcher;

public class Tokenizer implements Trackable {

	private TrackingReader reader;
	private Token peeked;

	public Tokenizer(TrackingReader reader) {
		this.reader = reader;
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
		if (next != symbol) {
			throw new TemplateParseException(reader, "expected %s, got %s", symbol, next);
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
		if (token.type() == TokenType.IDENTIFIER && token.identifier().equals(identifier)) {
			next();
			return true;
		}
		return false;
	}

	public void end() {
		Token next = next();
		if (next.type() != TokenType.END) {
			throw new TemplateParseException(reader, "expected end of tokens, got %s", next);
		}
	}

	public void consumeIdentifier(String value) {
		Token next = next();
		if (next.type() != TokenType.IDENTIFIER || !next.identifier().equals(value)) {
			throw new TemplateParseException(reader, "expected %s, got %s", value, next);
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
			throw new TemplateParseException(reader, e);
		}
	}

	public Term parseTopLevelExpression() {
		final Location location = getLocation();
		final Term expression = parseExpression();
		return new Term() {

			@Override
			public Object evaluate(Context context) {
				try {
					return expression.evaluate(context);
				} catch (TemplateRenderException e) {
					e.addLocation(location);
					throw e;
				}
			}

			@Override
			public String toString() {
				return expression.toString();
			}
		};
	}

	public Term parseExpression() {
		return ExpressionParser.parse(this);
	}

	public String parseIdentifier() {
		Token next = next();
		if (next.type() != TokenType.IDENTIFIER) {
			throw new TemplateParseException(reader, "expected identifier, got %s", next);
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
		case '~': return Symbol.TILDE;
		case '*': return reader.tryRead('*')
				? Symbol.POWER
				: Symbol.STAR;
		case '/': return Symbol.SLASH;
		case '?': return Symbol.QUESTION_MARK;
		case ':': return Symbol.COLON;
		case '%': return Symbol.PERCENT;
		case '.': return parseDot();
		case '&': require('&'); return Symbol.AND;
		case '|': require('|'); return Symbol.OR;
		case '=': return reader.tryRead('=')
				? Symbol.EQUAL
				: Symbol.ASSIGNMENT;
		case '!': return reader.tryRead('=')
				? Symbol.NOT_EQUAL
				: Symbol.NOT;
		case '<': return reader.tryRead('=')
				? Symbol.LESS_THAN_OR_EQUAL
				: Symbol.LESS_THAN;
		case '>': return reader.tryRead('=')
				? Symbol.GREATER_THAN_OR_EQUAL
				: Symbol.GREATER_THAN;
		case -1: return Tokens.END;
		// @formatter:on
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
		throw new TemplateParseException(reader,
				"unexpected char '%c' (%s)", ch, Character.getName(ch));
	}

	private void require(char required) throws IOException {
		int ch = reader.read();
		if (ch != required) {
			throw new TemplateParseException(reader, "expected '%c', got '%c'", required, ch);
		}
	}

	private Token parseString(char end) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (;;) {
			int next = reader.read();
			switch (next) {
			case -1:
				throw new TemplateParseException(reader, "unclosed string");
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
			if (ch == -1) {
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
		case "null":
			return Tokens.NULL;
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

	private void readEscapeChar(StringBuilder builder) throws IOException {
		int ch = reader.read();
		switch (ch) {
		case 't':
			builder.append('\t');
			break;
		case 'r':
			builder.append('\r');
			break;
		case 'n':
			builder.append('\n');
			break;
		case '\'':
		case '"':
		case '\\':
			builder.append((char) ch);
			break;
		case 'u':
			builder.append(readCodePoint());
			break;
		case -1:
			throw new TemplateParseException(reader, "unclosed string");
		default:
			throw new TemplateParseException(reader, "unexpected escaped '%c'", ch);
		}
	}

	private char readCodePoint() throws IOException {
		char[] buf = new char[4];
		for (int i = 0; i < 4; i++) {
			int ch = reader.read();
			if (ch == -1) {
				throw new TemplateParseException(reader, "unfinished unicode escape");
			}
			buf[i] = (char) ch;
		}
		String escape = new String(buf);
		try {
			return (char) Integer.parseInt(escape, 16);
		} catch (NumberFormatException e) {
			throw new TemplateParseException(reader, e, "invalid unicode escape: %s", escape);
		}
	}

	@Override
	public Location getLocation() {
		return reader.getLocation();
	}

}
