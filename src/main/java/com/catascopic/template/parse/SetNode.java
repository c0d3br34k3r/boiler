package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

public class SetNode implements Node {

	private final Assignment vars;

	SetNode(Assignment vars) {
		this.vars = vars;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		vars.assign(scope);
	}

}
