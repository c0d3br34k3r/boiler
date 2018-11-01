package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.expr.Term;
import com.google.common.base.Function;

public class Scope implements LocalAccess, Function<Term, Object> {

	private final LocalAccess parent;
	private final Path workingDir;
	private final TemplateResolver resolver;
	private Map<String, Object> values = new HashMap<>();

	Scope(TemplateResolver resolver, Path workingDir,
			Map<String, Object> params) {
		this.resolver = resolver;
		this.workingDir = workingDir;
		this.parent = BASE;
		values.putAll(params);
	}

	private Scope(TemplateResolver resolver, Path workingDir,
			LocalAccess parent) {
		this.resolver = resolver;
		this.workingDir = workingDir;
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

	public void renderTemplate(Appendable writer, String fileName,
			Assigner assigner)
			throws IOException {
		// TODO: dir could be null
		Path file = workingDir.resolve(fileName);
		Scope extended = new Scope(resolver, file.getParent(), this);
		assigner.assign(extended);
		resolver.getTemplate(file).render(writer, extended);
	}

	public void renderTextFile(Appendable writer, String fileName)
			throws IOException {
		writer.append(resolver.getTextFile(workingDir.resolve(fileName)));
	}

	private static final LocalAccess BASE = new LocalAccess() {

		@Override
		public Object get(String name) {
			throw new TemplateEvalException("%s could not be resolved", name);
		}
	};

	public String newLine() {
		return "\n";
	}

}
