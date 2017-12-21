package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;

/**
 * A {@link Term} that evaluates the bound value of the identifier.
 */
class Variable implements Term {

	private final String name;

	public Variable(String name) {
		this.name = name;
	}

	@Override
	public Object evaluate(Configuration config, Scope scope) throws CarrotException {
		return scope.resolve(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
