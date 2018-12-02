package com.catascopic.template;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class FunctionResolver {

	public static Builder builder() {
		return new Builder();
	}

	public static FunctionResolver builtinOnly() {
		return BUILTIN_ONLY;
	}

	private static final FunctionResolver BUILTIN_ONLY = builder().build();

	private final Map<String, TemplateFunction> functions;

	FunctionResolver(Map<String, TemplateFunction> functions) {
		this.functions = functions;
	}

	TemplateFunction get(String name) {
		TemplateFunction function = functions.get(name);
		if (function == null) {
			throw new TemplateEvalException("undefined function %s", name);
		}
		return function;
	}

	@Override
	public String toString() {
		return functions.keySet().toString();
	}

	public static class Builder {

		private Builder() {
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

		public Builder addFunctions(Map<String,
				? extends TemplateFunction> functionMap) {
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
