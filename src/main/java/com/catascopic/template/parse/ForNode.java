package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;
import com.catascopic.template.parse.Variables.Names;

class ForNode implements Node {

	private final Names names;
	private final Term iterable;
	private final Block block;

	ForNode(Names names, Term iterable, Block block) {
		this.names = names;
		this.iterable = iterable;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		for (Object item : Values.toIterable(iterable.evaluate(scope))) {
			names.assign(scope, item);
			block.render(writer, scope);
		}
	}

	@Override
	public String toString() {
		return "for " + names + " in " + iterable + ": " + block;
	}

}
