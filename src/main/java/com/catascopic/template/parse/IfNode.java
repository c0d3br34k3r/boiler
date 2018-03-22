package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;

class IfNode implements Node {

	private final Term condition;
	private final Block block;

	IfNode(Term condition, Block block) {
		this.condition = condition;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		if (Values.isTrue(condition)) {
			block.renderContent(writer, scope);
		} else {
			block.renderLinked(writer, scope);
		}
	}

}
