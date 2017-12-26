package au.com.codeka.carrot.expr;

import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

/**
 * A {@link Token} is something pulled off the statement stream and represents a
 * terminal like a string literal, number, identifier, etc.
 */
public class Token {

	private final TokenType tokenType;
	private final @Nullable Object value;

	public static final Token EOF = new Token(TokenType.END);
	public static final Token RIGHT_PARENTHESIS = new Token(TokenType.RIGHT_PARENTHESIS);
	public static final Token LEFT_PARENTHESIS = new Token(TokenType.LEFT_PARENTHESIS);
	public static final Token RIGHT_BRACKET = new Token(TokenType.RIGHT_BRACKET);
	public static final Token LEFT_BRACKET = new Token(TokenType.LEFT_BRACKET);
	public static final Token ASSIGNMENT = new Token(TokenType.ASSIGNMENT);
	public static final Token COMMA = new Token(TokenType.COMMA);
	public static final Token SEMICOLON = new Token(TokenType.SEMICOLON);
	public static final Token DOT = new Token(TokenType.DOT);
	public static final Token NOT = new Token(TokenType.NOT);
	public static final Token LOGICAL_AND = new Token(TokenType.LOGICAL_AND);
	public static final Token LOGICAL_OR = new Token(TokenType.LOGICAL_OR);
	public static final Token EQUAL = new Token(TokenType.EQUAL);
	public static final Token NOT_EQUAL = new Token(TokenType.NOT_EQUAL);
	public static final Token LESS_THAN = new Token(TokenType.LESS_THAN);
	public static final Token GREATER_THAN = new Token(TokenType.GREATER_THAN);
	public static final Token LESS_THAN_OR_EQUAL = new Token(TokenType.LESS_THAN_OR_EQUAL);
	public static final Token GREATER_THAN_OR_EQUAL = new Token(TokenType.GREATER_THAN_OR_EQUAL);
	public static final Token PLUS = new Token(TokenType.PLUS);
	public static final Token MINUS = new Token(TokenType.MINUS);
	public static final Token MULTIPLY = new Token(TokenType.MULTIPLY);
	public static final Token DIVIDE = new Token(TokenType.DIVIDE);
	public static final Token MODULO = new Token(TokenType.MODULO);
	public static final Token IN = new Token(TokenType.IN);
	
	public static final Token TRUE = new Token(TokenType.BOOLEAN_LITERAL, true);
	public static final Token FALSE = new Token(TokenType.BOOLEAN_LITERAL, false);

	public Token(TokenType tokenType) {
		this.tokenType = tokenType;
		this.value = null;
	}

	public Token(TokenType type, String value) {
		this(type, (Object) value);
	}

	public Token(TokenType type, Number value) {
		this(type, (Object) value);
	}

	private Token(TokenType type, boolean value) {
		this(type, (Object) value);
	}

	private Token(TokenType type, Object value) {
		this.tokenType = Preconditions.checkNotNull(type);
		this.value = Preconditions.checkNotNull(value);
		Preconditions.checkArgument(type.hasValue());
	}

	public TokenType getType() {
		return tokenType;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		String str = tokenType.toString();
		if (tokenType.hasValue()) {
			str += " <" + value + ">";
		}
		return str;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Token)) {
			return false;
		}
		Token otherToken = (Token) other;
		return otherToken.tokenType == tokenType && Objects.equals(otherToken.value, value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tokenType, value);
	}

}
