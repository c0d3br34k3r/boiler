package au.com.codeka.carrot.expr.unary;

import java.util.Set;

import com.google.common.collect.Sets;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.Token;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

/**
 * A factory for unary {@link Term}s.
 *
 * @author Marten Gajda
 */
public final class UnaryTermParser implements TermParser {

	private final TermParser termParser;
	private final Set<TokenType> tokenTypes;

	public UnaryTermParser(TermParser termParser, TokenType first, TokenType... rest) {
		this.termParser = termParser;
		this.tokenTypes = Sets.immutableEnumSet(first, rest);
	}

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Token token = tokenizer.tryGet(tokenTypes);
		if (token != null) {
			return new UnaryTerm(token.getType().unaryOperator(), this.parse(tokenizer));
		}
		return termParser.parse(tokenizer);
	}

}
