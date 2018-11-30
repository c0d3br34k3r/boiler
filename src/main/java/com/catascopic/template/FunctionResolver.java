package com.catascopic.template;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class FunctionResolver {

	private final ImmutableMap<String, TemplateFunction> functions;

	TemplateFunction get(String name) {
		TemplateFunction function = functions.get(name);
		if (function == null) {
			throw new TemplateEvalException("undefined function %s", name);
		}
		return function;
	}

	FunctionResolver(ImmutableMap<String, TemplateFunction> functions) {
		this.functions = functions;
	}

	private static final FunctionResolver BUILTIN_ONLY = builder().build();

	public static FunctionResolver builtinOnly() {
		return BUILTIN_ONLY;
	}

	static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		Builder() {
			addFunctions(Builtin.class);
		}

		private ImmutableMap.Builder<String, TemplateFunction> functions =
				ImmutableMap.builder();

		public <F extends Enum<F> & TemplateFunction> Builder addFunctions(
				Class<F> functionEnum) {
			for (F function : functionEnum.getEnumConstants()) {
				functions.put(Values.separatorToCamel(
						function.name().toLowerCase()), function);
			}
			return this;
		}

		public Builder addFunctions(Map<String, TemplateFunction> functionMap) {
			functions.putAll(functionMap);
			return this;
		}

		public Builder addFunction(String name, TemplateFunction function) {
			functions.put(name, function);
			return this;
		}

		public FunctionResolver build() {
			return new FunctionResolver(functions.build());
		}
	}

}
