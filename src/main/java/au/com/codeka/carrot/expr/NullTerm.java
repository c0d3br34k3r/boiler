package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

public enum NullTerm implements Term {

	INSTANCE;

	@Override
	public Object evaluate(Scope scope) {
		return null;
	}

}
