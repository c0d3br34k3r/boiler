package au.com.codeka.carrot.expr;

enum BinaryOperator {

	ADD {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.add(left, right.value());
		}
	},

	SUBTRACT {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.add(left, Values.negate(right.value()));
		}
	},

	MULTIPLY {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.multiply(left, right.value());
		}
	},

	DIVIDE {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.divide(left, right.value());
		}
	},

	MODULO {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.modulo(left, right.value());
		}
	},

	GREATER_THAN {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.compare(left, right.value()) > 0;
		}
	},

	LESS_THAN {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.compare(left, right.value()) < 0;
		}
	},

	GREATER_THAN_OR_EQUAL {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.compare(left, right.value()) >= 0;
		}
	},

	LESS_THAN_OR_EQUAL {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.compare(left, right.value()) <= 0;
		}
	},

	EQUAL {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.isEqual(left, right.value());
		}
	},

	NOT_EQUAL {
		@Override
		Object apply(Object left, LazyValue right) {
			return !Values.isEqual(left, right.value());
		}
	},

	AND {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.isTrue(left) ? right.value() : left;
		}
	},

	OR {
		@Override
		Object apply(Object left, LazyValue right) {
			return Values.isTrue(left) ? left : right.value();
		}
	};

	/**
	 * Applies the binary operator to the given operands.
	 * <p>
	 * Note that the right operand is passed as a {@link LazyValue} because some
	 * operands may not need to evaluate it, depending on the left operand (e.g.
	 * && and || operators).
	 *
	 * @param left the left operand.
	 * @param right the {@link LazyValue} right operand.
	 * @return the result of the operation.
	 * @if the operands are invalid for the given
	 *         operation
	 */
	abstract Object apply(Object left, LazyValue right);

}
