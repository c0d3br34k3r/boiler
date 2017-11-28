package au.com.codeka.carrot.expr.values;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TermParser;
import au.com.codeka.carrot.expr.Token;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.expr.Tokenizer;

/**
 * A {@link TermParser} which parses a constant string {@link Term} or delegates
 * to another parser if there is no string.
 *
 * @author Marten Gajda
 */
public final class StringTermParser implements TermParser {

	private final TermParser delegate;

	public StringTermParser(TermParser delegate) {
		this.delegate = delegate;
	}

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Token token = tokenizer.tryGet(TokenType.STRING_LITERAL);
		return token != null ? new StringTerm(token) : delegate.parse(tokenizer);
	}

	/**
	 * A trivial term containing only a constant string.
	 */
	private static final class StringTerm implements Term {

		private final Token token;

		public StringTerm(Token token) {
			this.token = token;
		}

		@Override
		public Object evaluate(Configuration config, Scope scope) throws CarrotException {
			return token.getValue();
		}

		@Override
		public String toString() {
			return "\"" + token.getValue().toString() + "\"";
		}
	}

}
