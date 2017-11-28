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
 * A {@link TermParser} which parses an identifier {@link Term} or delegates to
 * another parser if there is no identifier.
 *
 * @author Marten Gajda
 */
public final class IdentifierTermParser implements TermParser {

	private final TermParser delegate;

	public IdentifierTermParser(TermParser delegate) {
		this.delegate = delegate;
	}

	@Override
	public Term parse(Tokenizer tokenizer) throws CarrotException {
		Token token = tokenizer.tryGet(TokenType.IDENTIFIER);
		return token != null ? new IdentifierTerm(token) : delegate.parse(tokenizer);
	}

	/**
	 * An identifier {@link Term}. An identifier term always evaluates to the
	 * name of the identifier.
	 */
	private static final class IdentifierTerm implements Term {

		private final Token token;

		public IdentifierTerm(Token token) {
			this.token = token;
		}

		@Override
		public Object evaluate(Configuration config, Scope scope) throws CarrotException {
			return token.getValue().toString();
		}

		@Override
		public String toString() {
			return token.getValue().toString();
		}
	}

}
