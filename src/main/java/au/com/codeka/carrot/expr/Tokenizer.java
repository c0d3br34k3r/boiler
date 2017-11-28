package au.com.codeka.carrot.expr;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import au.com.codeka.carrot.CarrotException;

/**
 * Converts an input {@link Reader} into a stream of {@link Token}s.
 */
public class Tokenizer {

	private final PushbackReader reader;
	private ArrayDeque<Token> tokens = new ArrayDeque<>();

	public Tokenizer(Reader reader) throws CarrotException {
		this.reader = new PushbackReader(reader);
		next();
	}

	/**
	 * Returns true if the current token is and of the given {@link TokenType}s.
	 * You can then use {@link #expect} to get the token (and advance to the
	 * next one).
	 *
	 * @param types The {@link TokenType} we want to accept.
	 * @return True, if the current token is of the given type, or false it's
	 *         not.
	 */
	public boolean accept(Set<TokenType> types) {
		return types.contains(tokens.peek().getType());
	}

	public boolean accept(TokenType type) {
		return type == tokens.peek().getType();
	}

	/**
	 * Returns true if the token at the given offset from the current is of the
	 * given {@link TokenType}. The 0th token is the current one, the 1st is the
	 * after that and so on. This can be used to "look ahead" into the token
	 * stream.
	 *
	 * @param offset The offset from "current" that we want to peek. 0 is the
	 *        current token, 1 is the next and so on.
	 * @param type The {@link TokenType} we want to accept.
	 * @return True, if the current token is of the given type, or false it's
	 *         not.
	 * @throws CarrotException If there's an error parsing the tokens.
	 * @throws IOException
	 */
	@Deprecated
	public boolean accept(int offset, TokenType type) throws CarrotException {
		if (offset == 0) {
			return accept(type);
		}
		while (tokens.size() <= offset) {
			next();
		}
		return Iterators.get(tokens.iterator(), offset).getType() == type;
	}

	@Nonnull
	public Token require(TokenType type) throws CarrotException {
		Token token = expect(type);
		if (token == null) {
			throw new CarrotException(
					"Expected token of type " + type + ", got " + tokens.peek().getType());
		}
		return token;
	}

	/**
	 * Returns a {@link Token} if it's one of the given types, or throws a
	 * {@link CarrotException} if it's not.
	 *
	 * @param types The {@link TokenType}s we want to accept one of.
	 * @return The next {@link Token}, if it's of the given type.
	 * @throws CarrotException If there's an error parsing the token, or if it's
	 *         not of the given type.
	 * @throws IOException
	 */
	@Nonnull
	public Token require(Set<TokenType> types) throws CarrotException {
		Token token = expect(types);
		if (token == null) {
			throw new CarrotException(
					"Expected token of type " + types + ", got " + tokens.peek().getType());
		}
		return token;
	}

	@Nullable
	public Token expect(TokenType type) throws CarrotException {
		if (type == tokens.peek().getType()) {
			return advance();
		}
		return null;
	}

	@Nullable
	public Token expect(Set<TokenType> types) throws CarrotException {
		if (types.contains(tokens.peek().getType())) {
			return advance();
		}
		return null;
	}

	private Token advance() throws CarrotException {
		Token token = tokens.remove();
		next();
		return token;
	}

	/**
	 * @throws CarrotException unless we're at the end of the tokens.
	 * @throws IOException
	 */
	public void end() throws CarrotException {
		require(TokenType.EOF);
	}

	/**
	 * Creates a {@link CarrotException} with the given message, populated with
	 * our current state.
	 *
	 * @param msg The message to create the exception with.
	 * @return A {@link CarrotException} with the given message (presumably
	 *         because we got an unexpected token).
	 */
	public CarrotException unexpected(String msg) {
		return new CarrotException(String.format("%s, found: %s", msg, tokens.peek()));
	}

	private static final CharMatcher DIGIT = CharMatcher.inRange('0', '9');
	private static final CharMatcher DIGIT_OR_DOT = DIGIT.or(CharMatcher.is('.'));
	private static final CharMatcher IDENTIFIER_PART =
			CharMatcher.forPredicate(new Predicate<Character>() {

				@Override
				public boolean apply(Character input) {
					return Character.isJavaIdentifierPart(input);
				}
			});

	/**
	 * Advance to the {@link Token}, storing it in the member variable token.
	 *
	 * @throws CarrotException if there's an error parsing the tokens.
	 * @throws IOException
	 */
	private void next() throws CarrotException {
		try {
			int ch;
			do {
				ch = reader.read();
			} while (Character.isWhitespace(ch));
			if (ch == -1) {
				tokens.add(Token.of(TokenType.EOF));
				return;
			}
			tokens.add(getToken(ch));
		} catch (IOException e) {
			throw new CarrotException(e);
		}
	}

	private Token getToken(int ch) throws CarrotException, IOException {
		// TODO: Cache
		switch (ch) {
			case '(':
				return Token.of(TokenType.LEFT_PAREN);
			case ')':
				return Token.of(TokenType.RIGHT_PAREN);
			case '[':
				return Token.of(TokenType.LEFT_BRACKET);
			case ']':
				return Token.of(TokenType.RIGHT_BRACKET);
			case ',':
				return Token.of(TokenType.COMMA);
			case '.':
				return Token.of(TokenType.DOT);
			case '+':
				return Token.of(TokenType.PLUS);
			case '-':
				return Token.of(TokenType.MINUS);
			case '*':
				return Token.of(TokenType.MULTIPLY);
			case '/':
				return Token.of(TokenType.DIVIDE);
			case '&':
				return required('&', TokenType.LOGICAL_AND);
			case '|':
				return required('|', TokenType.LOGICAL_OR);
			case '=':
				return either('=', TokenType.EQUAL, TokenType.ASSIGNMENT);
			case '!':
				return either('=', TokenType.NOT_EQUAL, TokenType.NOT);
			case '<':
				return either('=', TokenType.LESS_THAN_OR_EQUAL, TokenType.LESS_THAN);
			case '>':
				return either('=', TokenType.GREATER_THAN_OR_EQUAL, TokenType.GREATER_THAN);
			case '"':
			case '\'':
				return readString((char) ch);
			default:
				if (DIGIT.matches((char) ch)) {
					return readNumber((char) ch);
				}
				if (Character.isJavaIdentifierStart(ch)) {
					return readIdentifier((char) ch);
				}
				throw new CarrotException("Unexpected character [" + (char) ch + "], " + Character.getName(ch));
		}
	}

	private Token readString(char end) throws CarrotException, IOException {
		StringBuilder builder = new StringBuilder();
		for (;;) {
			int next = reader.read();
			if (next == end) {
				return new Token(TokenType.STRING_LITERAL, builder.toString());
			}
			if (next == -1) {
				throw new CarrotException("Unexpected end-of-file waiting for " + end);
			}
			builder.append((char) next);
		}
	}

	private Token readNumber(char first) throws IOException {
		String numberStr = readUntil(first, DIGIT_OR_DOT);
		Number value;
		if (numberStr.contains(".")) {
			value = Double.parseDouble(numberStr);
		} else {
			value = Long.parseLong(numberStr);
		}
		return new Token(TokenType.NUMBER_LITERAL, value);
	}

	private Token readIdentifier(char first) throws IOException {
		String identifier = readUntil(first, IDENTIFIER_PART);
		// TODO: is this okay?
		switch (identifier) {
			case "or":
				return Token.of(TokenType.LOGICAL_OR);
			case "and":
				return Token.of(TokenType.LOGICAL_AND);
			case "not":
				return Token.of(TokenType.NOT);
			case "in":
				return Token.of(TokenType.IN);
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

	private Token required(char required, TokenType type) throws IOException, CarrotException {
		if (reader.read() != required) {
			throw new CarrotException("expected " + required);
		}
		return Token.of(type);
	}

	private Token either(char match, TokenType ifMatch, TokenType ifNotMatch) throws IOException {
		int ch = reader.read();
		if (ch == match) {
			return Token.of(ifMatch);
		}
		if (ch != -1) {
			reader.unread(ch);
		}
		return Token.of(ifNotMatch);
	}

}
