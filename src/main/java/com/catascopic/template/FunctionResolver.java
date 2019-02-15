package com.catascopic.template;

import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.value.Values;
import com.google.common.collect.ImmutableMap;

// TODO: make this a more general settings class
public class FunctionResolver {

	public static Builder builder() {
		return new Builder();
	}

	public static FunctionResolver builtInOnly() {
		return BUILT_IN_ONLY;
	}

	private static final FunctionResolver BUILT_IN_ONLY = builder().build();

	private final Map<String, TemplateFunction> functions;

	FunctionResolver(Map<String, TemplateFunction> functions) {
		this.functions = functions;
	}

	TemplateFunction get(String name) {
		TemplateFunction function = functions.get(name);
		if (function == null) {
			throw new TemplateRenderException("undefined function %s", name);
		}
		return function;
	}

	@Override
	public String toString() {
		return functions.keySet().toString();
	}

	public static class Builder {

		private Builder() {
			addFunctions(BuiltIn.class);
		}

		// Use HashMap so functions can be replaced
		private Map<String, TemplateFunction> functions = new HashMap<>();

		public <F extends Enum<F> & TemplateFunction> Builder addFunctions(
				Class<F> functionEnum) {
			for (F function : functionEnum.getEnumConstants()) {
				functions.put(Values.separatorToCamel(
						function.name().toLowerCase()), function);
			}
			return this;
		}

		public Builder addFunctions(
				Map<String, ? extends TemplateFunction> functionMap) {
			functions.putAll(functionMap);
			return this;
		}

		public Builder addFunction(String name, TemplateFunction function) {
			functions.put(name, function);
			return this;
		}

		public FunctionResolver build() {
			return new FunctionResolver(ImmutableMap.copyOf(functions));
		}
	}

}
