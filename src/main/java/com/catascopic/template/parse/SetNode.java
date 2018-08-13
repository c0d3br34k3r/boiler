package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.parse.Variables.Assigner;

class SetNode implements Node {

	private final Assigner assigner;

	SetNode(Assigner assigner) {
		this.assigner = assigner;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		assigner.assign(scope);
	}

	@Override
	public String toString() {
		return "set " + assigner;
	}

}
