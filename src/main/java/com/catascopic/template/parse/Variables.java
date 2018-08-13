package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.catascopic.template.Scope;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.expr.Symbol;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.expr.Values;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

class Variables {

	private Variables() {}

	static Names parseNames(Tokenizer tokenizer) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		do {
			builder.add(tokenizer.parseIdentifier());
		} while (tokenizer.tryConsume(Symbol.COMMA));
		List<String> varNames = builder.build();
		if (varNames.size() == 1) {
			return new Name(varNames.get(0));
		}
		return new UnpackNames(varNames);
	}

	private static void unpack(List<String> varNames, Scope scope,
			Object value) {
		Iterator<String> iter = varNames.iterator();
		for (Object unpacked : Values.toIterable(value)) {
			if (!iter.hasNext()) {
				throw new TemplateParseException("too many values to unpack");
			}
			scope.set(iter.next(), unpacked);
		}
		if (iter.hasNext()) {
			throw new TemplateParseException("not enough values to unpack");
		}
	}

	static Assigner parseAssignment(Tokenizer tokenizer) {
		ImmutableList.Builder<Assigner> builder = ImmutableList.builder();
		do {
			builder.add(parseAssigner(tokenizer));
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

	private static Assigner parseAssigner(Tokenizer tokenizer) {
		final Names names = parseNames(tokenizer);
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

	interface Assigner {

		void assign(Scope scope);
	}

	interface Names {

		void assign(Scope scope, Object value);
	}

	private static class Name implements Names {

		private final String varName;

		Name(String varName) {
			this.varName = varName;
		}

		@Override
		public void assign(Scope scope, Object value) {
			scope.set(varName, value);
		}

		@Override
		public String toString() {
			return varName;
		}
	}

	private static class UnpackNames implements Names {

		private final List<String> varNames;

		UnpackNames(List<String> varNames) {
			Set<String> unique = new HashSet<>();
			List<String> duplicates = new ArrayList<>();
			for (String name : varNames) {
				if (!unique.add(name)) {
					duplicates.add(name);
				}
			}
			if (!duplicates.isEmpty()) {
				throw new TemplateParseException("duplicate variable names: %s",
						Joiner.on(", ").join(duplicates));
			}
			this.varNames = varNames;
		}

		@Override
		public void assign(Scope scope, Object value) {
			unpack(varNames, scope, value);
		}

		@Override
		public String toString() {
			return Joiner.on(", ").join(varNames);
		}
	}

}
