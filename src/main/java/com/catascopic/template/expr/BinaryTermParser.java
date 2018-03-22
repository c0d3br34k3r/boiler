package com.catascopic.template.expr;

import java.util.Set;

import com.google.common.collect.Sets;

class BinaryTermParser implements TermParser {

	private final TermParser termParser;
	private final Set<Symbol> symbols;

	BinaryTermParser(TermParser termParser, Symbol first, Symbol... rest) {
		this.termParser = termParser;
		this.symbols = Sets.immutableEnumSet(first, rest);
	}

	@Override
	public Term parse(Tokenizer tokenizer) {
		Term left = termParser.parse(tokenizer);
		for (;;) {
			Token token = tokenizer.peek();
			if (token.type() == TokenType.SYMBOL && symbols.contains(token.symbol())) {
				left = new BinaryTerm(left,
						tokenizer.next().symbol().binaryOperator(),
						termParser.parse(tokenizer));
			} else {
				return left;
			}
		}
	}

}
