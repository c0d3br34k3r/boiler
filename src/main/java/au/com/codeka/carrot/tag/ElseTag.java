package au.com.codeka.carrot.tag;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nullable;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.ValueHelper;
import au.com.codeka.carrot.expr.StatementParser;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.tmpl.Node;
import au.com.codeka.carrot.tmpl.TagNode;

/**
 * The {@link ElseTag} can be chained with {@link IfTag} or {@link ForTag} to
 * contain the "else" block. In the case if {@link IfTag}, it'll be chained to
 * if the if condition is false. In the case of {@link ForTag}, it'll be chained
 * to if there are no elements in the list to be iterated.
 *
 * <p>
 * The {@link ElseTag} can have an optional "if &lt;expr&gt;" after if, in which
 * case the tag will basically be like an {@link IfTag} that can be chained to.
 * You can then do as many
 * <code>{% if blah %} {% else if blah %}...{% end %}</code> as you like.
 */
public class ElseTag extends Tag {

	@Nullable
	private Term expr;

	@Override
	public boolean isBlockTag() {
		return true;
	}

	/**
	 * Return true if we can chain to the given next {@link Tag}. If it's
	 * another else tag then we can chain to it.
	 */
	@Override
	public boolean canChain(Tag nextTag) {
		if (expr == null) {
			return false;
		}
		return nextTag instanceof ElseTag;
	}

	@Override
	public void parseStatement(StatementParser parser) throws CarrotException {
		String ifToken = parser.tryParseIdentifier();
		if (ifToken != null) {
			if (!ifToken.equals("if")) {
				throw new CarrotException("Expected 'if' after 'else'.");
			}
			expr = parser.parseExpression();
		}
		parser.parseEnd();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {
		if (expr == null || ValueHelper.isTrue(expr.evaluate(engine.getConfig(), scope))) {
			tagNode.renderChildren(engine, writer, scope);
		} else {
			Node nextNode = tagNode.getNextNode();
			if (nextNode != null) {
				nextNode.render(engine, writer, scope);
			}
		}
	}

}
