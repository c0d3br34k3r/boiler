package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Path;

import com.catascopic.template.parse.Node;
import com.google.common.collect.ImmutableMap;

public class TemplateEngine {

	private ParseCache<Node> templateCache;
	private ParseCache<String> textCache;
	private ImmutableMap<String, TemplateFunction> functions;

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
