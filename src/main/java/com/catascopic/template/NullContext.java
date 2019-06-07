package com.catascopic.template;

import java.util.List;

/**
 * A context where no references are allowed.
 */
public enum NullContext implements Context {

	/**
	 * The singleton {@link NullContext}.
	 */
	CONTEXT;

	@Override
	public Object get(String name) {
		throw new TemplateRenderException("identifier %s not allowed", name);
	}

	@Override
	public Object call(String functionName, List<Object> arguments) {
		throw new TemplateRenderException("invocation %s not allowed", functionName);
	}

}
