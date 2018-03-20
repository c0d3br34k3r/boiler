package au.com.codeka.carrot.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.LoopBindings;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.Composite;
import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Tokenizer;
import au.com.codeka.carrot.expr.Values;
import au.com.codeka.carrot.tmpl.Node;
import au.com.codeka.carrot.tmpl.TagNode;

/**
 * The "for" tag iterates through a loop and execute its block for each element.
 */
public class ForTag extends Tag {

	private String loopIdentifier;
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
	public void parseStatement(Tokenizer parser) throws CarrotException {
		loopIdentifier = parser.parseIdentifier();
		parser.consumeIdentifier("in");
		
		loopExpression = parser.parseExpression();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {
		Collection<?> items =
				Values.toCollection(loopExpression.evaluate(engine.getConfig(), scope));
		int i = 0;
		for (Object item : items) {
			// make bindings mutable?
			scope.push(new Composite(
					new MapBindings(loopIdentifier, item),
					new MapBindings("loop", new LoopBindings(items.size(), i++))));
			tagNode.render(engine, writer, scope);
			scope.pop();
		}
		// If we have an else block and the collection was empty, render the
		// else instead.
		if (items.isEmpty()) {
			Node nextNode = tagNode.getNextNode();
			if (nextNode != null) {
				nextNode.render(engine, writer, scope);
			}
		}
	}

}
