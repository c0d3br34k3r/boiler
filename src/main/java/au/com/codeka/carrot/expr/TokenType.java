package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.expr.binary.BinaryOperator;
import au.com.codeka.carrot.expr.binary.BinaryOperators;
import au.com.codeka.carrot.expr.binary.Complement;
import au.com.codeka.carrot.expr.unary.UnaryOperator;
import au.com.codeka.carrot.expr.unary.UnaryOperators;

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
	RPAREN,

	/** Left-parenthesis: {@code (} */
	LPAREN(RPAREN),

	/** Right-square-bracket: {@code ]} */
	RSQUARE,

	/** Left-square-bracket: {@code [} */
	LSQUARE(RSQUARE),

	/** Single Equals: {@code =} */
	ASSIGNMENT,

	/** Comma: {@code ,} */
	COMMA(BinaryOperators.ITERATION),
	
	/** Dot: {@code .} */
	DOT,
	
	/** Not: {@code !} */
	NOT(UnaryOperators.NOT),
	
	/** Logical and: {@code &&} */
	LOGICAL_AND(BinaryOperators.AND),
	
	/** Logical or: {@code ||} */
	LOGICAL_OR(BinaryOperators.OR),
	
	/** Equal: {@code ==} */
	EQUAL(BinaryOperators.EQUALS),
	
	/** Not equal: {@code !=} */
	NOT_EQUAL(new Complement(BinaryOperators.EQUALS)),
	
	/** Less than: {@code <} */
	LESS_THAN(BinaryOperators.LESS_THAN),
	
	/** Greater than: {@code >} */
	GREATER_THAN(BinaryOperators.GREATER_THAN),
	
	/** Less than or equal: {@code <=} */
	LESS_THAN_OR_EQUAL(new Complement(GREATER_THAN.binaryOperator)),
	
	/** Greater than or equal: {@code >=} */
	GREATER_THAN_OR_EQUAL(new Complement(LESS_THAN.binaryOperator)),
	
	/** Plus: {@code +} */
	PLUS(BinaryOperators.ADDITION, UnaryOperators.PLUS),
	
	/** Minus : {@code -} */
	MINUS(BinaryOperators.SUBTRACTION, UnaryOperators.MINUS),
	
	/** Multiply: {@code *} */
	MULTIPLY(BinaryOperators.MULTIPLICATION),
	
	/** Divide: {@code /} */
	DIVIDE(BinaryOperators.DIVISION),
	
	/** In: {@code in} */
	IN(BinaryOperators.IN);

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
