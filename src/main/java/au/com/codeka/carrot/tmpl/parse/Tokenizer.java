package au.com.codeka.carrot.tmpl.parse;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import javax.annotation.Nullable;

import com.google.common.io.LineReader;

import au.com.codeka.carrot.CarrotException;

/**
 * The {@link Tokenizer} takes an input stream of character and turns it into a
 * stream of {@link Token}s.
 *
 * <p>
 * Each {@link Token} represents a high-level component of the template. For
 * example, the following template:
 *
 * <p>
 * <code>Text {{ hello }} stuff {% if (blah) %} more stuff {% end %}</code>
 *
 * <p>
 * Corresponds to the following stream of tokens:
 *
 * <pre>
 * <code>TokenType=FIXED, Content="Text "
 *TokenType=ECHO, Content=" hello "
 *TokenType=FIXED, Content=" stuff "
 *TokenType=TAG, Content=" if (blah) "
 *TokenType=FIXED, Content=" more stuff "
 *TokenType=TAG, Content=" end "</code>
 * </pre>
 */
public class Tokenizer {

	private final PushbackReader reader;
	private final TokenFactory tokenFactory;

	/**
	 * Construct a new {@link Tokenizer} with the given {@link LineReader}, and
	 * a default {@link TokenFactory}.
	 *
	 * @param reader
	 */
	public Tokenizer(Reader reader) {
		this(reader, null);
	}

	/**
	 * Construct a new {@link Tokenizer} with the given {@link LineReader} and
	 * {@link TokenFactory}.
	 *
	 * @param reader
	 * @param tokenFactory A {@link TokenFactory} for creating the tokens. If
	 *        null, a default token factory that just creates instances of
	 *        {@link Token} is used.
	 */
	public Tokenizer(Reader reader, @Nullable TokenFactory tokenFactory) {
		this.reader = new PushbackReader(reader, 2);
		this.tokenFactory = tokenFactory == null ? new DefaultTokenFactory() : tokenFactory;
	}

	/**
	 * Gets the next token from the stream, or null if there's no tokens left.
	 *
	 * @return The next {@link Token} in the stream, or null if we're at the end
	 *         of the stream.
	 * @throws CarrotException when there's an error parsing the tokens.
	 */
	@Nullable
	public Token getNextToken() throws IOException, CarrotException {
		TokenType tokenType = TokenType.UNKNOWN;
		StringBuilder content = new StringBuilder();
		for (;;) {
			int i = reader.read();
			if (i == -1) {
				return tokenFactory.create(tokenType, content);
			}
			char ch = (char) i;
			switch (ch) {
				case '{':
					i = reader.read();
					if (i == -1) {
						content.append(ch);
						return tokenFactory.create(tokenType, content);
					}
					switch (i) {
						case '%':
							switch (tokenType) {
								case UNKNOWN:
									tokenType = TokenType.TAG;
									break;
								case FIXED:
									reader.unread("{%".toCharArray());
									return tokenFactory.create(tokenType, content);
								default:
									throw new CarrotException("Unexpected '{%'");
							}
							break;
						case '{':
							switch (tokenType) {
								case UNKNOWN:
									tokenType = TokenType.ECHO;
									break;
								case FIXED:
									reader.unread("{{".toCharArray());
									return tokenFactory.create(tokenType, content);
								default:
									throw new CarrotException("Unexpected '{{");
							}
							break;
						case '#':
							switch (tokenType) {
								case UNKNOWN:
									tokenType = TokenType.COMMENT;
									break;
								case FIXED:
									reader.unread("{#".toCharArray());
									return tokenFactory.create(tokenType, content);
								default:
									throw new CarrotException("Unexpected '{{");
							}
							break;
						case '\\':
						default:
							if (tokenType == TokenType.UNKNOWN) {
								tokenType = TokenType.FIXED;
							}
							content.append(ch);
							// if it's a '\\' we just eat the backslash, it's an
							// escape character
							if (i != '\\') {
								content.append((char) i);
							}
					}
					break;
				case '%':
				case '}':
				case '#':
					i = reader.read();
					if (i < 0) {
						content.append(ch);
						return tokenFactory.create(tokenType, content);
					}

					if ((char) i == '}') {
						if (tokenType == TokenType.ECHO && ch != '}') {
							throw new CarrotException("Expected '}}'");
						} else if (tokenType == TokenType.TAG && ch != '%') {
							throw new CarrotException("Expected '%}'");
						} else if (tokenType == TokenType.COMMENT && ch != '#') {
							throw new CarrotException("Expected '#}'");
						} else if (tokenType == TokenType.FIXED) {
							content.append(ch);
							content.append((char) i);
						} else {
							return tokenFactory.create(tokenType, content);
						}
					} else {
						content.append(ch);
						content.append((char) i);
					}
					break;
				default:
					if (tokenType == TokenType.UNKNOWN) {
						tokenType = TokenType.FIXED;
					}
					content.append(ch);
					break;
			}
		}
	}

	public Token getNextToken2() throws IOException, CarrotException {
		StringBuilder content = new StringBuilder();
		int c = reader.read();
		switch (c) {
			case -1:
				return new Token(TokenType.FIXED, "");
			case '{':
				int c2 = reader.read();
				switch (c2) {
					case -1:
					case '{':
					case '%':
					case '#':
				}
		}
		return null;
	}

	private interface ParseMode {

		Token parse(Tokenizer tokenizer) throws IOException;
	}

	private static class TagParseMode implements ParseMode {

		private final char end;
		private final TokenType type;

		private TagParseMode(char end, TokenType type) {
			this.end = end;
			this.type = type;
		}

		@Override
		public Token parse(Tokenizer tokenizer) throws IOException {
			return new Token(type, tokenizer.parseTag(end));
		}
	}

	private static final ParseMode TAG = new TagParseMode('%', TokenType.TAG);
	private static final ParseMode ECHO = new TagParseMode('}', TokenType.ECHO);
	private static final ParseMode COMMENT = new TagParseMode('#', TokenType.COMMENT);

	private static final ParseMode LITERAL = new ParseMode() {

		@Override
		public Token parse(Tokenizer tokenizer) throws IOException {
			return new Token(TokenType.FIXED, tokenizer.parseLiteral());
		}
	};

	private ParseMode mode = LITERAL;

	private String parseTag(char end) throws IOException {
		StringBuilder content = new StringBuilder();
		for (;;) {
			int c = reader.read();
			if (c == end) {
				int c2 = reader.read();
				if (c2 == '}') {
					return content.toString();
				}
				reader.unread(c2);
			} else if (c == -1) {
				return content.toString();
			}
			content.append(c);
		}
	}

	private String parseLiteral() throws IOException {
		StringBuilder content = new StringBuilder();
		for (;;) {
			int c = reader.read();
			switch (c) {
				case -1:
					return content.toString();
				case '{':
					int c2 = reader.read();
					switch (c2) {
						case -1:
							return null;
						case '{':
						case '%':
						case '#':
							mode = null;
							return content.toString();
						default:
							reader.unread(c2);
					}
				default:
					content.append(c);
			}
		}
	}

	private class DefaultTokenFactory implements TokenFactory {
		@Override
		public Token create(TokenType type, StringBuilder content) {
			switch (type) {
				case UNKNOWN:
					return null;
				default:
					return new Token(type, content.toString());
			}
		}
	}

}
