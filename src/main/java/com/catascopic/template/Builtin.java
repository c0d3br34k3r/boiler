package com.catascopic.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

enum Builtin implements TemplateFunction {

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
				return Values.range(params.getInt(0),
						params.getInt(1), params.getInt(2, 1));
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
			throw new TemplateEvalException(
					"%s (%s) is not a container",
					seq, seq.getClass().getName());
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
					params.getString(0),
					params.getString(1, " ").charAt(0));
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
			return Values.camelToSeparator(params.getString(0), params
					.getString(1, "_"));
		}
	},
	PAD {

		@Override
		public Object apply(Params params) {
			return Values.pad(params.getString(0),
					params.getInt(1),
					params.getString(2, " ").charAt(0),
					params.getBoolean(3, true));
		}
	},
	MATCHES {

		@Override
		public Object apply(Params params) {
			return Pattern.compile(params.getString(1))
					.matcher(params.getString(0)).matches();
		}
	},
	SEARCH {

		@Override
		public Object apply(Params params) {
			List<Object> result = new ArrayList<>();
			Matcher matcher = Pattern.compile(params.getString(1))
					.matcher(params.getString(0));
			int groups = matcher.groupCount() + 1;
			while (matcher.find()) {
				List<String> group = new ArrayList<>(groups);
				for (int i = 1; i < groups + 1; i++) {
					group.add(i, matcher.group(i));
				}
				result.add(group);
			}
			return result;
		}
	},
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
				params.scope().renderTemplate(builder,
						params.getString(0), assigner);
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			return builder.toString();
		}
	},
	TEXT {

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
