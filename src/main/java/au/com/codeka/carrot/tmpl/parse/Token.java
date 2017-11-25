package au.com.codeka.carrot.tmpl.parse;

import java.util.Objects;

/**
 * Represents a token in a stream of tokens from the {@link Tokenizer}.
 */
public class Token {

	private final TokenType type;
	private final String value;

	/**
	 * Create a new {@link Token}.
	 *
	 * @param type The {@link TokenType} of the token to create.
	 * @param content The content to include in the token.
	 * @return A new {@link Token}.
	 */
	public Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * @return The {@link TokenType} of this token.
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * @return The contents of this token.
	 */
	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Token) {
			return ((Token) other).type == type
					&& ((Token) other).value.equals(value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public String toString() {
		return String.format("%s <%s>", type, value);
	}

}
