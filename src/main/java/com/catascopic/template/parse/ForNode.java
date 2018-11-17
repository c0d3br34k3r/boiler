package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.catascopic.template.parse.Variables.Names;

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
		return new NodeBuilder() {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginBlock(this);
			}

			@Override
			Node build(Block block) {
				return new ForNode(names, sequence, block);
			}
		};
	}

}
