package com.catascopic.template.expr;

import com.catascopic.template.Context;
import com.catascopic.template.value.Values;

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
	POWER {

		@Override
		Object apply(Object left, Object right) {
			return Values.power(left, right);
		}
	},
	MODULO {

		@Override
		Object apply(Object left, Object right) {
			return Values.modulo(left, right);
		}
	},
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
			return Values.isTrue(leftValue) ? right.evaluate(context) : leftValue;
		}
	},
	OR {

		@Override
		Object apply(Term left, Term right, Context context) {
			Object leftValue = left.evaluate(context);
			return Values.isTrue(leftValue) ? leftValue : right.evaluate(context);
		}
	};

	/**
	 * Evaluates a binary operation. A BinaryOperator can override this method
	 * if it doesn't always need to evaluate both operands. If it does, it can
	 * implement {@link #apply(Object, Object)}.
	 * 
	 * @param left the left operand as a lazy-evaluated term
	 * @param right the right operand as a lazy-evaluated term
	 * @param context the context
	 * @return the result of the operation
	 */
	Object apply(Term left, Term right, Context context) {
		return apply(left.evaluate(context), right.evaluate(context));
	}

	/**
	 * Evaluates a binary operation. A BinaryOperator can implement this method
	 * if it always needs the result of both operands.
	 * 
	 * @param left the left operand as a value
	 * @param right the right operand as a value
	 * @return the result of the operation
	 */
	Object apply(Object left, Object right) {
		throw new AssertionError();
	}

}
