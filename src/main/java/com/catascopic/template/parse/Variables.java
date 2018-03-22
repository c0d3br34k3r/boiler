package com.catascopic.template.parse;

import java.util.ArrayList;
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

	static List<String> parseNames(Tokenizer tokenizer) {
		List<String> varNames = new ArrayList<>();
		do {
			varNames.add(tokenizer.parseIdentifier());
		} while (tokenizer.tryConsume(Symbol.COMMA));
		return varNames;
	}

	static void unpack(Scope scope, List<String> varNames, Object unpack) {
		Iterator<String> iter = varNames.iterator();
		for (Object unpacked : Values.toIterable(unpack)) {
			if (!iter.hasNext()) {
				throw new TemplateParseException(
						"too many values to unpack");
			}
			scope.set(iter.next(), unpacked);
		}
		if (iter.hasNext()) {
			throw new TemplateParseException(
					"not enough values to unpack");
		}
	}

	static Assigner parse(Tokenizer tokenizer) {
		ImmutableList.Builder<Assigner> builder = ImmutableList.builder();
		do {
			builder.add(parseAssigner(tokenizer));
		} while (tokenizer.tryConsume(Symbol.COMMA));
		final ImmutableList<Assigner> assigners = builder.build();
		return new Assigner() {

			@Override
			public void assign(Scope scope) {
				for (Assigner assigner : assigners) {
					assigner.assign(scope);
				}
			}
		};
	}

	static final Assigner EMPTY_ASSIGNER = new Assigner() {

		@Override
		public void assign(Scope scope) {
			// do nothing
		}
	};

	private final List<Assigner> assigners;

	private Variables(List<Assigner> assigners) {
		this.assigners = assigners;
	}

	void assign(Scope scope) {
		for (Assigner assigner : assigners) {
			assigner.assign(scope);
		}
	}

	private static Assigner parseAssigner(Tokenizer tokenizer) {
		final List<String> varNames = parseNames(tokenizer);
		tokenizer.consume(Symbol.ASSIGNMENT);
		final Term term = tokenizer.parseExpression();
		if (varNames.size() == 1) {
			final String varName = varNames.get(0);
			return new Assigner() {

				@Override
				public void assign(Scope scope) {
					scope.set(varName, term.evaluate(scope));
				}
			};
		}
		return new Assigner() {

			@Override
			public void assign(Scope scope) {
				unpack(scope, varNames, term.evaluate(scope));
			}
		};
	}

	interface Assigner {

		void assign(Scope scope);
	}

}
