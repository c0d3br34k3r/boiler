package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

class Variable implements Term {

	private final String name;

	public Variable(String name) {
		this.name = name;
	}

	@Override
	public Object evaluate(Scope scope) {
		return scope.resolve(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
