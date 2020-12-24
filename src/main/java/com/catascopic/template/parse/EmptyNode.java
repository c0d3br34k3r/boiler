package com.catascopic.template.parse;

import com.catascopic.template.Scope;

public enum EmptyNode implements Node {

	EMPTY_NODE;

	@Override
	public void render(Appendable writer, Scope scope) {
		// do nothing
	}

}
