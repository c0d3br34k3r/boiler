package au.com.codeka.carrot.bindings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import au.com.codeka.carrot.Bindings;

/**
 * {@link Bindings} of the loop variables of a for loop.
 *
 * @author Marten Gajda
 */
public final class LoopBindings implements Bindings {

	private final int count;
	private final int current;

	public LoopBindings(int count, int current) {
		this.count = count;
		this.current = current;
	}

	@Nullable
	@Override
	public Object resolve(@Nonnull String key) {
		switch (key) {
			case "index":
				return current;
			case "revindex":
				return count - current - 1;
			case "first":
				return current == 0;
			case "last":
				return current == (count - 1);
			case "length":
				return count;
			default:
				return null;
		}
	}

	@Override
	public boolean isEmpty() {
		// loop bindings are never ever empty
		return false;
	}

}
