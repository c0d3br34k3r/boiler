package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class IfNode implements Node {

	private final Term condition;
	private final Block block;

	private IfNode(Term condition, Block block) {
		this.condition = condition;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		if (Values.isTrue(condition.evaluate(scope))) {
			block.render(writer, scope);
		} else {
			block.renderElse(writer, scope);
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
			public void build(BlockBuilder builder) {
				builder.add(new IfNode(condition, builder.parseBlock()));
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
			public void build(BlockBuilder builder) {
				builder.beginElse();
			}

			@Override
			public String toString() {
				return "ELSE";
			}
		};
	}
	
	private static class IfBuilder extends BlockBuilder implements Tag {

		@Override
		public void build(BlockBuilder builder) {
			builder.addIf(ifBuilder);
		}
		
	}

}
