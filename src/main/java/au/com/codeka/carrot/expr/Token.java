package au.com.codeka.carrot.expr;

/**
 * A {@link Token} is something pulled off the statement stream and represents a
 * terminal like a string literal, number, identifier, etc.
 */
public abstract class Token {

	static final Token END = new SymbolToken(TokenType.END);
	static final Token RIGHT_PARENTHESIS = new SymbolToken(TokenType.RIGHT_PARENTHESIS);
	static final Token LEFT_PARENTHESIS = new SymbolToken(TokenType.LEFT_PARENTHESIS);
	static final Token RIGHT_BRACKET = new SymbolToken(TokenType.RIGHT_BRACKET);
	static final Token LEFT_BRACKET = new SymbolToken(TokenType.LEFT_BRACKET);
	static final Token ASSIGNMENT = new SymbolToken(TokenType.ASSIGNMENT);
	static final Token COMMA = new SymbolToken(TokenType.COMMA);
	static final Token DOT = new SymbolToken(TokenType.DOT);
	static final Token NOT = new SymbolToken(TokenType.NOT);
	static final Token LOGICAL_AND = new SymbolToken(TokenType.LOGICAL_AND);
	static final Token LOGICAL_OR = new SymbolToken(TokenType.LOGICAL_OR);
	static final Token EQUAL = new SymbolToken(TokenType.EQUAL);
	static final Token NOT_EQUAL = new SymbolToken(TokenType.NOT_EQUAL);
	static final Token LESS_THAN = new SymbolToken(TokenType.LESS_THAN);
	static final Token GREATER_THAN = new SymbolToken(TokenType.GREATER_THAN);
	static final Token LESS_THAN_OR_EQUAL = new SymbolToken(TokenType.LESS_THAN_OR_EQUAL);
	static final Token GREATER_THAN_OR_EQUAL = new SymbolToken(TokenType.GREATER_THAN_OR_EQUAL);
	static final Token PLUS = new SymbolToken(TokenType.PLUS);
	static final Token MINUS = new SymbolToken(TokenType.MINUS);
	static final Token MULTIPLY = new SymbolToken(TokenType.MULTIPLY);
	static final Token DIVIDE = new SymbolToken(TokenType.DIVIDE);
	static final Token MODULO = new SymbolToken(TokenType.MODULO);

	static final Token TRUE = new ValueToken(true);
	static final Token FALSE = new ValueToken(false);

	static Token valueToken(Object value) {
		return new ValueToken(value);
	}

	static Token identifierToken(String value) {
		return new IdentifierToken(value);
	}

	public abstract TokenType getType();

	public abstract Object getValue();

	public abstract String getIdentifier();

	private static class SymbolToken extends Token {

		private final TokenType type;

		private SymbolToken(TokenType type) {
			this.type = type;
		}

		@Override
		public TokenType getType() {
			return type;
		}

		@Override
		public Object getValue() {
			throw new IllegalStateException();
		}

		@Override
		public String getIdentifier() {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return type.toString();
		}
	}

	private static class ValueToken extends Token {

		private final Object value;

		private ValueToken(Object value) {
			this.value = value;
		}

		@Override
		public TokenType getType() {
			return TokenType.VALUE;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public String getIdentifier() {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return TokenType.VALUE + ": " + value;
		}
	}

	private static class IdentifierToken extends Token {

		private final String value;

		private IdentifierToken(String value) {
			this.value = value;
		}

		@Override
		public TokenType getType() {
			return TokenType.IDENTIFIER;
		}

		@Override
		public Object getValue() {
			throw new IllegalStateException();
		}

		@Override
		public String getIdentifier() {
			return value;
		}

		@Override
		public String toString() {
			return TokenType.IDENTIFIER + ": " + value;
		}
	}

}
