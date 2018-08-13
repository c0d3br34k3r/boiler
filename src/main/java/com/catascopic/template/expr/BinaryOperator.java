package com.catascopic.template.expr;

import com.catascopic.template.Scope;
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
			return Values.isEqual(left, right);
		}
	},
	NOT_EQUAL {

		@Override
		Object apply(Object left, Object right) {
			return !Values.isEqual(left, right);
		}
	},
	AND {

		@Override
		Object apply(Term left, Term right, Scope scope) {
			Object leftValue = left.evaluate(scope);
			return Values.isTrue(leftValue) ? right.evaluate(scope) : leftValue;
		}
	},
	OR {

		@Override
		Object apply(Term left, Term right, Scope scope) {
			Object leftValue = left.evaluate(scope);
			return Values.isTrue(leftValue) ? leftValue : right.evaluate(scope);
		}
	};

	Object apply(Object left, Object right) {
		throw new AssertionError();
	}

	Object apply(Term left, Term right, Scope scope) {
		return apply(left.evaluate(scope), right.evaluate(scope));
	}

}
