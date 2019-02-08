package com.catascopic.template;

import java.util.List;

public enum NullContext implements Context {
	CONTEXT;

	@Override
	public Object get(String name) {
		throw new TemplateEvalException("identifier %s not allowed", name);
	}

	@Override
	public Object call(String functionName, List<Object> arguments) {
		throw new TemplateEvalException("invocation %s not allowed",
				functionName);
	}

}
