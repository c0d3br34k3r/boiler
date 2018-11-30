package com.catascopic.template;

import java.util.Map;

class BasicScope extends Scope {

	private FunctionResolver functions;

	BasicScope(FunctionResolver functions) {
		this.functions = functions;
	}

	BasicScope(Map<String, ? extends Object> values,
			FunctionResolver functions) {
		super(values);
		this.functions = functions;
	}

	@Override
	public TemplateFunction getFunction(String name) {
		return functions.get(name);
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
