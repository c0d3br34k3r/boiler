package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.value.Values;

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
		System.out.println(condition.evaluate(scope));
		if (Values.isTrue(condition.evaluate(scope))) {
			block.render(writer, scope);
		} else {
			elseNode.render(writer, scope);
		}
	}

	static Tag parseTag(Tokenizer tokenizer) {
		final Location location = tokenizer.getLocation();
		final Term condition = tokenizer.parseTopLevelExpression();
		return new NodeBuilder(location) {

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
			protected void checkElse(BlockBuilder builder) {}

			@Override
			public String toString() {
				return "if block at " + location;
			}
		};
	}

	public static Tag parseElseTag(Tokenizer tokenizer) {
		final Location location = tokenizer.getLocation();
		if (tokenizer.tryConsume("if")) {
			final Term condition = tokenizer.parseTopLevelExpression();
			return new NodeBuilder(location) {

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
				protected void checkElse(BlockBuilder builder) {}

				@Override
				public String toString() {
					return "else-if block at " + location;
				}
			};
		}
		return new NodeBuilder(location) {

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
				return "else block at " + location;
			}
		};
	}

	@Override
	public String toString() {
		return elseNode == EmptyNode.EMPTY
				? "@{if " + condition + " " + block + "@{end}"
				: "@{if " + condition + " " + block + elseNode + "@{end}";
	}

}
