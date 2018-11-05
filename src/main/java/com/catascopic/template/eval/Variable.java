package com.catascopic.template.eval;

import com.catascopic.template.Scope;

class Variable implements Term {

	private final String name;

	public Variable(String name) {
		this.name = name;
	}

	@Override
	public Object evaluate(Scope scope) {
		return scope.get(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
