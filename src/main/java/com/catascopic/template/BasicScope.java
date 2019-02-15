package com.catascopic.template;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

class BasicScope extends Scope {

	private FunctionResolver functions;

	BasicScope(FunctionResolver functions) {
		this.functions = functions;
	}

	BasicScope(Map<String, ? extends Object> values, FunctionResolver functions) {
		super(values);
		this.functions = functions;
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
		return functions.get(name);
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
