package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;
import au.com.codeka.carrot.expr.Tokenizer;
import au.com.codeka.carrot.tag.Tag;

/**
 * The "include" tag is used to include the contents of another template.
 *
 * <p>
 * Using the include tag is very simple:
 *
 * <pre>
 * <code>
 * {% include "foo.html" %}
 * </code>
 * </pre>
 */
public class IncludeTag extends Tag {

	private final Term templateName;

	IncludeTag(Tokenizer tokenizer) throws CarrotException {
		templateName = tokenizer.parseExpression();
	}

	@Override
	public void render(CarrotEngine engine, Writer writer, TagNode tagNode, Scope scope)
			throws CarrotException, IOException {
		Path template = Paths.get(templateName.evaluate(engine.getConfig(), scope).toString());
		engine.getConfig().getResourceLocator().findResource(null, templateName);
		engine.process(writer, resourceName, scope);
	}

}
