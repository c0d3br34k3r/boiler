package com.catascopic.template;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.catascopic.template.eval.Term;
import com.catascopic.template.eval.Tokenizer;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

enum BuiltIn implements TemplateFunction {

	BOOL {

		@Override
		public Object apply(Params params) {
			return params.getBoolean(0);
		}
	},
	FLOAT {

		@Override
		public Object apply(Params params) {
			return params.getDouble(0);
		}
	},
	INT {

		@Override
		public Object apply(Params params) {
			return params.getInt(0);
		}
	},
	STR {

		@Override
		public Object apply(Params params) {
			return params.getString(0);
		}
	},
	LEN {

		@Override
		public Object apply(Params params) {
			return Values.len(params.get(0));
		}
	},
	MIN {

		@Override
		public Object apply(Params params) {
			return Values.min(params.size() == 1
					? params.getIterable(0)
					: params.asList());
		}
	},
	MAX {

		@Override
		public Object apply(Params params) {
			return Values.max(params.size() == 1
					? params.getIterable(0)
					: params.asList());
		}
	},
	ABS {

		@Override
		public Object apply(Params params) {
			return Values.abs(params.getNumber(0));
		}
	},
	RANGE {

		@Override
		public Object apply(Params params) {
			switch (params.size()) {
			case 0:
				throw new TemplateEvalException(
						"range must have at least 1 param");
			case 1:
				return Values.range(params.getInt(0));
			default:
				return Values.range(params.getInt(0), params.getInt(1),
						params.getInt(2, 1));
			}
		}
	},
	ENUMERATE {

		@Override
		public Object apply(Params params) {
			return new Enumeration(params.getIterable(0));
		}
	},
	ZIP {

		@Override
		public Object apply(Params params) {
			return new Zip(params.size() == 1
					? params.getIterable(0)
					: params.asList());
		}
	},
	ENTRIES {

		@Override
		public Object apply(Params params) {
			return Values.entries(params.getMap(0));
		}
	},
	KEYS {

		@Override
		public Object apply(Params params) {
			return params.getMap(0).keySet();
		}
	},
	VALUES {

		@Override
		public Object apply(Params params) {
			return params.getMap(0).values();
		}
	},
	CONTAINS {

		@Override
		public Object apply(Params params) {
			Object seq = params.get(0);
			if (seq instanceof String) {
				return ((String) seq).contains(params.getString(1));
			}
			if (seq instanceof Collection) {
				return ((Collection<?>) seq).contains(params.get(1));
			}
			throw new TemplateEvalException("%s (%s) is not a container", seq,
					seq.getClass().getName());
		}
	},
	CAPITALIZE {

		@Override
		public Object apply(Params params) {
			return Values.capitalize(params.getString(0));
		}
	},
	REPLACE {

		@Override
		public Object apply(Params params) {
			return params.getString(0).replace(params.getString(1),
					params.getString(2));
		}
	},
	STARTS_WITH {

		@Override
		public Object apply(Params params) {
			return params.getString(0).startsWith(params.getString(1));
		}
	},
	ENDS_WITH {

		@Override
		public Object apply(Params params) {
			return params.getString(0).endsWith(params.getString(1));
		}
	},
	INDEX_OF {

		@Override
		public Object apply(Params params) {
			return Values.indexOf(params.get(0), params.get(1),
					params.getInt(2, 0));
		}
	},
	LAST_INDEX_OF {

		@Override
		public Object apply(Params params) {
			if (params.size() <= 2) {
				return Values.lastIndexOf(params.get(0), params.get(1));
			}
			return Values.lastIndexOf(params.get(0), params.get(1),
					params.getInt(2));
		}
	},
	JOIN {

		@Override
		public Object apply(Params params) {
			return Joiner.on(params.getString(1)).join(
					Values.toIterable(params.get(0)));
		}
	},
	SPLIT {

		@Override
		public Object apply(Params params) {
			return Splitter.on(params.getString(1)).splitToList(
					params.getString(0));
		}
	},
	UPPER {

		@Override
		public Object apply(Params params) {
			// TODO: Ascii.toUpperCase?
			return params.getString(0).toUpperCase();
		}
	},
	LOWER {

		@Override
		public Object apply(Params params) {
			return params.getString(0).toLowerCase();
		}
	},
	TRIM {

		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimFrom(params.getString(0));
		}
	},
	COLLAPSE {

		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimAndCollapseFrom(
					params.getString(0), params.getChar(1, " "));
		}
	},
	SEPARATOR_TO_CAMEL {

		@Override
		public Object apply(Params params) {
			return Values.separatorToCamel(params.getString(0),
					params.getString(1, "_"));
		}
	},
	CAMEL_TO_SEPARATOR {

		@Override
		public Object apply(Params params) {
			return Values.camelToSeparator(params.getString(0),
					params.getString(1, "_"));
		}
	},
	PAD {

		@Override
		public Object apply(Params params) {
			return Values.pad(params.getString(0), params.getInt(1),
					params.getChar(2, " "), params.getBoolean(3, true));
		}
	},
	// MATCHES {
	//
	// @Override
	// public Object apply(Params params) {
	// return Pattern.compile(params.getString(1)).matcher(params
	// .getString(0)).matches();
	// }
	// },
	// SEARCH {
	//
	// @Override
	// public Object apply(Params params) {
	// List<Object> result = new ArrayList<>();
	// Matcher matcher = Pattern.compile(params.getString(1)).matcher(
	// params.getString(0));
	// int groups = matcher.groupCount() + 1;
	// while (matcher.find()) {
	// List<String> group = new ArrayList<>(groups);
	// for (int i = 0; i < groups; i++) {
	// group.add(i, matcher.group(i));
	// }
	// result.add(group);
	// }
	// return result;
	// }
	// },
	TEMPLATE {

		@Override
		public Object apply(Params params) {
			final Map<String, ?> map = params.getMap(1,
					Collections.<String, Object> emptyMap());
			Assigner assigner = new Assigner() {

				@Override
				public void assign(Scope scope) {
					for (Map.Entry<String, ?> entry : map.entrySet()) {
						scope.set(entry.getKey(), entry.getValue());
					}
				}
			};
			StringBuilder builder = new StringBuilder();
			try {
				params.scope().renderTemplate(builder, params.getString(0),
						assigner);
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			return builder.toString();
		}
	},
	TEXT_FILE {

		@Override
		public Object apply(Params params) {
			StringBuilder builder = new StringBuilder();
			try {
				params.scope().renderTextFile(builder, params.getString(0));
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			return builder.toString();
		}
	},
	LOCALS {

		@Override
		public Object apply(Params params) {
			return params.scope().locals();
		}
	},
	EVAL {

		@Override
		public Object apply(Params params) {
			Tokenizer tokenizer = new Tokenizer(new PositionReader(
					new StringReader(params.getString(0))));
			Term expression = tokenizer.parseExpression();
			tokenizer.end();
			return expression.evaluate(params.scope());
		}
	};

	// TODO: other possibilities:
	// isUpper
	// isLower
	// regexReplace
	// group
	// sum
	// exp
	// sqrt
	// floor/ceiling/round
	// stringCompare
	// substringBefore/substringAfter
	// date and time functions
	// distinctValues

}
