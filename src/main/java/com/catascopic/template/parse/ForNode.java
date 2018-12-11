package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.catascopic.template.parse.Variables.NameAssigner;

class ForNode implements Node {

	private final NameAssigner names;
	private final Term sequence;
	private final Block block;

	private ForNode(NameAssigner names, Term sequence, Block block) {
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

	static Tag parseTag(Tokenizer tokenizer) {
		final NameAssigner names = Variables.parseNames(tokenizer);
		tokenizer.consumeIdentifier("in");
		final Term sequence = tokenizer.parseExpression();
		return new NodeBuilder() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginBlock(this);
			}

			@Override
			protected Node build(Block block) {
				return new ForNode(names, sequence, block);
			}

			@Override
			public String toString() {
				return "@{for " + names + " in " + sequence + "}";
			}
		};
	}

	@Override
	public String toString() {
		return "@{for " + names + " in " + sequence + "}" + block;
	}

}
