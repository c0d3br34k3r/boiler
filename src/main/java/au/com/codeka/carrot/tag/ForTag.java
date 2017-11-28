package au.com.codeka.carrot.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.ValueHelper;
import au.com.codeka.carrot.bindings.Composite;
import au.com.codeka.carrot.bindings.IterableExpansionBindings;
import au.com.codeka.carrot.bindings.LoopVarBindings;
import au.com.codeka.carrot.bindings.SingletonBindings;
import au.com.codeka.carrot.expr.Identifier;
import au.com.codeka.carrot.expr.StatementParser;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.TokenType;
import au.com.codeka.carrot.tmpl.Node;
import au.com.codeka.carrot.tmpl.TagNode;

/**
 * The "for" tag iterates through a loop and execute its block for each element.
 */
public class ForTag extends Tag {

	private List<Identifier> loopIdentifiers;
	private Term loopExpression;

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
		return (nextTag instanceof ElseTag);
	}

	@Override
	public void parseStatement(StatementParser stmtParser) throws CarrotException {
		loopIdentifiers = stmtParser.parseIdentifierList();
		stmtParser.parseToken(TokenType.IN);
		loopExpression = stmtParser.parseTerm();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException,
			IOException {

		Collection<?> objects =
				ValueHelper.iterate(loopExpression.evaluate(engine.getConfig(), scope));
		int i = 0;
		for (Object current : objects) {
			Bindings loopIdentifierBindings;
			if (loopIdentifiers.size() > 1 && current instanceof Iterable) {
				loopIdentifierBindings =
						new IterableExpansionBindings(loopIdentifiers, (Iterable<?>) current);
			} else {
				loopIdentifierBindings =
						new SingletonBindings(loopIdentifiers.get(0).evaluate(), current);
			}
			scope.push(
					new Composite(loopIdentifierBindings,
							new SingletonBindings("loop",
									new LoopVarBindings(objects.size(), i++))));
			tagNode.renderChildren(engine, writer, scope);
			scope.pop();
		}

		// If we have an else block and the collection was empty, render the
		// else instead.
		if (objects.isEmpty()) {
			Node nextNode = tagNode.getNextNode();
			if (nextNode != null) {
				nextNode.render(engine, writer, scope);
			}
		}
	}

}
