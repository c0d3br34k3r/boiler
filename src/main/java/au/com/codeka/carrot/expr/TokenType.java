package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.expr.binary.BinaryOperator;
import au.com.codeka.carrot.expr.binary.BinaryOperator;
import au.com.codeka.carrot.expr.unary.UnaryOperator;
import au.com.codeka.carrot.expr.unary.UnaryOperator;

/**
 * An enumeration of the different types of {@link Token}s we can pull off the
 * statement parser.
 */
public enum TokenType {

	/** An unknown token, or the end of the stream. */
	EOF,

	/** A string literal {@code "like this"}. */
	STRING_LITERAL(true),

	/** A number literal, like {@code 12} or {@code 12.34}. */
	NUMBER_LITERAL(true),

	/** A Java-style identifier like {@code foo} or {@code bar}. */
	IDENTIFIER(true),

	/** Right-parenthesis: {@code )} */
	RIGHT_PARENTHESIS,

	/** Left-parenthesis: {@code (} */
	LEFT_PARENTHESIS(RIGHT_PARENTHESIS),

	/** Right-square-bracket: {@code ]} */
	RIGHT_BRACKET,

	/** Left-square-bracket: {@code [} */
	LEFT_BRACKET(RIGHT_BRACKET),

	/** Single Equals: {@code =} */
	ASSIGNMENT,

	/** Comma: {@code ,} */
	COMMA(BinaryOperator.ITERATE),

	/** Dot: {@code .} */
	DOT,

	/** Not: {@code !} */
	NOT(UnaryOperator.NOT),

	/** Logical and: {@code &&} */
	LOGICAL_AND(BinaryOperator.AND),

	/** Logical or: {@code ||} */
	LOGICAL_OR(BinaryOperator.OR),

	/** Equal: {@code ==} */
	EQUAL(BinaryOperator.EQUAL),

	/** Not equal: {@code !=} */
	NOT_EQUAL(BinaryOperator.NOT_EQUAL),

	/** Less than: {@code <} */
	LESS_THAN(BinaryOperator.LESS_THAN),

	/** Greater than: {@code >} */
	GREATER_THAN(BinaryOperator.GREATER_THAN),

	/** Less than or equal: {@code <=} */
	LESS_THAN_OR_EQUAL(BinaryOperator.LESS_THAN_OR_EQUAL),

	/** Greater than or equal: {@code >=} */
	GREATER_THAN_OR_EQUAL(BinaryOperator.GREATER_THAN_OR_EQUAL),

	/** Plus: {@code +} */
	PLUS(BinaryOperator.PLUS, UnaryOperator.PLUS),

	/** Minus : {@code -} */
	MINUS(BinaryOperator.MINUS, UnaryOperator.MINUS),

	/** Multiply: {@code *} */
	MULTIPLY(BinaryOperator.MULTIPLY),

	/** Divide: {@code /} */
	DIVIDE(BinaryOperator.DIVIDE),

	/** In: {@code in} */
	IN(BinaryOperator.IN);

	private final boolean hasValue;
	private final BinaryOperator binaryOperator;
	private final UnaryOperator unaryOperator;
	private final TokenType closingToken;

	TokenType() {
		this(false, null, null, null);
	}

	TokenType(boolean hasValue) {
		this(hasValue, null, null, null);
	}

	TokenType(TokenType closingToken) {
		this(false, null, null, closingToken);
	}

	TokenType(UnaryOperator unaryOperator) {
		this(false, null, unaryOperator, null);
	}

	TokenType(BinaryOperator binaryOperator) {
		this(false, binaryOperator, null, null);
	}

	TokenType(BinaryOperator binaryOperator, UnaryOperator unaryOperator) {
		this(false, binaryOperator, unaryOperator, null);
	}

	TokenType(boolean hasValue, BinaryOperator binaryOperator, UnaryOperator unaryOperator,
			TokenType closingToken) {
		this.hasValue = hasValue;
		this.binaryOperator = binaryOperator;
		this.unaryOperator = unaryOperator;
		this.closingToken = closingToken;
	}

	public boolean hasValue() {
		return hasValue;
	}

	public BinaryOperator binaryOperator() {
		if (binaryOperator == null) {
			throw new UnsupportedOperationException(
					String.format("%s is not a binary operator", this.toString()));
		}
		return binaryOperator;
	}

	public UnaryOperator unaryOperator() {
		if (unaryOperator == null) {
			throw new UnsupportedOperationException(
					String.format("%s is not an unary operator", this.toString()));
		}
		return unaryOperator;
	}

	public TokenType closingType() {
		return closingToken;
	}

}
