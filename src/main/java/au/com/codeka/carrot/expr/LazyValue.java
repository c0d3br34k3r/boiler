package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

/**
 * A {@link Lazy} {@link Term} which is evaluated on access.
 *
 * @author Marten Gajda
 */
class LazyValue {

	private final Scope scope;
	private final Term term;

	LazyValue(Scope scope, Term term) {
		this.scope = scope;
		this.term = term;
	}

	Object value() {
		return term.evaluate(scope);
	}

}
