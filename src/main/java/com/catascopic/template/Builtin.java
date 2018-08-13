package com.catascopic.template;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

enum Builtin implements TemplateFunction {

	BOOL {

		@Override
		public Object apply(Params params) {
			return Values.isTrue(params.get());
		}
	},
	FLOAT {

		@Override
		public Object apply(Params params) {
			return Values.toNumber(params.get()).doubleValue();
		}
	},
	INT {

		@Override
		public Object apply(Params params) {
			return Values.toNumber(params.get()).intValue();
		}
	},
	STR {

		@Override
		public Object apply(Params params) {
			return Values.toString(params.get());
		}
	},
	LEN {

		@Override
		public Object apply(Params params) {
			return Values.len(params.get());
		}
	},
	MIN {

		@Override
		public Object apply(Params params) {
			return Values.min(params.size() == 1
					? Values.toIterable(params.get())
					: params.asList());
		}
	},
	MAX {

		@Override
		public Object apply(Params params) {
			return Values.max(params.size() == 1
					? Values.toIterable(params.get())
					: params.asList());
		}
	},
	ABS {

		@Override
		public Object apply(Params params) {
			return Values.abs((Number) params.get());
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
				return Values.range(params.<Integer> get());
			case 2:
				return Values.range(params.<Integer> get(0),
						params.<Integer> get(1));
			default:
				return Values.range(params.<Integer> get(0),
						params.<Integer> get(1), params.<Integer> get(2));
			}
		}
	},
	ENUMERATE {

		@Override
		public Object apply(Params params) {
			return new Enumeration(Values.toIterable(params.get()));
		}
	},
	ZIP {

		@Override
		public Object apply(Params params) {
			return new Zip(params.size() == 1
					? params.asList()
					: Values.toIterable(params.get()));
		}
	},
	ENTRIES {

		@Override
		public Object apply(Params params) {
			return Values.entries(params.<Map<?, ?>> get());
		}
	},
	KEYS {

		@Override
		public Object apply(Params params) {
			return params.<Map<?, ?>> get().keySet();
		}
	},
	VALUES {

		@Override
		public Object apply(Params params) {
			return params.<Map<?, ?>> get().values();
		}
	},
	CONTAINS {

		@Override
		public Object apply(Params params) {
			Object seq = params.get(0);
			if (seq instanceof String) {
				return ((String) seq).contains(params.<String> get(1));
			}
			if (seq instanceof Collection) {
				return ((Collection<?>) seq).contains(params.get());
			}
			throw new TemplateEvalException(
					"%s (%s) is not a container",
					seq, seq.getClass().getName());
		}
	},
	CAPITALIZE {

		@Override
		public Object apply(Params params) {
			return Values.capitalize(params.<String> get());
		}
	},
	REPLACE {

		@Override
		public Object apply(Params params) {
			return params.<String> get(0).replace(params.<String> get(1),
					params.<String> get(2));
		}
	},
	STARTS_WITH {

		@Override
		public Object apply(Params params) {
			return params.<String> get(0).startsWith(params.<String> get(1));
		}
	},
	ENDS_WITH {

		@Override
		public Object apply(Params params) {
			return params.<String> get(0).endsWith(params.<String> get(1));
		}
	},
	INDEX_OF {

		@Override
		public Object apply(Params params) {
			String str = params.<String> get(0);
			Values.index(str, (int) params.get(1));
			int index = params.size() >= 3
					? Values.getIndex(params.<Integer> get(1), str)
					: 0;
			return params.<String> get(0).indexOf(params.<String> get(1),
					index);
		}
	},
	LAST_INDEX_OF {

		@Override
		public Object apply(Params params) {
			String str = params.<String> get(0);
			int index = params.size() >= 3
					? Values.getIndex(params.<Integer> get(1), str)
					: str.length() - 1;
			return params.<String> get(0).indexOf(params.<String> get(1),
					index);
		}
	},
	JOIN {

		@Override
		public Object apply(Params params) {
			return Joiner.on(params.<String> get(1)).join(
					Values.toIterable(params.get(0)));
		}
	},
	SPLIT {

		@Override
		public Object apply(Params params) {
			return Splitter.on(params.<String> get(1)).splitToList(
					params.<String> get(0));
		}
	},
	UPPER {

		@Override
		public Object apply(Params params) {
			return params.<String> get().toUpperCase();
		}
	},
	LOWER {

		@Override
		public Object apply(Params params) {
			return params.<String> get().toLowerCase();
		}
	},
	TRIM {

		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimFrom(params.<String> get());
		}
	},
	COLLAPSE {

		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimAndCollapseFrom(
					params.<String> get(0),
					params.getOrDefault(1, " ").charAt(0));
		}
	},
	SEPARATOR_TO_CAMEL {

		@Override
		public Object apply(Params params) {
			return Values.separatorToCamel(params.<String> get(0),
					params.getOrDefault(1, "_"));
		}
	},
	CAMEL_TO_SEPARATOR {

		@Override
		public Object apply(Params params) {
			return Values.camelToSeparator(params.<String> get(0), params
					.getOrDefault(1, "_"));
		}
	};

	// TODO: other possibilities:
	// sum
	// exp
	// sqrt
	// floor/ceiling/round
	// stringCompare
	// substringBefore/substringAfter
	// date and time functions
	// distinctValues

	String functionName() {
		return Values.separatorToCamel(name().toLowerCase());
	}

}
