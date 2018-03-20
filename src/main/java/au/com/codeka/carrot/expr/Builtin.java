package au.com.codeka.carrot.expr;

import java.util.Collection;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import au.com.codeka.carrot.Params;

public enum Builtin implements TemplateFunction {

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
			return params.get().toString();
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
			return Values.min(params.size() == 1 ? (Iterable<?>) params.get() : params);
		}
	},
	MAX {
		@Override
		public Object apply(Params params) {
			return Values.max(params.size() == 1 ? (Iterable<?>) params.get() : params);
		}
	},
	RANGE {
		@Override
		public Object apply(Params params) {
			switch (params.size()) {
			case 0:
				throw new IllegalArgumentException();
			case 1:
				return Values.range(params.getInt());
			case 2:
				return Values.range(params.getInt(0), params.getInt(1));
			default:
				return Values.range(params.getInt(0), params.getInt(1), params.getInt(2));
			}
		}
	},
	CONTAINS {
		@Override
		public Object apply(Params params) {
			Object seq = params.get(0);
			if (seq instanceof String) {
				return ((String) seq).contains(params.getStr(1));
			}
			if (seq instanceof Collection) {
				return ((Collection<?>) seq).contains(params.get());
			}
			throw new IllegalArgumentException();
		}
	},
	CAPITALIZE {
		@Override
		public Object apply(Params params) {
			return Values.capitalize(params.getStr());
		}
	},
	REPLACE {
		@Override
		public Object apply(Params params) {
			return params.getStr(0).startsWith(params.getStr(1));
		}
	},
	STARTS_WITH {
		@Override
		public Object apply(Params params) {
			return params.getStr(0).startsWith(params.getStr(1));
		}
	},
	ENDS_WITH {
		@Override
		public Object apply(Params params) {
			return params.getStr(0).endsWith(params.getStr(1));
		}
	},
	INDEX_OF {
		@Override
		public Object apply(Params params) {
			return params.getStr(0).indexOf(params.getStr(1),
					params.size() >= 3 ? params.getInt(2) : 0);
		}
	},
	LAST_INDEX_OF {
		@Override
		public Object apply(Params params) {
			return params.getStr(0).lastIndexOf(params.getStr(1),
					params.size() >= 3 ? params.getInt(2) : 0);
		}
	},
	JOIN {
		@Override
		public Object apply(Params params) {
			return Joiner.on(params.getStr(1)).join((Iterable<?>) params.get(0));
		}
	},
	SPLIT {
		@Override
		public Object apply(Params params) {
			return Splitter.on(params.getStr(1)).splitToList(params.getStr(0));
		}
	},
	UPPER {
		@Override
		public Object apply(Params params) {
			return params.getStr().toUpperCase();
		}
	},
	LOWER {
		@Override
		public Object apply(Params params) {
			return params.getStr().toLowerCase();
		}
	},
	TRIM {
		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimFrom(params.getStr());
		}
	},
	COLLAPSE {
		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimAndCollapseFrom(params.getStr(0), params
					.getStrOrDefault(1, "_").charAt(0));
		}
	},
	SEPARATOR_TO_CAMEL {
		@Override
		public Object apply(Params params) {
			return Values.separatorToCamel(params.getStr(0), params.getStrOrDefault(1, "_"));
		}
	},
	CAMEL_TO_SEPARATOR {
		@Override
		public Object apply(Params params) {
			return Values.camelToSeparator(params.getStr(0), params.getStrOrDefault(1, "_"));
		}
	};

	// TODO: other possibilities:
	// abs
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
