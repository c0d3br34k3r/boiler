package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

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
