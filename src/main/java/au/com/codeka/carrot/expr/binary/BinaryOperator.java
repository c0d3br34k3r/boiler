package au.com.codeka.carrot.expr.binary;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

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
			// TODO: throw exception?
			return false;
		}
	},

	ITERATE {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return Iterables.concat(Collections.singleton(left), new LazyIterable(right));
		}
	},

	ACCESS {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return Access.access(left, right);
		}
	};

	/**
	 * An {@link Iterable} which flattens the value of the right hand side of an
	 * IterableOperator.
	 */
	private final class LazyIterable implements Iterable<Object> {
		private final Lazy value;

		public LazyIterable(Lazy value) {
			this.value = value;
		}

		// TODO:
		@SuppressWarnings("unchecked")
		@Nonnull
		@Override
		public Iterator<Object> iterator() {
			Object val;
			try {
				val = value.value();
			} catch (CarrotException e) {
				// TODO: find more appropriate exception
				throw new IllegalStateException("can't iterate elements", e);
			}
			return val instanceof Iterable ? ((Iterable<Object>) val).iterator()
					: Iterators.singletonIterator(val);
		}
	}

	/**
	 * Applies the binary operator to the given operands.
	 *
	 * <p>
	 * Note that the right operand is passed as a {@link Lazy} because some
	 * operands may not need to evaluate it, depending on the left operand (e.g.
	 * boolean `and` and `or` operators).
	 *
	 * @param left The left operand.
	 * @param right The {@link Lazy} right operand.
	 * @return The result of the operation.
	 * @throws CarrotException if there's any error applying the operator.
	 */
	public abstract Object apply(Object left, Lazy right) throws CarrotException;

}
