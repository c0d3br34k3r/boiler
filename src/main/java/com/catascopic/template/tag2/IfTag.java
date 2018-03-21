package com.catascopic.template.tag2;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.StatementParser;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Values;
import com.catascopic.template.tag.Node;
import com.catascopic.template.tag.TagNode;

/**
 * The "if" tag evaluates it's single parameter and outputs it's children if
 * true. It can be chained with zero or more ElseifTags and zero or one
 * ElseTags.
 */
public class IfTag extends Tag {

	private Term expr;

	@Override
	public boolean isBlockTag() {
		return true;
	}

	/**
	 * Return true if we can chain to the given next {@link Tag}. If it's an
	 * else tag then we can chain to it.
	 */
	@Override
	public boolean canChain(Tag nextTag) {
		return nextTag instanceof ElseTag;
	}

	@Override
	public void parseStatement(StatementParser stmtParser) throws CarrotException {
		expr = stmtParser.parseExpression();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {
		Object value = expr.evaluate(engine.getConfig(), scope);
		if (Values.isTrue(value)) {
			tagNode.renderChildren(engine, writer, scope);
		} else {
			Node nextNode = tagNode.getNextNode();
			if (nextNode != null) {
				nextNode.render(engine, writer, scope);
			}
		}
	}

}
