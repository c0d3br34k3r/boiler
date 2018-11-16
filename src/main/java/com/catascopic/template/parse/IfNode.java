package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class IfNode implements Node {

	private final Term condition;
	private final Node block;
	private final Node elseBlock;

	private IfNode(Term condition, Node block, Node elseBlock) {
		this.condition = condition;
		this.block = block;
		this.elseBlock = elseBlock;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		if (Values.isTrue(condition.evaluate(scope))) {
			block.render(writer, scope);
		} else {
			elseBlock.render(writer, scope);
		}
	}

	@Override
	public String toString() {
		// TODO
		return super.toString();
	}

	static Tag parseTag(Tokenizer tokenizer) {
		final Term condition = tokenizer.parseExpression();
		return new Tag() {

			@Override
			public Node createNode(TagStream stream) {
				return new IfNode(condition, null, null);
			}

			@Override
			public String toString() {
				return "IF " + condition;
			}
		};
	}

	public static Tag parseElseTag(Tokenizer tokenizer) {
		if (tokenizer.tryConsume("if")) {
			return parseTag(tokenizer);
		}
		return new Tag() {
			
			@Override
			public Node createNode(TagStream stream) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String toString() {
				return "ELSE";
			}
		};
	}

}
