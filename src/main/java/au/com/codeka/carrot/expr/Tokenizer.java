package au.com.codeka.carrot.expr;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Iterators;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.util.LineReader;

/**
 * Converts an input {@link Reader} into a stream of {@link Token}s.
 */
public class Tokenizer {

	private final LineReader reader;
	private @Nullable Character lookahead;
	private ArrayDeque<Token> tokens = new ArrayDeque<>();

	public Tokenizer(LineReader reader) throws CarrotException {
		this.reader = reader;
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
	 */
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
					"Expected token of type " + type + ", got " + tokens.peek().getType(),
					reader.getPointer());
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
	 */
	@Nonnull
	public Token require(Set<TokenType> types) throws CarrotException {
		Token token = expect(types);
		if (token == null) {
			throw new CarrotException(
					"Expected token of type " + types + ", got " + tokens.peek().getType(),
					reader.getPointer());
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
		return new CarrotException(String.format("%s, found: %s", msg, tokens.peek()),
				reader.getPointer());
	}

	private static final CharMatcher DIGIT = CharMatcher.inRange('0', '9');
	private static final CharMatcher DIGIT_OR_DOT = DIGIT.or(CharMatcher.is('.'));

	/**
	 * Advance to the {@link Token}, storing it in the member variable token.
	 *
	 * @throws CarrotException if there's an error parsing the tokens.
	 */
	private void next() throws CarrotException {
		int ch = nextChar();
		while (Character.isWhitespace(ch)) {
			ch = nextChar();
		}
		if (ch < 0) {
			tokens.add(new Token(TokenType.EOF));
			return;
		}

		int next;
		Token token;

		// TODO: Cache
		switch (ch) {
			case '(':
				token = new Token(TokenType.LEFT_PAREN);
				break;
			case ')':
				token = new Token(TokenType.RIGHT_PAREN);
				break;
			case '[':
				token = new Token(TokenType.LEFT_BRACKET);
				break;
			case ']':
				token = new Token(TokenType.RIGHT_BRACKET);
				break;
			case ',':
				token = new Token(TokenType.COMMA);
				break;
			case '.':
				token = new Token(TokenType.DOT);
				break;
			case '+':
				token = new Token(TokenType.PLUS);
				break;
			case '-':
				token = new Token(TokenType.MINUS);
				break;
			case '*':
				token = new Token(TokenType.MULTIPLY);
				break;
			case '/':
				token = new Token(TokenType.DIVIDE);
				break;
			case '&':
				next = nextChar();
				if (next != '&') {
					throw new CarrotException("Expected &&", reader.getPointer());
				}
				token = new Token(TokenType.LOGICAL_AND);
				break;
			case '|':
				next = nextChar();
				if (next != '|') {
					throw new CarrotException("Expected ||", reader.getPointer());
				}
				token = new Token(TokenType.LOGICAL_OR);
				break;
			case '=':
				next = nextChar();
				if (next != '=') {
					if (next > 0) {
						lookahead = (char) next;
					}
					token = new Token(TokenType.ASSIGNMENT);
				} else {
					token = new Token(TokenType.EQUAL);
				}
				break;
			case '!':
				next = nextChar();
				if (next != '=') {
					lookahead = (char) next;
					token = new Token(TokenType.NOT);
				} else {
					token = new Token(TokenType.NOT_EQUAL);
				}
				break;
			case '<':
				next = nextChar();
				if (next != '=') {
					if (next > 0) {
						lookahead = (char) next;
					}
					token = new Token(TokenType.LESS_THAN);
				} else {
					token = new Token(TokenType.LESS_THAN_OR_EQUAL);
				}
				break;
			case '>':
				next = nextChar();
				if (next != '=') {
					if (next > 0) {
						lookahead = (char) next;
					}
					token = new Token(TokenType.GREATER_THAN);
				} else {
					token = new Token(TokenType.GREATER_THAN_OR_EQUAL);
				}
				break;
			case '"':
			case '\'':
				String str = "";
				next = nextChar();
				while (next >= 0 && next != ch) {
					str += (char) next;
					next = nextChar();
				}
				if (next < 0) {
					throw new CarrotException("Unexpected end-of-file waiting for " + (char) ch,
							reader.getPointer());
				}
				token = new Token(TokenType.STRING_LITERAL, str);
				break;
			default:
				// if it starts with a number it's a number, else identifier.
				if (DIGIT.matches((char) ch)) {
					StringBuilder number = new StringBuilder();
					number.append((char) ch);
					next = nextChar();
					while (next >= 0 && DIGIT_OR_DOT.matches((char) next)) {
						number.append((char) next);
						next = nextChar();
					}
					if (next >= 0) {
						lookahead = (char) next;
					}
					Object value;
					String numberStr = number.toString();
					if (numberStr.contains(".")) {
						value = Double.parseDouble(numberStr);
					} else {
						value = Long.parseLong(numberStr);
					}
					token = new Token(TokenType.NUMBER_LITERAL, value);
				} else if (Character.isJavaIdentifierStart(ch)) {
					String identifier = "";
					identifier += (char) ch;
					next = nextChar();
					while (next > 0 && Character.isJavaIdentifierPart(next)) {
						identifier += (char) next;
						next = nextChar();
					}
					if (next > 0) {
						lookahead = (char) next;
					}
					switch (identifier) {
						case "or":
							token = new Token(TokenType.LOGICAL_OR);
							break;
						case "and":
							token = new Token(TokenType.LOGICAL_AND);
							break;
						case "not":
							token = new Token(TokenType.NOT);
							break;
						case "in":
							token = new Token(TokenType.IN);
							break;
						default:
							token = new Token(TokenType.IDENTIFIER, identifier);
					}
				} else {
					throw new CarrotException("Unexpected character: " + (char) ch,
							reader.getPointer());
				}
		}

		tokens.add(token);
	}

	private int nextChar() throws CarrotException {
		try {
			int ch;
			if (lookahead != null) {
				ch = lookahead;
				lookahead = null;
			} else {
				ch = reader.nextChar();
			}

			return ch;
		} catch (IOException e) {
			throw new CarrotException(e);
		}
	}
}
