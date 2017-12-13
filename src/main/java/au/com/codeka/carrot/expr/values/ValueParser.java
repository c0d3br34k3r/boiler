package au.com.codeka.carrot.expr.values;

import java.util.Set;

import com.google.common.collect.Sets;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.Token;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

/**
 * A {@link TermParser} which parses a constant number {@link Term} or delegates
 * to another parser if there is no number.
 *
 * @author Marten Gajda
 */
public final class ValueParser implements TermParser {

	private final TermParser delegate;
	private final TermParser expressionParser;

	public ValueParser(TermParser delegate, TermParser expressionParser) {
		this.delegate = delegate;
		this.expressionParser = expressionParser;
	}

	private static final Set<TokenType> TOKENS = Sets.immutableEnumSet(
			TokenType.NUMBER_LITERAL,
			TokenType.STRING_LITERAL,
			// TokenType.IDENTIFIER,
			TokenType.LEFT_PARENTHESIS);

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Token token = tokenizer.tryGet(TOKENS);
		if (token == null) {
			return delegate.parse(tokenizer);
		}
		// System.out.println(token);
		switch (token.getType()) {
			case NUMBER_LITERAL:
			case STRING_LITERAL:
				return new ValueTerm(token.getValue());
			// case IDENTIFIER:
			// return new ValueTerm(new Identifier((String) token.getValue()));
			case LEFT_PARENTHESIS:
				Term term = expressionParser.parse(tokenizer);
				tokenizer.get(TokenType.RIGHT_PARENTHESIS);
				return term;
			// TODO: Arrays and objects?
			default:
				throw new AssertionError();
		}
	}

	private static class ValueTerm implements Term {

		private final Object value;

		public ValueTerm(Object value) {
			this.value = value;
		}

		@Override
		public Object evaluate(Configuration config, Scope scope) throws CarrotException {
			return value;
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

}
