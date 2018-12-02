package com.catascopic.template.parse;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

// TODO: re-modularize TagParser, TagCleaner, and TemplateParser
public class TemplateParser {

	public static Node parse(Reader reader) throws IOException {
		return new TemplateParser().parse(TagParser.parse(reader));
	}

	private Queue<BlockBuilder> stack =
			Collections.asLifoQueue(new ArrayDeque<BlockBuilder>());

	private Node parse(Collection<Tag> tags) {
		final Builder<Node> builder = ImmutableList.builder();
		BlockBuilder nodeBuilder = new BlockBuilder() {

			@Override
			public Node buildElse(Node elseNode) {
				throw new IllegalStateException("else not allowed");
			}

			@Override
			public Node build() {
				throw new IllegalStateException("unbalanced end");
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
		BlockBuilder last = stack.remove();
		if (last != nodeBuilder) {
			throw new IllegalStateException("unclosed block");
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

	void beginElse(final BlockBuilder elseBlock) {
		final BlockBuilder ifBlock = stack.remove();
		stack.add(new BlockBuilder() {

			@Override
			public Node build() {
				return ifBlock.buildElse(elseBlock.build());
			}

			@Override
			public void add(Node node) {
				elseBlock.add(node);
			}

			@Override
			public Node buildElse(Node elseNode) {
				return ifBlock.buildElse(elseBlock.buildElse(elseNode));
			}
		});
	}

}
