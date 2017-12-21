package au.com.codeka.carrot.expr;

import static au.com.codeka.carrot.expr.TokenType.DIVIDE;
import static au.com.codeka.carrot.expr.TokenType.EQUAL;
import static au.com.codeka.carrot.expr.TokenType.GREATER_THAN;
import static au.com.codeka.carrot.expr.TokenType.GREATER_THAN_OR_EQUAL;
import static au.com.codeka.carrot.expr.TokenType.IN;
import static au.com.codeka.carrot.expr.TokenType.LESS_THAN;
import static au.com.codeka.carrot.expr.TokenType.LESS_THAN_OR_EQUAL;
import static au.com.codeka.carrot.expr.TokenType.LOGICAL_AND;
import static au.com.codeka.carrot.expr.TokenType.LOGICAL_OR;
import static au.com.codeka.carrot.expr.TokenType.MINUS;
import static au.com.codeka.carrot.expr.TokenType.MULTIPLY;
import static au.com.codeka.carrot.expr.TokenType.NOT_EQUAL;
import static au.com.codeka.carrot.expr.TokenType.PLUS;

import au.com.codeka.carrot.CarrotException;

public class ExpressionParser {

	public static Term parse(Tokenizer tokenizer) throws CarrotException {
		return EXPRESSION_PARSER.parse(tokenizer);
	}

	private static final TermParser EXPRESSION_PARSER =
		// @formatter:off
		new BinaryTermParser(
		  new BinaryTermParser(
		    new BinaryTermParser(
		      new BinaryTermParser(
		        new BinaryTermParser(
		          new BinaryTermParser(
		            new ValueParser(),
		            MULTIPLY, DIVIDE),
		          PLUS, MINUS),
		        LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, IN),
		      EQUAL, NOT_EQUAL),
		    LOGICAL_AND),
		  LOGICAL_OR);
		// @formatter:on

}
