package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class FileScope extends Scope implements LocalAccess {

	private final Path file;
	private final TemplateEngine engine;
	private final LocalAccess parent;

	FileScope(Path file, TemplateEngine engine,
			Map<String, ? extends Object> initial) {
		super(initial);
		this.file = file;
		this.engine = engine;
		this.parent = BASE;
	}

	private FileScope(Path file, FileScope parent) {
		this.file = file;
		this.engine = parent.engine;
		this.parent = parent;
	}

	@Override
	Object getAlt(String name) {
		return parent.get(name);
	}

	@Override
	public Map<String, Object> locals() {
		Map<String, Object> collected = new HashMap<>();
		collectLocals(collected);
		return collected;
	}

	@Override
	public void collectLocals(Map<String, Object> collected) {
		parent.collectLocals(collected);
		collected.putAll(locals);
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
		try {
			engine.getTemplate(resolvedFile).render(writer, extended);
		} catch (TemplateEvalException e) {
			e.setResource(resolvedFile);
			throw e;
		}
	}

	@Override
	public void renderTextFile(Appendable writer, String path)
			throws IOException {
		writer.append(engine.getTextFile(file.resolveSibling(path)));
	}

	private static final LocalAccess BASE = new LocalAccess() {

		@Override
		public Object get(String name) {
			throw new TemplateEvalException("%s is undefined", name);
		}

		@Override
		public void collectLocals(Map<String, Object> locals) {
			// nothing to add
		}
	};

}
