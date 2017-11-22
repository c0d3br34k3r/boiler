package au.com.codeka.carrot.expr.binary;

import java.util.EnumSet;
import java.util.Set;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.Token;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

/**
 * A factory for binary {@link Term}s.
 *
 * @author Marten Gajda
 */
public final class BinaryTermParser implements TermParser {

	private final TermParser termParser;
	private final Set<TokenType> tokenTypes;

	public BinaryTermParser(TermParser termParser, TokenType first, TokenType... rest) {
		this.termParser = termParser;
		this.tokenTypes = EnumSet.of(first, rest);
	}

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Term left = termParser.parse(tokenizer);
		for (;;) {
			Token token = tokenizer.expect(tokenTypes);
			if (token == null) {
				return left;
			}
			left = new BinaryTerm(left,
					token.getType().binaryOperator(),
					termParser.parse(tokenizer));
		}
	}

}
