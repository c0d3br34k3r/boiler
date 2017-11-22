package au.com.codeka.carrot.expr.binary;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;
import au.com.codeka.carrot.expr.Lazy;
import au.com.codeka.carrot.expr.accessible.Access;

public enum BinaryOperators implements BinaryOperator {

	ADDITION {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.add(left, right.value());
		}
	},

	SUBTRACTION {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.add(left, ValueHelper.negate(right.value()));
		}
	},

	MULTIPLICATION {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.multiply(left, right.value());
		}
	},

	DIVISION {
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

	EQUALS {
		@Override
		public Object apply(Object left, Lazy right) throws CarrotException {
			return ValueHelper.isEqual(left, right.value());
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

	ITERATION {
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
			return val instanceof Iterable ? ((Iterable<?>) val).iterator()
					: Iterators.singletonIterator(val);
		}
	}

}
