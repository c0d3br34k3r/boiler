package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.parse.Variables.NameAssigner;
import com.catascopic.template.value.Values;

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
		final Location location = tokenizer.getLocation();
		final NameAssigner names = Variables.parseNames(tokenizer);
		tokenizer.consumeIdentifier("in");
		final Term sequence = tokenizer.parseTopLevelExpression();
		return new NodeBuilder(location) {

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
				return "for block at " + location;
			}
		};
	}

	@Override
	public String toString() {
		return "for[" + names + " in " + sequence + "] {" + block + "}";
	}

}
