package com.catascopic.template.parse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.catascopic.template.Assigner;
import com.catascopic.template.TemplateEvalException;
import com.catascopic.template.Scope;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Symbol;
import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

class Variables {

	private Variables() {}

	static Names parseNames(Tokenizer tokenizer) {
		return parseNames(tokenizer, new HashSet<String>());
	}

	private static Names parseNames(Tokenizer tokenizer, Set<String> unique) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		do {
			String name = tokenizer.parseIdentifier();
			if (!unique.add(name)) {
				throw new TemplateParseException(tokenizer,
						"duplicate variable name: %s", name);
			}
			builder.add(name);
		} while (tokenizer.tryConsume(Symbol.COMMA));
		List<String> varNames = builder.build();
		if (varNames.size() == 1) {
			return new Name(varNames.get(0));
		}
		return new UnpackNames(varNames);
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
		final Names names = parseNames(tokenizer, unique);
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
			this.varNames = varNames;
		}

		@Override
		public void assign(Scope scope, Object value) {
			Iterator<String> iter = varNames.iterator();
			for (Object unpacked : Values.toIterable(value)) {
				if (!iter.hasNext()) {
					throw new TemplateEvalException(
							"too many values to unpack into names: %s", varNames);
				}
				scope.set(iter.next(), unpacked);
			}
			if (iter.hasNext()) {
				throw new TemplateEvalException(
						"not enough values to unpack into names: %s", varNames);
			}
		}

		@Override
		public String toString() {
			return Joiner.on(", ").join(varNames);
		}
	}

}
