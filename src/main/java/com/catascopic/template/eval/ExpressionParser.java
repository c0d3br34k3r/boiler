package com.catascopic.template.eval;

class ExpressionParser {

	static Term parse(Tokenizer tokenizer) {
		Term left = EXPRESSION_PARSER.parse(tokenizer);
		if (tokenizer.tryConsume(Symbol.QUESTION_MARK)) {
			Term first = parse(tokenizer);
			tokenizer.consume(Symbol.COLON);
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
		            Symbol.STAR,
		            Symbol.SLASH,
		            Symbol.PERCENT),
		          Symbol.PLUS,
		          Symbol.MINUS),
		        Symbol.LESS_THAN,
		        Symbol.LESS_THAN_OR_EQUAL, 
		        Symbol.GREATER_THAN,
		        Symbol.GREATER_THAN_OR_EQUAL),
		      Symbol.EQUAL, 
		      Symbol.NOT_EQUAL),
		    Symbol.AND),
		  Symbol.OR);
	// @formatter:on

}
