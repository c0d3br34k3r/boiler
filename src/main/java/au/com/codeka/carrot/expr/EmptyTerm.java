package au.com.codeka.carrot.expr;

import java.util.Collections;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;

/**
 * An empty {@link Term}. Empty terms always evaluate to an empty
 * {@link Iterable}.
 */
public enum EmptyTerm implements Term {

	EMPTY;

	@Override
	public Object evaluate(Configuration config, Scope scope) throws CarrotException {
		return Collections.emptySet();
	}

}
