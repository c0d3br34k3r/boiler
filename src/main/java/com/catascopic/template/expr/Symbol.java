package com.catascopic.template.expr;

public enum Symbol implements Token {

	LEFT_PARENTHESIS,
	RIGHT_PARENTHESIS,
	LEFT_BRACKET,
	RIGHT_BRACKET,
	LEFT_CURLY_BRACKET,
	RIGHT_CURLY_BRACKET,
	ASSIGNMENT,
	COMMA,
	QUESTION_MARK,
	COLON,
	DOT,
	NOT(UnaryOperator.NOT),
	AND(BinaryOperator.AND),
	OR(BinaryOperator.OR),
	EQUAL(BinaryOperator.EQUAL),
	NOT_EQUAL(BinaryOperator.NOT_EQUAL),
	LESS_THAN(BinaryOperator.LESS_THAN),
	GREATER_THAN(BinaryOperator.GREATER_THAN),
	LESS_THAN_OR_EQUAL(BinaryOperator.LESS_THAN_OR_EQUAL),
	GREATER_THAN_OR_EQUAL(BinaryOperator.GREATER_THAN_OR_EQUAL),
	PLUS(BinaryOperator.ADD, UnaryOperator.POSITIVE),
	MINUS(BinaryOperator.SUBTRACT, UnaryOperator.NEGATIVE),
	STAR(BinaryOperator.MULTIPLY),
	SLASH(BinaryOperator.DIVIDE),
	PERCENT(BinaryOperator.MODULO);

	private final BinaryOperator binaryOperator;
	private final UnaryOperator unaryOperator;

	Symbol() {
		this(null, null);
	}

	Symbol(UnaryOperator unaryOperator) {
		this(null, unaryOperator);
	}

	Symbol(BinaryOperator binaryOperator) {
		this(binaryOperator, null);
	}

	Symbol(BinaryOperator binaryOperator, UnaryOperator unaryOperator) {
		this.binaryOperator = binaryOperator;
		this.unaryOperator = unaryOperator;
	}

	BinaryOperator binaryOperator() {
		if (binaryOperator == null) {
			throw new UnsupportedOperationException(
					String.format("%s is not a binary operator", this));
		}
		return binaryOperator;
	}

	UnaryOperator unaryOperator() {
		if (unaryOperator == null) {
			throw new UnsupportedOperationException(
					String.format("%s is not an unary operator", this));
		}
		return unaryOperator;
	}

	@Override
	public TokenType type() {
		return TokenType.SYMBOL;
	}

	@Override
	public Object value() {
		throw new IllegalStateException(this + " is not a value");
	}

	@Override
	public String identifier() {
		throw new IllegalStateException(this + " is not an identifier");
	}

	@Override
	public Symbol symbol() {
		return this;
	}

}
