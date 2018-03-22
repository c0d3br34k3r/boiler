package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;

class ForNode implements Node {

	private final String varName;
	private final Term iterable;
	private final Block block;

	ForNode(String varName, Term iterable, Block block) {
		this.varName = varName;
		this.iterable = iterable;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		for (Object item : Values.toIterable(iterable.evaluate(scope))) {
			scope.set(varName, item);
			block.renderContent(writer, scope);
		}
	}
}
