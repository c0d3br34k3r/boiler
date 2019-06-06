package com.catascopic.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.value.Values;
import com.google.common.collect.ImmutableMap;

public class Settings {

	public static Builder builder() {
		return new Builder();
	}

	public static final Settings DEFAULT = builder().build();

	private final Map<String, TemplateFunction> functions;
	private final Debugger debugger;

	Settings(Map<String, TemplateFunction> functions, Debugger debugger) {
		this.functions = functions;
		this.debugger = debugger;
	}

	TemplateFunction getFunction(String name) {
		TemplateFunction function = functions.get(name);
		if (function == null) {
			throw new TemplateRenderException("undefined function %s", name);
		}
		return function;
	}

	void print(Location location, String message) throws IOException {
		debugger.print(location, message);
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
		private Debugger debugger = Debuggers.STANDARD_OUTPUT;

		public <F extends Enum<F> & TemplateFunction> Builder addFunctions(Class<F> functionEnum) {
			for (F function : functionEnum.getEnumConstants()) {
				functions.put(Values.separatorToCamel(function.name().toLowerCase()), function);
			}
			return this;
		}

		public Builder addFunctions(Map<String, ? extends TemplateFunction> functionMap) {
			functions.putAll(functionMap);
			return this;
		}

		public Builder addFunction(String name, TemplateFunction function) {
			functions.put(name, function);
			return this;
		}

		public Builder setDebugger(Debugger debugger) {
			this.debugger = debugger;
			return this;
		}

		public Settings build() {
			return new Settings(ImmutableMap.copyOf(functions), debugger);
		}
	}

}
