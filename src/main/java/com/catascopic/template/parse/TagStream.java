package com.catascopic.template.parse;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class TagStream {

	private Iterator<Tag> iterator;

	Block parseBlock() {
		ImmutableList.Builder<Node> builder = ImmutableList.builder();
		for (;;) {
			Tag tag = iterator.next();
			if (tag.type() == Tag.Type.END) {
				return new Block(builder.build());
			} else if (tag == Tag.)
			builder.add(tag.createNode(this));
		}
	}

}
