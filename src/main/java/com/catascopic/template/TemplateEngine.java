package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.catascopic.template.parse.Node;
import com.google.common.collect.ImmutableMap;

public class TemplateEngine {

	private ParseCache<Node> templateCache;
	private ParseCache<String> textCache;
	private ImmutableMap<String, TemplateFunction> functions;

	public void render(Path path, Appendable writer,
			Map<String, ? extends Object> params) throws IOException {
		templateCache.get(path).render(writer,
				new FileScope(path, this, params));
	}

	public String render(Path path, Map<String, Object> params) {
		StringBuilder builder = new StringBuilder();
		try {
			render(path, builder, params);
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return builder.toString();
	}

	Node getTemplate(Path file) throws IOException {
		return templateCache.get(file);
	}

	String getTextFile(Path file) throws IOException {
		return textCache.get(file);
	}

	TemplateFunction getFunction(String name) {
		return functions.get(name);
	}

}
