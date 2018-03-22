package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.parse.Variables.Assigner;

public class SetNode implements Node {

	private final Assigner vars;

	SetNode(Assigner vars) {
		this.vars = vars;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		vars.assign(scope);
	}

}
