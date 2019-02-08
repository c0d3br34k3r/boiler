package com.catascopic.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scope implements Context {

	// package-private
	Map<String, Object> values = new HashMap<>();

	Scope(Map<String, ? extends Object> initial) {
		values.putAll(initial);
	}

	Scope() {}

	@Override
	public final Object get(String name) {
		Object value = values.get(name);
		if (value == null) {
			if (values.containsKey(name)) {
				return null;
			}
			return getAlt(name);
		}
		return value;
	}

	abstract Object getAlt(String name);

	public final void set(String name, Object value) {
		values.put(name, value);
	}

	public abstract Map<String, Object> locals();

	@Override
	public Object call(String functionName, List<Object> arguments) {
		return getFunction(functionName).apply(new Params(arguments, this));
	}

	abstract TemplateFunction getFunction(String name);

	public abstract void renderTemplate(Appendable writer, String path,
			Assigner assigner) throws IOException;

	public abstract void renderTextFile(Appendable writer, String path)
			throws IOException;

	public void print(Location location, String message) {
		System.out.println(message);
	}

}
