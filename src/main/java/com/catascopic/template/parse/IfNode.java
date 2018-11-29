package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;

class IfNode implements Node {

	private final Term condition;
	private final Block block;
	private final Node elseNode;

	private IfNode(Term condition, Block block, Node elseNode) {
		this.condition = condition;
		this.block = block;
		this.elseNode = elseNode;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		if (Values.isTrue(condition.evaluate(scope))) {
			block.render(writer, scope);
		} else {
			elseNode.render(writer, scope);
		}
	}

	static Tag parseTag(Tokenizer tokenizer) {
		final Term condition = tokenizer.parseExpression();
		return new NodeBuilder() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginBlock(this);
			}

			@Override
			protected Node build(Block block) {
				return build(block, EmptyNode.EMPTY);
			}

			@Override
			protected Node build(Block block, Node elseNode) {
				return new IfNode(condition, block, elseNode);
			}

			@Override
			public String toString() {
				return "if " + condition;
			}
		};
	}

	public static Tag parseElseTag(Tokenizer tokenizer) {
		if (tokenizer.tryConsume("if")) {
			final Term condition = tokenizer.parseExpression();
			return new NodeBuilder() {

				@Override
				public void handle(TemplateParser parser) {
					parser.beginElse(this);
				}

				@Override
				protected Node build(Block block) {
					return build(block, EmptyNode.EMPTY);
				}

				@Override
				protected Node build(Block block, Node elseNode) {
					return new IfNode(condition, block, elseNode);
				}

				@Override
				public String toString() {
					return "else if " + condition;
				}
			};
		}
		return new NodeBuilder() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginElse(this);
			}

			@Override
			protected Node build(Block block) {
				return block;
			}

			@Override
			public String toString() {
				return "else";
			}
		};
	}

	@Override
	public String toString() {
		return elseNode == EmptyNode.EMPTY
				? "IF " + condition + " " + block
				: "IF " + condition + " " + block + " ELSE " + elseNode;
	}

}
