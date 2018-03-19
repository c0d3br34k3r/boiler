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
	private final Set<TokenType> tokenTypes;

	BinaryTermParser(TermParser termParser, TokenType first, TokenType... rest) {
		this.termParser = termParser;
		this.tokenTypes = Sets.immutableEnumSet(first, rest);
	}

	@Override
	public Term parse(Tokenizer tokenizer) {
		Term left = termParser.parse(tokenizer);
		while (tokenTypes.contains(tokenizer.peek())) {
			left = new BinaryTerm(left,
					tokenizer.next().getType().binaryOperator(),
					termParser.parse(tokenizer));
		}
		return left;
	}

}
