package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;

/**
 * A {@link Lazy} {@link Term} which is evaluated on access.
 *
 * @author Marten Gajda
 */
class LazyValue {

	private final Configuration config;
	private final Scope scope;
	private final Term term;

	LazyValue(Configuration config, Scope scope, Term term) {
		this.config = config;
		this.scope = scope;
		this.term = term;
	}

	Object value() throws CarrotException {
		return term.evaluate(config, scope);
	}

}
