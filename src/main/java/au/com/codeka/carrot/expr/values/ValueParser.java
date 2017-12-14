package au.com.codeka.carrot.expr.values;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.Token;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;
import au.com.codeka.carrot.expr.accessible.AccessTerm;

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
			TokenType.BOOLEAN_LITERAL,
			TokenType.IDENTIFIER,
			TokenType.LEFT_PARENTHESIS);

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Token token = tokenizer.tryGet(TOKENS);
		if (token == null) {
			return delegate.parse(tokenizer);
		}
		switch (token.getType()) {
			case NUMBER_LITERAL:
			case STRING_LITERAL:
			case BOOLEAN_LITERAL:
				return new ValueTerm(token.getValue());
			case IDENTIFIER:
				return getAccessTerm(tokenizer, new Variable((String) token.getValue()));
			case LEFT_PARENTHESIS:
				Term term = expressionParser.parse(tokenizer);
				tokenizer.get(TokenType.RIGHT_PARENTHESIS);
				return term;
			// TODO: Arrays and objects?
			default:
				throw new AssertionError();
		}
	}

	private static final Set<TokenType> ACCESS_TYPE =
			Sets.immutableEnumSet(TokenType.DOT, TokenType.LEFT_BRACKET);

	private Term getAccessTerm(Tokenizer tokenizer, Term accessTerm) throws CarrotException {
		Term result = accessTerm;
		for (;;) {
			Token token = tokenizer.tryGet(ACCESS_TYPE);
			if (token == null) {
				break;
			}
			switch (token.getType()) {
				case DOT:
					result = new AccessTerm(result,
							new ValueTerm(tokenizer.get(TokenType.IDENTIFIER).getValue()));
					break;
				case LEFT_BRACKET:
					result = new AccessTerm(result, expressionParser.parse(tokenizer));
					break;
				// case LEFT_PARENTHESIS:
				// result = new Unaccessible(new MethodTerm(result,
				// iterationTerm.parse(tokenizer)));
				// break;
				default:
			}
			if (token.getType().closingType() != null) {
				tokenizer.get(token.getType().closingType());
			}
		}
		return result;
	}

	private static class ValueTerm implements Term {

		private final Object value;

		public ValueTerm(Object value) {
			this.value = Preconditions.checkNotNull(value);
		}

		@Override
		public Object evaluate(Configuration config, Scope scope) throws CarrotException {
			return value;
		}

		@Override
		public String toString() {
			return value instanceof String ? "\"" + value + "\"" : value.toString();
		}
	}

}
