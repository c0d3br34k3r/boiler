package com.catascopic.template.tag2;

import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Bindings;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Configuration;
import com.catascopic.template.Scope;
import com.catascopic.template.bindings.MapBindings;
import com.catascopic.template.expr.Term;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

class BindingCreator {

	private List<BindingEntry> entries = new ArrayList<>();

	void add(String name, Term value) {
		entries.add(new BindingEntry(name, value));
	}

	Bindings create(Configuration config, Scope scope) throws CarrotException {
		Builder<String, Object> builder = ImmutableMap.builder();
		for (BindingEntry entry : entries) {
			builder.put(entry.name, entry.value.evaluate(config, scope));
		}
		return new MapBindings(builder.build());
	}

	private static class BindingEntry {
		private final String name;
		private final Term value;

		BindingEntry(String name, Term value) {
			this.name = name;
			this.value = value;
		}
	}

}
