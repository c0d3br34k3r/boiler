package com.catascopic.template.eval;

import com.catascopic.template.Context;
import com.catascopic.template.Values;

enum BinaryOperator {

	ADD {

		@Override
		Object apply(Object left, Object right) {
			return Values.add(left, right);
		}
	},
	SUBTRACT {

		@Override
		Object apply(Object left, Object right) {
			return Values.add(left, Values.negate(right));
		}
	},
	MULTIPLY {

		@Override
		Object apply(Object left, Object right) {
			return Values.multiply(left, right);
		}
	},
	DIVIDE {

		@Override
		Object apply(Object left, Object right) {
			return Values.divide(left, right);
		}
	},
	MODULO {

		@Override
		Object apply(Object left, Object right) {
			return Values.modulo(left, right);
		}
	},

	// TODO: EXPONENT?

	GREATER_THAN {

		@Override
		Object apply(Object left, Object right) {
			return Values.compare(left, right) > 0;
		}
	},
	LESS_THAN {

		@Override
		Object apply(Object left, Object right) {
			return Values.compare(left, right) < 0;
		}
	},
	GREATER_THAN_OR_EQUAL {

		@Override
		Object apply(Object left, Object right) {
			return Values.compare(left, right) >= 0;
		}
	},
	LESS_THAN_OR_EQUAL {

		@Override
		Object apply(Object left, Object right) {
			return Values.compare(left, right) <= 0;
		}
	},
	EQUAL {

		@Override
		Object apply(Object left, Object right) {
			return Values.equal(left, right);
		}
	},
	NOT_EQUAL {

		@Override
		Object apply(Object left, Object right) {
			return !Values.equal(left, right);
		}
	},
	AND {

		@Override
		Object apply(Term left, Term right, Context context) {
			Object leftValue = left.evaluate(context);
			return Values.isTrue(leftValue) ? right.evaluate(context)
					: leftValue;
		}
	},
	OR {

		@Override
		Object apply(Term left, Term right, Context context) {
			Object leftValue = left.evaluate(context);
			return Values.isTrue(leftValue) ? leftValue : right.evaluate(
					context);
		}
	};

	Object apply(Object left, Object right) {
		throw new AssertionError();
	}

	Object apply(Term left, Term right, Context context) {
		return apply(left.evaluate(context), right.evaluate(context));
	}

}
