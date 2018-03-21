package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.tag2.Tag;

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
