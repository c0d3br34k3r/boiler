package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import com.catascopic.template.Location;
import com.catascopic.template.TemplateParseException;

public class TemplateParser {

	public static Node parse(Reader reader) throws IOException {
		return new TemplateParser(TagParser.parse(reader)).parse();
	}

	private Collection<Tag> tags;
	private Queue<BlockBuilder> stack =
			Collections.asLifoQueue(new ArrayDeque<BlockBuilder>());

	private TemplateParser(Collection<Tag> tags) {
		this.tags = tags;
	}

	private Node parse() {
		BlockBuilder nodeBuilder = new BlockBuilder() {

			@Override
			Node build() {
				return getBlock();
			}
		};
		stack.add(nodeBuilder);
		for (Tag tag : tags) {
			tag.build(this);
		}
		Node template = stack.remove().build();
		if (!stack.isEmpty()) {
			// TODO: tag location
			throw new TemplateParseException((Location) null, 
					"unclosed tag %s", stack.remove());
		}
		return template;
	}

	void endBlock() {
		add(stack.remove().build());
	}

	void add(Node node) {
		stack.element().add(node);
	}

	void beginBlock(BlockBuilder nodeBuilder) {
		stack.add(nodeBuilder);
	}

	void beginElse(NodeBuilderTag elseNode) {
		stack.element().setElse(elseNode);
	}

}
