package com.catascopic.template;

import java.io.IOException;
import java.util.Map;

/**
 * Scope implementation that has no working directory and cannot resolve file
 * paths.
 */
class BasicScope extends Scope {

	private Settings settings;

	BasicScope(LocalAccess params, Settings settings) {
		super(params);
		this.settings = settings;
	}

	BasicScope(Map<String, ?> params, Settings settings) {
		super(params);
		this.settings = settings;
	}

	@Override
	public TemplateFunction getFunction(String name) {
		return settings.getFunction(name);
	}

	@Override
	public void print(Location location, String message) throws IOException {
		settings.print(location, message);
	}

	@Override
	public String renderTemplate(String path, Map<String, ?> params) {
		throw new TemplateRenderException("file resolution not allowed");
	}

	@Override
	public String renderTextFile(String path) {
		throw new TemplateRenderException("file resolution not allowed");
	}
}
