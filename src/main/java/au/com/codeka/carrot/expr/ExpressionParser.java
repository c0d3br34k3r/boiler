package au.com.codeka.carrot.expr;

class ExpressionParser {

	static Term parse(Tokenizer tokenizer) {
		Term left = EXPRESSION_PARSER.parse(tokenizer);
		if (tokenizer.tryConsume(TokenType.QUESTION_MARK)) {
			Term first = parse(tokenizer);
			tokenizer.consume(TokenType.COLON);
			Term second = parse(tokenizer);
			left = new ConditionalTerm(left, first, second);
		}
		return left;
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
		            TokenType.MULTIPLY,
		            TokenType.DIVIDE,
		            TokenType.MODULO),
		          TokenType.PLUS,
		          TokenType.MINUS),
		        TokenType.LESS_THAN,
		        TokenType.LESS_THAN_OR_EQUAL, 
		        TokenType.GREATER_THAN,
		        TokenType.GREATER_THAN_OR_EQUAL),
		      TokenType.EQUAL, 
		      TokenType.NOT_EQUAL),
		    TokenType.LOGICAL_AND),
		  TokenType.LOGICAL_OR);
	// @formatter:on

}
