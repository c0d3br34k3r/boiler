package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Tokenizer;

public class SetBlockNode implements Node, Tag {

	private final String name;
	private final Block block;

	private SetBlockNode(String name, Block block) {
		this.name = name;
		this.block = block;
	}

	@Override
	public void render(Appendable writer, Scope scope) {
		StringBuilder content = new StringBuilder();
		try {
			block.render(content, scope);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		scope.set(name, content.toString());
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.add(this);
	}

	static Tag parseTag(Tokenizer tokenizer) {
		final Location location = tokenizer.getLocation();
		final String identifier = tokenizer.parseIdentifier();
		return new NodeBuilder(location) {

			@Override
			public void handle(TemplateParser parser) {
				parser.beginBlock(this);
			}

			@Override
			protected Node build(Block block) {
				return new SetBlockNode(identifier, block);
			}

			@Override
			public String toString() {
				return "setblock block at " + location;
			}
		};
	}

	@Override
	public String toString() {
		return "setblock[" + name + "] {" + block + "}";
	}
}
