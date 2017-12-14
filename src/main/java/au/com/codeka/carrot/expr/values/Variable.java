package au.com.codeka.carrot.expr.values;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;

/**
 * A {@link Term} decorator which evaluates the value of the decorated term to a
 * bound variable of the current scope.
 *
 * @author Marten Gajda
 */
public final class Variable implements Term {

	// TODO: What???
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
