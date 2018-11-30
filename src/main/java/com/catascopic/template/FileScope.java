package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

class FileScope extends Scope {

	private final Path file;
	private final TemplateEngine engine;

	FileScope(Path file, TemplateEngine engine,
			Map<String, ? extends Object> initial) {
		super(initial);
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
