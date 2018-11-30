package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.eval.Term;
import com.google.common.base.Function;

public abstract class Scope extends LocalAccess implements
		Function<Term, Object> {

	static Scope create(Path file, TemplateEngine engine) {
		return new FileScope(file, engine);
	}
	
	static Scope create(Path file, TemplateEngine engine) {
		return new FileScope(file, engine);
	}

	private final LocalAccess parent;
	private Map<String, Object> values = new HashMap<>();

	Scope(LocalAccess parent) {
		this.parent = parent;
	}

	Scope() {
		this(BASE);
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

	@Override
	protected void collect(Map<String, Object> locals) {
		parent.collect(locals);
		locals.putAll(values);
	}

	public void set(String name, Object value) {
		values.put(name, value);
	}

	public Map<String, Object> locals() {
		Map<String, Object> locals = new HashMap<>();
		collect(locals);
		return locals;
	}

	@Override
	public Object apply(Term input) {
		return input.evaluate(this);
	}

	public abstract TemplateFunction getFunction(String name);

	public abstract void renderTemplate(Appendable writer, String path,
			Assigner assigner) throws IOException;

	public abstract void renderTextFile(Appendable writer, String path)
			throws IOException;

	private static final LocalAccess BASE = new LocalAccess() {

		@Override
		public Object get(String name) {
			throw new TemplateEvalException("%s is undefined", name);
		}

		@Override
		protected void collect(Map<String, Object> locals) {
			// nothing to collect
		}
	};

	private static class FileScope extends Scope {

		final Path file;
		final TemplateEngine engine;

		FileScope(Path file, TemplateEngine engine) {
			this.file = file;
			this.engine = engine;
		}

		FileScope(Path file, FileScope parent) {
			super(parent);
			this.file = file;
			this.engine = parent.engine;
		}

		@Override
		public TemplateFunction getFunction(String name) {
			return engine.getFunction(name);
		}

		@Override
		public void renderTemplate(Appendable writer, String path,
				Assigner assigner) throws IOException {
			Path resolvedFile = file.resolveSibling(path);
			Scope extended = new FileScope(resolvedFile, this);
			assigner.assign(extended);
			engine.getTemplate(resolvedFile).render(writer, extended);
		}

		@Override
		public void renderTextFile(Appendable writer, String path)
				throws IOException {
			writer.append(engine.getTextFile(file.resolveSibling(path)));
		}
	}

	private static class BasicScope extends Scope {

		BasicScope() {}

		BasicScope(LocalAccess parent) {
			super(parent);
		}

		@Override
		public TemplateFunction getFunction(String name) {
			return engine.getFunction(name);
		}

		@Override
		public void renderTemplate(Appendable writer, String path,
				Assigner assigner) {
			throw new TemplateEvalException("file resolution not allowed");
		}

		@Override
		public void renderTextFile(Appendable writer, String path) {
			throw new TemplateEvalException("file resolution not allowed");
		}
	}

}
