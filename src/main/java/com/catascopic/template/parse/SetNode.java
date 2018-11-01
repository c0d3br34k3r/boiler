package com.catascopic.template.parse;

import com.catascopic.template.Assigner;
import com.catascopic.template.Scope;

class SetNode implements Node {

	private final Assigner assigner;

	SetNode(Assigner assigner) {
		this.assigner = assigner;
	}

	@Override
	public void render(Appendable writer, Scope scope) {
		assigner.assign(scope);
	}

	@Override
	public String toString() {
		return "<% set " + assigner + " %>";
	}

}
