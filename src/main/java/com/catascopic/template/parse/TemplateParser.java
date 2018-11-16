package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

public class TemplateParser {

	public static Node parse(Reader reader) throws IOException {
		return new TemplateParser(TagParser.parse(reader)).parse();
	}

	private Collection<Tag> tags;
	private Queue<NodeBuilder> stack =
			Collections.asLifoQueue(new ArrayDeque<NodeBuilder>());

	private TemplateParser(Collection<Tag> tags) {
		this.tags = tags;
	}

	private Node parse() {
		NodeBuilder nodeBuilder = new NodeBuilder() {

			@Override
			Node build() {
				return getBlock();
			}
		};
		stack.add(nodeBuilder);
		for (Tag tag : tags) {
			tag.build(this);
		}
		return stack.remove().build();
	}

	public void endBlock() {
		add(stack.remove().build());
	}

	void add(Node node) {
		stack.element().add(node);
	}

	public void beginBlock(NodeBuilder nodeBuilder) {
		stack.add(nodeBuilder);
	}

	public void beginElse(NodeBuilder elseNode) {
		stack.element().setElse(elseNode);
	}

}
