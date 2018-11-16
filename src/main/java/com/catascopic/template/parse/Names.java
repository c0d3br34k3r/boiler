package com.catascopic.template.parse;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.catascopic.template.Locatable;
import com.catascopic.template.Locatables;
import com.catascopic.template.Scope;
import com.catascopic.template.TemplateEvalException;
import com.catascopic.template.TemplateParseException;
import com.catascopic.template.Values;
import com.catascopic.template.eval.Symbol;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets;

abstract class Names {

	static Names parse(Tokenizer tokenizer) {
		Locatable location = new Locatables(tokenizer);
		String name = tokenizer.parseIdentifier();
		if (!tokenizer.tryConsume(Symbol.COMMA)) {
			return new Name(name);
		}
		Set<String> unique = Sets.newHashSet(name);
		Builder<String> builder = ImmutableList.builder();
		builder.add(name);
		do {
			name = tokenizer.parseIdentifier();
			builder.add(name);
			if (!unique.add(name)) {
				throw new TemplateParseException(tokenizer,
						"duplicate name: %s", name);
			}
		} while (tokenizer.tryConsume(Symbol.COMMA));
		return new UnpackNames(builder.build(), location);
	}

	abstract void assign(Scope scope, Object value);

	private static class Name extends Names {

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

	private static class UnpackNames extends Names {

		private final List<String> varNames;
		private final Locatable location;

		UnpackNames(List<String> varNames, Locatable location) {
			this.varNames = varNames;
		}

		@Override
		public void assign(Scope scope, Object value) {
			Iterator<String> iter = varNames.iterator();
			for (Object unpacked : Values.toIterable(value)) {
				if (!iter.hasNext()) {
					throw new TemplateEvalException(
							"too many values to unpack into names: %s",
							varNames);
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
