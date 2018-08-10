package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.expr.Term;
import com.catascopic.template.parse.Node;
import com.google.common.base.Function;

public class Scope implements Resolver, Function<Term, Object> {

	private final Resolver parent;
	private final Path dir;
	private final TemplateEngine engine;
	private Map<String, Object> values = new HashMap<>();

	Scope(TemplateEngine engine, Path dir, Resolver parent) {
		this.engine = engine;
		this.dir = dir;
		this.parent = parent;
	}

	@Override
	public Object get(String name) {
		Object value = values.get(name);
		// TODO: null masking
		if (value == null) {
			return parent.get(name);
		}
		return value;
	}

	public void set(String name, Object value) {
		values.put(name, value);
	}

	public TemplateFunction getFunction(String name) {
		return engine.getFunction(name);
	}

	@Override
	public Object apply(Term input) {
		return input.evaluate(this);
	}

	public void renderTemplate(Appendable writer, String fileName)
			throws IOException {
		Path file = dir.resolve(fileName);
		getDocument(file, true).render(writer,
				new Scope(engine, file.getParent(), this));
	}

	public void renderTextFile(Appendable writer, String fileName)
			throws IOException {
		getDocument(dir.resolve(fileName), false).render(writer, null);
	}

	private Node getDocument(Path file, boolean template)
			throws IOException {
		return engine.cache().getDocument(file, template);
	}

}
