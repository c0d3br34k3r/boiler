package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * A {@link Scope} with a working directory that can resolve file paths. A
 * FileScope can also have a parent if it was created by another FileScope
 * (i.e., when a template calls another template). A FileScope resolves variable
 * references recursively along its ancestry.
 */
class FileScope extends Scope {

	private final Path file;
	private final TemplateEngine engine;

	FileScope(Path file, TemplateEngine engine, LocalAccess initial) {
		super(initial);
		this.file = file;
		this.engine = engine;
	}

	FileScope(Path file, TemplateEngine engine, Map<String, ?> initial) {
		super(initial);
		this.file = file;
		this.engine = engine;
	}

	private FileScope(Path file, FileScope parent) {
		super(parent);
		this.file = file;
		this.engine = parent.engine;
	}

	@Override
	public TemplateFunction getFunction(String name) {
		return engine.settings().getFunction(name);
	}

	@Override
	public void print(Location location, String message) throws IOException {
		engine.settings().print(location, message);
	}

	@Override
	public String renderTemplate(String path, Map<String, ?> params) {
		Path resolvedFile = file.resolveSibling(path);
		Scope extended = new FileScope(resolvedFile, this);
		extended.setAll(params);
		StringBuilder builder = new StringBuilder();
		try {
			engine.getTemplate(resolvedFile).render(builder, extended);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return builder.toString();
	}

	@Override
	public String renderTextFile(String path) {
		return engine.getTextFile(file.resolveSibling(path));
	}

}
