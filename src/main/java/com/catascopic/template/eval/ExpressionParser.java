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

	private static final TermParser EXPRESSION_PARSER;

	static {
		ValueParser valueParser = new ValueParser();
		BinaryTermParser multiplication = new BinaryTermParser(
				valueParser,
				Symbol.STAR,
				Symbol.SLASH,
				Symbol.PERCENT);
		BinaryTermParser addition = new BinaryTermParser(
				multiplication,
				Symbol.PLUS,
				Symbol.MINUS);
		BinaryTermParser compare = new BinaryTermParser(
				addition,
				Symbol.LESS_THAN,
				Symbol.LESS_THAN_OR_EQUAL,
				Symbol.GREATER_THAN,
				Symbol.GREATER_THAN_OR_EQUAL);
		BinaryTermParser equal = new BinaryTermParser(
				compare,
				Symbol.EQUAL,
				Symbol.NOT_EQUAL);
		BinaryTermParser and = new BinaryTermParser(equal, Symbol.AND);
		EXPRESSION_PARSER = new BinaryTermParser(and, Symbol.OR);
	}

}
