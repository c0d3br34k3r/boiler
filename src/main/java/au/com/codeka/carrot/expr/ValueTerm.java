package au.com.codeka.carrot.expr;

import com.google.common.base.Preconditions;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;

class ValueTerm implements Term {

	private final Object value;

	ValueTerm(Object value) {
		this.value = Preconditions.checkNotNull(value);
	}

	@Override
	public Object evaluate(Configuration config, Scope scope) throws CarrotException {
		return value;
	}

	@Override
	public String toString() {
		return value instanceof String ? "\"" + value + "\"" : value.toString();
	}
}
