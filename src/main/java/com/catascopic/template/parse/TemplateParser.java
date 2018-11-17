package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import com.catascopic.template.Location;
import com.catascopic.template.TemplateParseException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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
		final Builder<Node> builder = ImmutableList.builder();
		BlockBuilder nodeBuilder = new BlockBuilder() {

			@Override
			public void setElse(BlockBuilder builder) {
				throw new IllegalStateException();
			}

			@Override
			public Node build() {
				throw new IllegalStateException();
			}

			@Override
			public void add(Node node) {
				builder.add(node);
			}
		};
		stack.add(nodeBuilder);
		for (Tag tag : tags) {
			tag.handle(this);
		}
		stack.remove();
		if (!stack.isEmpty()) {
			// TODO: tag location
			throw new TemplateParseException((Location) null,
					"unclosed tag %s", stack.remove());
		}
		return new Block(builder.build());
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

	void beginElse(BlockBuilder elseNode) {
		stack.element().setElse(elseNode);
	}

}
