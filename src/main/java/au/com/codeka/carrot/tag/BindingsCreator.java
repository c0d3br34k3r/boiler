package au.com.codeka.carrot.tag;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.expr.Term;

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
