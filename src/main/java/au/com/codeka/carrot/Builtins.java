package au.com.codeka.carrot;

import java.util.Collection;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import au.com.codeka.carrot.expr.Functions;

public enum Builtins {

	BOOLEAN {
		@Override
		Object apply(Params params) {
			return ValueHelper.isTrue(params.get());
		}
	},
	FLOAT {
		@Override
		Object apply(Params params) {
			return ValueHelper.toNumber(params.get());
		}
	},
	INT {
		@Override
		Object apply(Params params) {
			return ValueHelper.toNumber(params.get());
		}
	},
	STRING {
		@Override
		Object apply(Params params) {
			return params.get().toString();
		}
	},
	LEN {
		@Override
		Object apply(Params params) {
			return Functions.len(params.get());
		}
	},
	RANGE {
		@Override
		Object apply(Params params) {
			switch (params.size()) {
				case 0:
					throw new IllegalArgumentException();
				case 1:
					return Functions.range(params.getInt());
				case 2:
					return Functions.range(params.getInt(0), params.getInt(1));
				default:
					return Functions.range(params.getInt(0), params.getInt(1), params.getInt(2));
			}
		}
	},
	IN {
		@Override
		Object apply(Params params) {
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
		Object apply(Params params) {
			return Functions.capitalize(params.getStr());
		}
	},
	REPLACE {
		@Override
		Object apply(Params params) {
			return params.getStr(0).startsWith(params.getStr(1));
		}
	},
	STARTS_WITH {
		@Override
		Object apply(Params params) {
			return params.getStr(0).startsWith(params.getStr(1));
		}
	},
	ENDS_WITH {
		@Override
		Object apply(Params params) {
			return params.getStr(0).endsWith(params.getStr(1));
		}
	},
	INDEX_OF {
		@Override
		Object apply(Params params) {
			return params.getStr(0).indexOf(params.getStr(1),
					params.size() >= 3 ? params.getInt(2) : 0);
		}
	},
	LAST_INDEX_OF {
		@Override
		Object apply(Params params) {
			return params.getStr(0).lastIndexOf(params.getStr(1),
					params.size() >= 3 ? params.getInt(2) : 0);
		}
	},
	JOIN {
		@Override
		Object apply(Params params) {
			return Joiner.on(params.getStr(1)).join((Iterable<?>) params.get(0));
		}
	},
	SPLIT {
		@Override
		Object apply(Params params) {
			return Splitter.on(params.getStr(1)).splitToList(params.getStr(0));
		}
	},
	UPPER {
		@Override
		Object apply(Params params) {
			return params.getStr().toUpperCase();
		}
	},
	LOWER {
		@Override
		Object apply(Params params) {
			return params.getStr().toLowerCase();
		}
	},
	TRIM {
		@Override
		Object apply(Params params) {
			return CharMatcher.whitespace().trimFrom(params.getStr());
		}
	},
	SEPARATOR_TO_CAMEL {
		@Override
		Object apply(Params params) {
			switch (params.size()) {
				case 0:
					throw new IllegalArgumentException();
				case 1:
					return Functions.separatorToCamel(params.getStr());
				default:
					return Functions.separatorToCamel(params.getStr(0), params.getStr(1));
			}
		}
	},
	CAMEL_TO_SEPARATOR {
		@Override
		Object apply(Params params) {
			switch (params.size()) {
				case 0:
					throw new IllegalArgumentException();
				case 1:
					return Functions.camelToSeparator(params.getStr());
				default:
					return Functions.camelToSeparator(params.getStr(0), params.getStr(1));
			}
		}
	};

	abstract Object apply(Params params);

}
