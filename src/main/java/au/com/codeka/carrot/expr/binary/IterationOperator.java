package au.com.codeka.carrot.expr.binary;

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.expr.Lazy;
import au.com.codeka.carrot.expr.TokenType;

/**
 * The binary ITERATION operator like in {@code a, b}.
 *
 * @author Marten Gajda
 */
public final class IterationOperator implements BinaryOperator {

	@Override
	public Object apply(Object left, Lazy right) throws CarrotException {
		return Iterables.concat(Collections.singleton(left), new LazyIterable(right));
	}

	@Override
	public String toString() {
		return TokenType.COMMA.toString();
	}

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
			return val instanceof Iterable ? ((Iterable<Object>) val).iterator()
					: Iterators.singletonIterator(val);
		}
	}
}
