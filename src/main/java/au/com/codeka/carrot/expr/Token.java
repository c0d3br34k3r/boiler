package au.com.codeka.carrot.expr;

import java.util.Objects;

/**
 * A {@link Token} is something pulled off the statement stream and represents a
 * terminal like a string literal, number, identifier, etc.
 */
public class Token {

	static final Token END = new Token(TokenType.END);
	static final Token RIGHT_PARENTHESIS = new Token(TokenType.RIGHT_PARENTHESIS);
	static final Token LEFT_PARENTHESIS = new Token(TokenType.LEFT_PARENTHESIS);
	static final Token RIGHT_BRACKET = new Token(TokenType.RIGHT_BRACKET);
	static final Token LEFT_BRACKET = new Token(TokenType.LEFT_BRACKET);
	static final Token ASSIGNMENT = new Token(TokenType.ASSIGNMENT);
	static final Token COMMA = new Token(TokenType.COMMA);
	static final Token DOT = new Token(TokenType.DOT);
	static final Token NOT = new Token(TokenType.NOT);
	static final Token LOGICAL_AND = new Token(TokenType.LOGICAL_AND);
	static final Token LOGICAL_OR = new Token(TokenType.LOGICAL_OR);
	static final Token EQUAL = new Token(TokenType.EQUAL);
	static final Token NOT_EQUAL = new Token(TokenType.NOT_EQUAL);
	static final Token LESS_THAN = new Token(TokenType.LESS_THAN);
	static final Token GREATER_THAN = new Token(TokenType.GREATER_THAN);
	static final Token LESS_THAN_OR_EQUAL = new Token(TokenType.LESS_THAN_OR_EQUAL);
	static final Token GREATER_THAN_OR_EQUAL = new Token(TokenType.GREATER_THAN_OR_EQUAL);
	static final Token PLUS = new Token(TokenType.PLUS);
	static final Token MINUS = new Token(TokenType.MINUS);
	static final Token MULTIPLY = new Token(TokenType.MULTIPLY);
	static final Token DIVIDE = new Token(TokenType.DIVIDE);
	static final Token MODULO = new Token(TokenType.MODULO);
	static final Token TRUE = new Token(TokenType.VALUE, true);
	static final Token FALSE = new Token(TokenType.VALUE, false);

	private final TokenType tokenType;
	private final Object value;

	private Token(TokenType tokenType) {
		this.tokenType = tokenType;
		this.value = null;
	}

	Token(TokenType type, Object value) {
		this.tokenType = type;
		this.value = value;
	}

	public TokenType getType() {
		return tokenType;
	}

	public Object getValue() {
		if (value == null) {
			throw new IllegalStateException();
		}
		return value;
	}

	@Override
	public String toString() {
		return value == null ? tokenType.toString() : tokenType + ": " + value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Token)) {
			return false;
		}
		Token other = (Token) obj;
		return other.tokenType == tokenType && Objects.equals(other.value, value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tokenType, value);
	}

}
