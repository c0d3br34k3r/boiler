package com.catascopic.template;

import java.nio.charset.Charset;
import java.util.Map;

import com.catascopic.template.expr.Values;

public class TemplateEngine {

	private final Bindings globalBindings;
	private final ParseCache cache;
	private final Charset charset;
	private final Map<String, TemplateFunction> functions;

	public TemplateEngine(Bindings globalBindings, Charset charset) {
		test(Builtin.class);
	}

	<F extends Enum<F> & TemplateFunction> void test(Class<F> funcs) {
		for (F function : funcs.getEnumConstants()) {
			functions.put(Values.separatorToCamel(function.name()), function);
		}
	}

}
