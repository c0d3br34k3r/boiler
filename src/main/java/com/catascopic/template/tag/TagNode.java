package com.catascopic.template.tag;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import javax.annotation.Nullable;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Configuration;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.StatementParser;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.tag2.EndTag;
import com.catascopic.template.tag2.Tag;
import com.catascopic.template.tmpl.parse.Segment;

/**
 * A {@link TagNode} represents a node of the form "{% tagname foo %}" where
 * "tagname" is the name of the tag and "foo" is the parameters.
 * <p>
 * Tags are represented by the {@link Tag} class, which is extensible.
 */
public class TagNode extends Node {

	private final Tag tag;

	public TagNode(Tag tag) {
		this.tag = tag;
	}

	@Override
	boolean canLink(Node nextNode) {
		if (nextNode instanceof TagNode) {
			Tag nextTag = ((TagNode) nextNode).tag;
			return tag.canChain(nextTag);
		}
		return false;
	}

	/**
	 * @return True if this is an end block (that is, if it ends its parent's
	 *         block).
	 */
	public boolean isEndBlock() {
		return tag instanceof EndTag;
	}

	/**
	 * @return The {@link Tag} for this node.
	 */
	public Tag getTag() {
		return tag;
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, Scope scope)
			throws CarrotException, IOException {
		tag.render(engine, writer, this, scope);
	}

}
