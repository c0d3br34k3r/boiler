package au.com.codeka.carrot.expr;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A factory for binary {@link Term}s.
 *
 * @author Marten Gajda
 */
final class BinaryTermParser implements TermParser {

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
			Symbol symbol = tokenizer.tryConsume(symbols);
			if (symbol == null) {
				return left;
			}
			left = new BinaryTerm(left, symbol.binaryOperator(), termParser.parse(tokenizer));
		}
	}

}
