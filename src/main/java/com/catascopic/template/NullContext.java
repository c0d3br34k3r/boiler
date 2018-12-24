package com.catascopic.template;

import java.util.List;

import com.catascopic.template.eval.Term;

public enum NullContext implements Context {
	CONTEXT;

	@Override
	public Object apply(Term term) {
		return term.evaluate(CONTEXT);
	}

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
