package au.com.codeka.carrot.expr.accessible;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import au.com.codeka.carrot.CarrotException;

/**
 * A function or method which can be called with an {@link Iterable} of
 * parameters.
 *
 * @author Marten Gajda
 */
public interface Callable {

	/**
	 * Calls the {@link Callable} with the given parameters.
	 *
	 * @param params The actual parameters of the call. May be an empty
	 *        {@link Iterable}.
	 * @return The result of the call.
	 * @throws CarrotException If something went wrong.
	 */
	@Nullable
	Object call(@Nonnull Iterable<?> params) throws CarrotException;

}
