package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.catascopic.template.parse.Node;

class CachedTemplate extends Template implements Resolver {

	private Node node;
	private Path workingDir;
	private TemplateEngine engine;

	@Override
	public void render(Appendable writer, Map<String, Object> params)
			throws IOException {
		node.render(writer, new Scope(this, params));
	}

	@Override
	public void renderTemplate(String path, Appendable writer, Scope scope,
			Assigner assigner) throws IOException {
		Path file = workingDir.resolve(path);
		engine.getTemplate(file).render(writer, new Scope(scope, extend(file
				.getParent()), assigner));
	}

	private Resolver extend(Path parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void renderTextFile(String path, Appendable writer)
			throws IOException {
		writer.append(engine.getTextFile(workingDir.resolve(path)));
	}

	@Override
	public TemplateFunction getFunction(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
