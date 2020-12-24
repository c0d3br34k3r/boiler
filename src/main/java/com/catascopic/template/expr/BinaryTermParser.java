package com.catascopic.template.expr;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

class BinaryTermParser implements TermParser {

	private final TermParser lowerOrder;
	private final ImmutableSet<Symbol> symbols;

	BinaryTermParser(TermParser lowerOrder, Symbol first, Symbol... rest) {
		this.lowerOrder = lowerOrder;
		this.symbols = Sets.immutableEnumSet(first, rest);
	}

	@Override
	public Term parse(Tokenizer tokenizer) {
		Term left = lowerOrder.parse(tokenizer);
		for (;;) {
			Token token = tokenizer.peek();
			if (token.type() == TokenType.SYMBOL && symbols.contains(token.symbol())) {
				left = new BinaryTerm(left, tokenizer.next().symbol().binaryOperator(),
						lowerOrder.parse(tokenizer));
			} else {
				return left;
			}
		}
	}

}
