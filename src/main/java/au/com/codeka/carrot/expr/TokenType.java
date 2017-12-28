package au.com.codeka.carrot.expr;

/**
 * An enumeration of the different types of {@link Token}s we can pull off the
 * statement parser.
 */
public enum TokenType {

	/** The end of the stream. */
	END,
//	
//	/** The end of the stream. */
//	START_TAG,
//	
//	/** The end of the stream. */
//	END_TAG,
//	
//	/** The end of the stream. */
//	START_ECHO,
//	
//	/** The end of the stream. */
//	END_ECHO,

	/** A string literal {@code "like this"}. */
	STRING_LITERAL(true),

	/** A number literal, like {@code 12} or {@code 12.34}. */
	NUMBER_LITERAL(true),

	/** A boolean literal, like {@code true} or {@code false}. */
	BOOLEAN_LITERAL(true),

	/** A Java-style identifier like {@code foo} or {@code bar}. */
	IDENTIFIER(true),

	/** Left-parenthesis: {@code (} */
	LEFT_PARENTHESIS,

	/** Right-parenthesis: {@code )} */
	RIGHT_PARENTHESIS,

	/** Left-square-bracket: {@code [} */
	LEFT_BRACKET,

	/** Right-square-bracket: {@code ]} */
	RIGHT_BRACKET,

	/** Single Equals: {@code =} */
	ASSIGNMENT,

	/** Comma: {@code ,} */
	COMMA,

	/** Semicolon: {@code ;} */
	SEMICOLON,

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
	PLUS(BinaryOperator.ADD, UnaryOperator.PLUS),

	/** Minus: {@code -} */
	MINUS(BinaryOperator.SUBTRACT, UnaryOperator.MINUS),

	/** Multiply: {@code *} */
	MULTIPLY(BinaryOperator.MULTIPLY),

	/** Divide: {@code /} */
	DIVIDE(BinaryOperator.DIVIDE),
	
	/** Divide: {@code /} */
	MODULO(BinaryOperator.MODULO),

	/** In: {@code in} */
	IN(BinaryOperator.IN);

	private final boolean hasValue;
	private final BinaryOperator binaryOperator;
	private final UnaryOperator unaryOperator;

	TokenType() {
		this(false, null, null);
	}

	TokenType(boolean hasValue) {
		this(hasValue, null, null);
	}

	TokenType(UnaryOperator unaryOperator) {
		this(false, null, unaryOperator);
	}

	TokenType(BinaryOperator binaryOperator) {
		this(false, binaryOperator, null);
	}

	TokenType(BinaryOperator binaryOperator, UnaryOperator unaryOperator) {
		this(false, binaryOperator, unaryOperator);
	}

	TokenType(boolean hasValue, BinaryOperator binaryOperator, UnaryOperator unaryOperator) {
		this.hasValue = hasValue;
		this.binaryOperator = binaryOperator;
		this.unaryOperator = unaryOperator;
	}

	boolean hasValue() {
		return hasValue;
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

}
