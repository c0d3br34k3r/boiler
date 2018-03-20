package au.com.codeka.carrot.expr;

import java.util.Collections;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import au.com.codeka.carrot.Scope;

class MapTerm implements Term {

	static final Term EMPTY = new Term() {

		@Override
		public Object evaluate(Scope scope) {
			return Collections.emptyMap();
		}

		@Override
		public String toString() {
			return "{}";
		}
	};

	private final Map<String, Term> items;

	MapTerm(Map<String, Term> items) {
		this.items = items;
	}

	@Override
	public Object evaluate(final Scope scope) {
		return Maps.transformValues(items, new Function<Term, Object>() {

			@Override
			public Object apply(Term input) {
				return input.evaluate(scope);
			}
		});
	}

	@Override
	public String toString() {
		return "{" + Joiner.on(", ").withKeyValueSeparator(": ").join(items) + "}";
	}

}
