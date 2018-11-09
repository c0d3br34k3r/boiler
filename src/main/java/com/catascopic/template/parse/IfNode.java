package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class IfNode implements NodeCreator {

	private final Term condition;

	IfNode(Tokenizer tokenizer) {
		this.condition = tokenizer.parseExpression();
	}

	@Override
	public Node create(NodeStream stream) {
		final Block block = stream.getBlock();
		return new Node() {

			@Override
			public void render(Appendable writer, Scope scope)
					throws IOException {
				if (Values.isTrue(condition.evaluate(scope))) {
					block.render(writer, scope);
				} else {
					block.renderElse(writer, scope);
				}
			}
		};
	}

}
