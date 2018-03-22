package com.catascopic.template.parse;

import java.io.IOException;
import java.util.List;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;

class UnpackForNode implements Node {

	private final List<String> varNames;
	private final Term iterable;
	private final Block block;

	UnpackForNode(List<String> varNames, Term iterable, Block block) {
		this.varNames = varNames;
		this.iterable = iterable;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		for (Object item : Values.toIterable(iterable.evaluate(scope))) {
			Assignment.unpack(scope, varNames, item);
			block.renderContent(writer, scope);
		}
	}

}
