package au.com.codeka.carrot.expr;


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
		            TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.MODULO),
		          TokenType.PLUS, TokenType.MINUS),
		        TokenType.LESS_THAN, 
		        TokenType.LESS_THAN_OR_EQUAL, 
		        TokenType.GREATER_THAN, 
		        TokenType.GREATER_THAN_OR_EQUAL, TokenType.IN),
		      TokenType.EQUAL, TokenType.NOT_EQUAL),
		    TokenType.LOGICAL_AND),
		  TokenType.LOGICAL_OR);
		// @formatter:on

}
