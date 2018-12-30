package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.catascopic.template.Assigner;
import com.catascopic.template.Location;
import com.catascopic.template.Scope;
import com.catascopic.template.TemplateEvalException;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Symbol;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

// TODO: rework this, move to eval?
class Variables {

	private Variables() {}

	static NameAssigner parseNames(Tokenizer tokenizer) {
		return parseNames(tokenizer, new HashSet<String>());
	}

	private static NameAssigner parseNames(Tokenizer tokenizer,
			Set<String> unique) {
		return parseNames(tokenizer, unique, false);
	}

	private static NameAssigner parseNames(Tokenizer tokenizer,
			Set<String> unique, boolean forceUnpack) {
		Location location = tokenizer.getLocation();
		List<NameAssigner> assigners = new ArrayList<>();
		do {
			NameAssigner assigner;
			if (tokenizer.tryConsume(Symbol.LEFT_PARENTHESIS)) {
				assigner = parseNames(tokenizer, unique, true);
				tokenizer.consume(Symbol.RIGHT_PARENTHESIS);
			} else {
				String name = tokenizer.parseIdentifier();
				if (!unique.add(name)) {
					throw new TemplateParseException(tokenizer,
							"duplicate variable name: %s", name);
				}
				assigner = new SingleName(name);
			}
			assigners.add(assigner);
		} while (tokenizer.tryConsume(Symbol.COMMA));
		if (assigners.size() > 1 || forceUnpack) {
			return new Unpacker(ImmutableList.copyOf(assigners), location);
		}
		return assigners.get(0);
	}

	static Assigner parseAssignment(Tokenizer tokenizer) {
		ImmutableList.Builder<Assigner> builder = ImmutableList.builder();
		Set<String> unique = new HashSet<>();
		do {
			builder.add(parseAssigner(tokenizer, unique));
		} while (tokenizer.tryConsume(Symbol.COMMA));
		final List<Assigner> assigners = builder.build();
		if (assigners.size() == 1) {
			return assigners.get(0);
		}
		return new Assigner() {

			@Override
			public void assign(Scope scope) {
				for (Assigner assigner : assigners) {
					assigner.assign(scope);
				}
			}

			@Override
			public String toString() {
				return Joiner.on(", ").join(assigners);
			}
		};
	}

	private static Assigner parseAssigner(Tokenizer tokenizer,
			Set<String> unique) {
		final NameAssigner names = parseNames(tokenizer, unique);
		tokenizer.consume(Symbol.ASSIGNMENT);
		final Term term = tokenizer.parseExpression();
		return new Assigner() {

			@Override
			public void assign(Scope scope) {
				names.assign(scope, term.evaluate(scope));
			}

			@Override
			public String toString() {
				return names + " = " + term;
			}
		};
	}

	static final Assigner EMPTY = new Assigner() {

		@Override
		public void assign(Scope scope) {
			// do nothing
		}

		@Override
		public String toString() {
			return "empty";
		}
	};

	/**
	 * Assigns a value to a particular name, or unpacks a sequence into several
	 * names.
	 */
	interface NameAssigner {

		void assign(Scope scope, Object value);
	}

	private static class SingleName implements NameAssigner {

		private final String name;

		SingleName(String varName) {
			this.name = varName;
		}

		@Override
		public void assign(Scope scope, Object value) {
			scope.set(name, value);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static class Unpacker implements NameAssigner {

		private final List<NameAssigner> assigners;
		private final Location location;

		Unpacker(ImmutableList<NameAssigner> varNames, Location location) {
			this.assigners = varNames;
			this.location = location;
		}

		@Override
		public void assign(Scope scope, Object value) {
			Iterator<NameAssigner> iter = assigners.iterator();
			for (Object unpacked : Values.toIterable(value)) {
				if (!iter.hasNext()) {
					throw new TemplateEvalException(location,
							"too many values %s to unpack into names: %s",
							value, assigners);
				}
				iter.next().assign(scope, unpacked);
			}
			if (iter.hasNext()) {
				throw new TemplateEvalException(location,
						"not enough values %s to unpack into names: %s",
						value, assigners);
			}
		}

		@Override
		public String toString() {
			return "(" + Joiner.on(", ").join(assigners) + ")";
		}
	}

}
