package com.catascopic.template.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.collect.ImmutableList;

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

	@Override
	public String toString() {
		return elseNode == EmptyNode.EMPTY
				? "<% if " + condition + " %>" + block + "<% end %>"
				: "<% if " + condition + " %>" + block + "<% else %>"
						+ elseNode + "<% end %>";
	}

	static Tag parseTag(Tokenizer tokenizer) {
		final Term condition = tokenizer.parseExpression();
		final List<Node> nodes = new ArrayList<>();
		return new BlockBuilderTag() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginBlock(this);
			}

			@Override
			public Node build() {
				return buildElse(EmptyNode.EMPTY);
			}

			@Override
			public void add(Node node) {
				nodes.add(node);
			}

			@Override
			public Node buildElse(Node elseNode) {
				return new IfNode(condition, new Block(ImmutableList.copyOf(
						nodes)), elseNode);
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
			final List<Node> nodes = new ArrayList<>();
			return new BlockBuilderTag() {

				@Override
				public void handle(TemplateParser parser) {
					parser.beginElse(this);
				}

				@Override
				public Node build() {
					return buildElse(EmptyNode.EMPTY);
				}

				@Override
				public void add(Node node) {
					nodes.add(node);
				}

				@Override
				public Node buildElse(Node elseNode) {
					return new IfNode(condition, new Block(ImmutableList.copyOf(
							nodes)), elseNode);
				}

				@Override
				public String toString() {
					return "else if " + condition;
				}
			};
		}
		final List<Node> nodes = new ArrayList<>();
		return new BlockBuilderTag() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginElse(this);
			}

			@Override
			public Node build() {
				return new Block(ImmutableList.copyOf(nodes));
			}

			@Override
			public void add(Node node) {
				nodes.add(node);
			}

			@Override
			public Node buildElse(Node elseNode) {
				throw new IllegalStateException();
			}

			@Override
			public String toString() {
				return "else";
			}
		};
	}

}
