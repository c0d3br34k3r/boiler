package com.catascopic.template.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.catascopic.template.parse.Variables.Names;
import com.google.common.collect.ImmutableList;

class ForNode implements Node {

	private final Names names;
	private final Term sequence;
	private final Block block;

	private ForNode(Names names, Term sequence, Block block) {
		this.names = names;
		this.sequence = sequence;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		for (Object item : Values.toIterable(sequence.evaluate(scope))) {
			names.assign(scope, item);
			block.render(writer, scope);
		}
	}

	@Override
	public String toString() {
		return "<% for" + names + " in " + sequence + "%>"
				+ block
				+ "<% end %>";
	}

	static Tag parseTag(Tokenizer tokenizer) {
		final Names names = Variables.parseNames(tokenizer);
		tokenizer.consumeIdentifier("in");
		final Term sequence = tokenizer.parseExpression();
		final List<Node> nodes = new ArrayList<>();
		return new BlockBuilderTag() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginBlock(this);
			}

			@Override
			public Node build() {
				return new ForNode(names, sequence, 
						new Block(ImmutableList.copyOf(nodes)));
			}

			@Override
			public void add(Node node) {
				nodes.add(node);
			}

			@Override
			public Node buildElse(Node elseNode) {
				throw new IllegalStateException();
			}
		};
	}

}
