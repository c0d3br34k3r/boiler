package com.catascopic.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.eval.Term;
import com.google.common.base.Function;

public abstract class Scope extends LocalAccess implements
		Function<Term, Object> {

	private final LocalAccess parent;
	private Map<String, Object> values = new HashMap<>();

	Scope(LocalAccess parent) {
		this.parent = parent;
	}

	Scope() {
		this(BASE);
	}

	Scope(Map<String, ? extends Object> initial) {
		this();
		values.putAll(initial);
	}

	@Override
	public Object get(String name) {
		Object value = values.get(name);
		// TODO: null masking
		if (value == null) {
			if (!values.containsKey(name)) {
				return null;
			}
			return parent.get(name);
		}
		return value;
	}

	@Override
	protected void collect(Map<String, Object> locals) {
		parent.collect(locals);
		locals.putAll(values);
	}

	public void set(String name, Object value) {
		values.put(name, value);
	}

	public Map<String, Object> locals() {
		Map<String, Object> locals = new HashMap<>();
		collect(locals);
		return locals;
	}

	@Override
	public Object apply(Term input) {
		return input.evaluate(this);
	}

	public abstract TemplateFunction getFunction(String name);

	public abstract void renderTemplate(Appendable writer, String path,
			Assigner assigner) throws IOException;

	public abstract void renderTextFile(Appendable writer, String path)
			throws IOException;

	private static final LocalAccess BASE = new LocalAccess() {

		@Override
		public Object get(String name) {
			throw new TemplateEvalException("%s is undefined", name);
		}

		@Override
		protected void collect(Map<String, Object> locals) {
			// nothing to collect
		}
	};

}
