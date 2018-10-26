package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.expr.Term;
import com.google.common.base.Function;

public class Scope implements Locals, Function<Term, Object> {

	private final Locals parent;
	private final Path dir;
	private final TemplateResolver resolver;
	private Map<String, Object> values = new HashMap<>();

	Scope(TemplateResolver resolver, Path dir, Map<String, Object> params) {
		this.resolver = resolver;
		this.dir = dir;
		this.parent = BASE;
		values.putAll(params);
	}

	private Scope(TemplateResolver resolver, Path dir, Locals parent) {
		this.resolver = resolver;
		this.dir = dir;
		this.parent = parent;
	}

	@Override
	public Object get(String name) {
		Object value = values.get(name);
		// TODO: null masking
		if (value == null) {
			if (values.containsKey(name)) {
				return null;
			}
			return parent.get(name);
		}
		return value;
	}

	public void set(String name, Object value) {
		values.put(name, value);
	}

	public TemplateFunction getFunction(String name) {
		return resolver.getFunction(name);
	}

	@Override
	public Object apply(Term input) {
		return input.evaluate(this);
	}

	public void renderTemplate(Appendable writer, String fileName)
			throws IOException {
		// TODO: dir could be null
		Path file = dir.resolve(fileName);
		resolver.getTemplate(file).render(writer,
				new Scope(resolver, file.getParent(), this));
	}

	public void renderTextFile(Appendable writer, String fileName)
			throws IOException {
		writer.append(resolver.getTextFile(dir.resolve(fileName)));
	}

	private static final Locals BASE = new Locals() {

		@Override
		public Object get(String name) {
			throw new TemplateEvalException("%s could not be resolved", name);
		}
	};

	public String newLine() {
		return "\n";
	}

}
