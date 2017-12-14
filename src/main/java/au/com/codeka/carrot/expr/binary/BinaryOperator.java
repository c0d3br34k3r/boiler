package au.com.codeka.carrot.expr.binary;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Iterables;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;
import au.com.codeka.carrot.expr.Lazy;
import au.com.codeka.carrot.expr.accessible.Access;

public enum BinaryOperator {

	PLUS {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.add(left, right.value());
		}
	},

	MINUS {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.add(left, ValueHelper.negate(right.value()));
		}
	},

	MULTIPLY {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.multiply(left, right.value());
		}
	},

	DIVIDE {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.divide(left, right.value());
		}
	},

	GREATER_THAN {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) > 0;
		}
	},

	LESS_THAN {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) < 0;
		}
	},

	GREATER_THAN_OR_EQUAL {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) >= 0;
		}
	},

	LESS_THAN_OR_EQUAL {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.compare(left, right.value()) <= 0;
		}
	},

	EQUAL {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.isEqual(left, right.value());
		}
	},

	NOT_EQUAL {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return !ValueHelper.isEqual(left, right.value());
		}
	},

	AND {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.isTrue(left) ? right.value() : left;
		}
	},

	OR {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.isTrue(left) ? left : right.value();
		}
	},

	IN {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
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
	},

	ACCESS {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return Access.access(left, right);
		}
	};

	/**
	 * Applies the binary operator to the given operands.
	 * <p>
	 * Note that the right operand is passed as a {@link Lazy} because some
	 * operands may not need to evaluate it, depending on the left operand (e.g.
	 * && and || operators).
	 *
	 * @param left the left operand.
	 * @param right the {@link Lazy} right operand.
	 * @return the result of the operation.
	 * @throws CarrotException if the operands are invalid for the given
	 *         operation
	 */
	public abstract Object apply(Object left, Lazy right) throws CarrotException;

}
