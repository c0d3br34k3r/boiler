package com.catascopic.template.parse;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.catascopic.template.Location;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.TrackingReader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TemplateParser {

	private TemplateParser() {}

	public static Node parse(TrackingReader reader) throws IOException {
		Location location = reader.getLocation();
		return new TemplateParser().parse(TagParser.parse(reader), location);
	}

	private Queue<BlockBuilder> stack = Collections.asLifoQueue(new ArrayDeque<BlockBuilder>());

	private Node parse(List<Tag> tags, final Location location) {
		final Builder<Node> builder = ImmutableList.builder();
		BlockBuilder nodeBuilder = new BlockBuilder(location) {

			@Override
			protected Node build() {
				throw new TemplateParseException(location, "unbalanced end");
			}

			@Override
			protected void add(Node node) {
				builder.add(node);
			}

			@Override
			public String toString() {
				return "outer template block";
			}
		};
		stack.add(nodeBuilder);
		for (Tag tag : tags) {
			tag.handle(this);
		}
		BlockBuilder last = stack.remove();
		if (last != nodeBuilder) {
			throw new TemplateParseException(location, "unclosed block: " + last);
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

	void beginElse(final NodeBuilder elseBlock) {
		final BlockBuilder ifBlock = stack.remove();
		ifBlock.checkElse(elseBlock);
		stack.add(new BlockBuilder(elseBlock) {

			@Override
			protected Node build() {
				return ifBlock.buildElse(elseBlock.build());
			}

			@Override
			protected void add(Node node) {
				elseBlock.add(node);
			}

			@Override
			protected Node buildElse(Node elseNode) {
				return ifBlock.buildElse(elseBlock.buildElse(elseNode));
			}

			@Override
			protected void checkElse(BlockBuilder elseBuilder) {
				elseBlock.checkElse(elseBuilder);
			}

			@Override
			public String toString() {
				return elseBlock.toString();
			}
		});
	}

}
