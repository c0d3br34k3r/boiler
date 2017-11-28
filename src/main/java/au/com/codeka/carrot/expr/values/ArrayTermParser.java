package au.com.codeka.carrot.expr.values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

/**
 * A {@link TermParser} which parses an expression {@link Term} in parenthesis
 * or delegates to another parser if there is no opening parenthesis.
 *
 * @author Marten Gajda
 */
public final class ArrayTermParser implements TermParser {

	private final TermParser delegate;
	private final TermParser expressionParser;

	/**
	 * Creates an {@link ArrayTermParser}.
	 *
	 * @param delegate the "fallback" {@link TermParser} in case no opening
	 *        parenthesis has been found.
	 * @param expressionParser the {@link TermParser} to parse the expression in
	 *        parenthesis.
	 */
	public ArrayTermParser(TermParser delegate, TermParser expressionParser) {
		this.delegate = delegate;
		this.expressionParser = expressionParser;
	}

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		if (tokenizer.tryConsume(TokenType.LEFT_BRACKET)) {
			if (tokenizer.tryConsume(TokenType.RIGHT_BRACKET)) {
				return new ArrayTerm(Collections.<Term> emptyList());
			}
			List<Term> terms = new ArrayList<>();
			do {
				// parse the expression in between
				terms.add(expressionParser.parse(tokenizer));
				// consume the ")".
			} while (tokenizer.tryConsume(TokenType.COMMA));
			tokenizer.get(TokenType.RIGHT_BRACKET);
			return new ArrayTerm(terms);
		}
		return delegate.parse(tokenizer);
	}

	/**
	 * An identifier {@link Term}. An identifier term always evaluates to the
	 * name of the identifier.
	 */
	private static final class ArrayTerm implements Term {

		private List<Term> terms;

		public ArrayTerm(List<Term> terms) {
			this.terms = terms;
		}

		@Override
		public Object evaluate(final Configuration config, final Scope scope)
				throws CarrotException {
			Builder<Object> builder = ImmutableList.builder();
			for (Term term : terms) {
				builder.add(term.evaluate(config, scope));
			}
			return builder.build();
		}

		@Override
		public String toString() {
			return terms.toString();
		}
	}

}
