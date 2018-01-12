package au.com.codeka.carrot.expr;

/**
 * An enumeration of the different types of {@link Token}s we can pull off the
 * statement parser.
 */
public enum TokenType {

	/** The end of the stream. */
	END,

	/**
	 * Any literal value, such as a string {@code "like this"}, a number, like
	 * {@code 12} or {@code 12.34}, or a boolean literal, like {@code true} or
	 * {@code false}.
	 */
	VALUE,

	/** A Java-style identifier like {@code foo} or {@code bar}. */
	IDENTIFIER,

	/** Left-parenthesis: {@code (} */
	LEFT_PARENTHESIS,

	/** Right-parenthesis: {@code )} */
	RIGHT_PARENTHESIS,

	/** Left-square-bracket: {@code [} */
	LEFT_BRACKET,

	/** Right-square-bracket: {@code ]} */
	RIGHT_BRACKET,

	/** The assignment operator, {@code =} */
	ASSIGNMENT,

	/** A comma, {@code ,} */
	COMMA,

	/** The dot (access) operator, {@code .} */
	DOT,

	/** The not operator, {@code !} */
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
	MODULO(BinaryOperator.MODULO);

	private final BinaryOperator binaryOperator;
	private final UnaryOperator unaryOperator;

	TokenType() {
		this(null, null);
	}

	TokenType(UnaryOperator unaryOperator) {
		this(null, unaryOperator);
	}

	TokenType(BinaryOperator binaryOperator) {
		this(binaryOperator, null);
	}

	TokenType(BinaryOperator binaryOperator, UnaryOperator unaryOperator) {
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

}
