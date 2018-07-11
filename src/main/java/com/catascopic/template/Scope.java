package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.expr.Term;
import com.google.common.base.Function;

public class Scope implements Resolver, Function<Term, Object> {

	private static final Resolver BASE = new Resolver() {

		@Override
		public Object get(String name) {
			throw new TemplateParseException("%s cannot be resolved", name);
		}
	};

	private final Resolver parent;
	private final Path dir;
	private final TemplateEngine engine;
	private Map<String, Object> values = new HashMap<>();

	static Scope create(TemplateEngine engine, Path dir) {
		return new Scope(engine, dir, BASE);
	}

	static Scope create(TemplateEngine engine, 
			Path dir,
			Map<String, Object> values) {
		Scope scope = create(engine, dir);
		scope.values.putAll(values);
		return scope;
	}

	private Scope(TemplateEngine engine, Path dir, Resolver parent) {
		this.engine = engine;
		this.dir = dir;
		this.parent = parent;
	}

	@Override
	public Object get(String name) {
		Object value = values.get(name);
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
		engine.getTemplate(fileName).render(writer, this);
	}

	public void renderTextFile(Appendable writer, String fileName)
			throws IOException {
		engine.getTextFile(fileName).render(writer, this);
	}

	public Scope extend() {
		return new Scope(engine, dir, this);
	}

}
