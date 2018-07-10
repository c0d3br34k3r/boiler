package com.catascopic.template.parse;

import java.util.Iterator;
import java.util.List;

import com.catascopic.template.Scope;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.expr.Symbol;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.catascopic.template.expr.Values;
import com.google.common.collect.ImmutableList;

class Variables {

	private Variables() {}

	static Names parseNames(Tokenizer tokenizer) {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		do {
			builder.add(tokenizer.parseIdentifier());
		} while (tokenizer.tryConsume(Symbol.COMMA));
		final List<String> varNames = builder.build();

		if (varNames.size() == 1) {
			final String varName = varNames.get(0);
			return new Names() {

				@Override
				public void assign(Scope scope, Object value) {
					scope.set(varName, value);
				}
			};
		}
		// TODO: allow duplicate variable names in one set statement?
		return new Names() {

			@Override
			public void assign(Scope scope, Object value) {
				unpack(varNames, scope, value);
			}
		};
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
		};
	}

	static final Assigner EMPTY = new Assigner() {

		@Override
		public void assign(Scope scope) {
			// do nothing
		}
	};

	interface Assigner {

		void assign(Scope scope);
	}

	interface Names {

		void assign(Scope scope, Object value);
	}

}
