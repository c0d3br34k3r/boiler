package com.catascopic.template;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

class BasicScope extends Scope {

	private Settings settings;

	BasicScope(Settings functions) {
		this.settings = functions;
	}

	BasicScope(Map<String, ? extends Object> values, Settings settings) {
		super(values);
		this.settings = settings;
	}

	@Override
	Object getAlt(String name) {
		throw new TemplateRenderException("%s is undefined", name);
	}

	@Override
	public Map<String, Object> locals() {
		return ImmutableMap.copyOf(locals);
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
	public void renderTemplate(Appendable writer, String path, Assigner assigner) {
		throw new TemplateRenderException("file resolution not allowed");
	}

	@Override
	public void renderTextFile(Appendable writer, String path) {
		throw new TemplateRenderException("file resolution not allowed");
	}
}
