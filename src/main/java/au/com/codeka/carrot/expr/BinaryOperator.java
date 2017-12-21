package au.com.codeka.carrot.expr;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Iterables;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;

enum BinaryOperator {

	ADD {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.add(left, right.value());
		}
	},

	SUBTRACT {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.add(left, ValueHelper.negate(right.value()));
		}
	},

	MULTIPLY {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.multiply(left, right.value());
		}
	},

	DIVIDE {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.divide(left, right.value());
		}
	},

	GREATER_THAN {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) > 0;
		}
	},

	LESS_THAN {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) < 0;
		}
	},

	GREATER_THAN_OR_EQUAL {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) >= 0;
		}
	},

	LESS_THAN_OR_EQUAL {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) <= 0;
		}
	},

	EQUAL {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.isEqual(left, right.value());
		}
	},

	NOT_EQUAL {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return !ValueHelper.isEqual(left, right.value());
		}
	},

	AND {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.isTrue(left) ? right.value() : left;
		}
	},

	OR {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			return ValueHelper.isTrue(left) ? left : right.value();
		}
	},

	IN {
		@Override
		Object apply(Object left, LazyValue right) throws CarrotException {
			Object rightValue = right.value();
			if (rightValue instanceof Collection) {
				return ((Collection<?>) rightValue).contains(left);
			}
			if (rightValue instanceof Map) {
				return ((Map<?, ?>) rightValue).containsKey(left);
			}
			if (rightValue instanceof Bindings) {
				return ((Bindings) rightValue).resolve(left.toString()) != null;
			}
			if (rightValue instanceof Iterable) {
				return Iterables.contains(((Iterable<?>) rightValue), left);
			}
			throw new CarrotException("rightValue "
					+ rightValue + "(" + rightValue.getClass() + ") is not a container");
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
	 * @throws CarrotException if the operands are invalid for the given
	 *         operation
	 */
	abstract Object apply(Object left, LazyValue right) throws CarrotException;

}
