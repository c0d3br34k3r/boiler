package com.catascopic.template.eval;

import com.catascopic.template.Context;

class Variable implements Term {

	private final String name;

	public Variable(String name) {
		this.name = name;
	}

	@Override
	public Object evaluate(Context context) {
		return context.get(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
