package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.expr.binary.AddOperator;
import au.com.codeka.carrot.expr.binary.AndOperator;
import au.com.codeka.carrot.expr.binary.BinaryOperator;
import au.com.codeka.carrot.expr.binary.Complement;
import au.com.codeka.carrot.expr.binary.DivOperator;
import au.com.codeka.carrot.expr.binary.EqOperator;
import au.com.codeka.carrot.expr.binary.GreaterOperator;
import au.com.codeka.carrot.expr.binary.InOperator;
import au.com.codeka.carrot.expr.binary.IterationOperator;
import au.com.codeka.carrot.expr.binary.LessOperator;
import au.com.codeka.carrot.expr.binary.MulOperator;
import au.com.codeka.carrot.expr.binary.OrOperator;
import au.com.codeka.carrot.expr.binary.SubOperator;
import au.com.codeka.carrot.expr.unary.MinusOperator;
import au.com.codeka.carrot.expr.unary.NotOperator;
import au.com.codeka.carrot.expr.unary.PlusOperator;
import au.com.codeka.carrot.expr.unary.UnaryOperator;

/**
 * An enumeration of the different types of {@link Token}s we can pull off the
 * statement parser.
 */
public enum TokenType {
	/**
	 * An unknown token, or the end of the stream.
	 */
	EOF(false),

	/**
	 * A string literal "like this".
	 */
	STRING_LITERAL(true),

	/**
	 * A number literal, like 12 or 12.34.
	 */
	NUMBER_LITERAL(true),

	/**
	 * A Java-style identifier like foo or bar.
	 */
	IDENTIFIER(true),

	/**
	 * Right-parenthesis: )
	 */
	RPAREN(false),

	/**
	 * Left-parenthesis: (
	 */
	LPAREN(false, RPAREN),

	/**
	 * Right-square-bracket: ]
	 */
	RSQUARE(false),

	/**
	 * Left-square-bracket: [
	 */
	LSQUARE(false, RSQUARE),

	/**
	 * Single Equals: =
	 */
	ASSIGNMENT(false),

	COMMA(false, new IterationOperator()),
	DOT(false),
	NOT(false, null, new NotOperator()),
	LOGICAL_AND(false, new AndOperator()),
	LOGICAL_OR(false, new OrOperator()),
	EQUALITY(false, new EqOperator()),
	INEQUALITY(false, new Complement(EQUALITY.binaryOperator)),
	LESS_THAN(false, new LessOperator()),
	GREATER_THAN(false, new GreaterOperator()),
	LESS_THAN_OR_EQUAL(false, new Complement(GREATER_THAN.binaryOperator)),
	GREATER_THAN_OR_EQUAL(false, new Complement(LESS_THAN.binaryOperator)),
	PLUS(false, new AddOperator(), new PlusOperator()),
	MINUS(false, new SubOperator(), new MinusOperator()),
	MULTIPLY(false, new MulOperator()),
	DIVIDE(false, new DivOperator()),
	IN(false, new InOperator());

	private final boolean hasValue;
	private final BinaryOperator binaryOperator;
	private final UnaryOperator unaryOperator;
	private final TokenType closingToken;

	TokenType(boolean hasValue) {
		this(hasValue, null, null, null);
	}

	TokenType(boolean hasValue, TokenType closingToken) {
		this(hasValue, null, null, closingToken);
	}

	TokenType(boolean hasValue, BinaryOperator binaryOperator) {
		this(hasValue, binaryOperator, null, null);
	}

	TokenType(boolean hasValue, BinaryOperator binaryOperator, UnaryOperator unaryOperator) {
		this(hasValue, binaryOperator, unaryOperator, null);
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
