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

abstract class Assignment {

	public static Assignment parse(Tokenizer tokenizer) {
		final List<String> varNames = parseVarNames(tokenizer);
		tokenizer.consume(Symbol.ASSIGNMENT);
		final Term term = tokenizer.parseExpression();
		if (varNames.size() == 1) {
			final String varName = varNames.get(0);
			return new Assignment() {

				@Override
				public void assign(Scope scope) {
					scope.set(varName, term.evaluate(scope));
				}
			};
		}
		return new Assignment() {

			@Override
			public void assign(Scope scope) {
				unpack(scope, varNames, term.evaluate(scope));
			}
		};
	}

	public static final Assignment EMPTY = new Assignment() {

		@Override
		void assign(Scope scope) {}
	};

	static Assignment parseGroup(Tokenizer tokenizer) {
		ImmutableList.Builder<Assignment> builder = ImmutableList.builder();
		do {
			builder.add(Assignment.parse(tokenizer));
		} while (tokenizer.tryConsume(Symbol.COMMA));
		final ImmutableList<Assignment> assignments = builder.build();
		return new Assignment() {

			@Override
			void assign(Scope scope) {
				for (Assignment assignment : assignments) {
					assignment.assign(scope);
				}
			}
		};
	}

	private static List<String> parseVarNames(Tokenizer tokenizer) {
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

	abstract void assign(Scope scope);

}
