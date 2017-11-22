package au.com.codeka.carrot.bindings;

import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nonnull;

import au.com.codeka.carrot.Bindings;

/**
 * {@link Bindings} without any values.
 *
 * @author Marten Gajda
 */
public final class EmptyBindings implements Bindings, Iterable<EntryBindings> {

	@Override
	public Object resolve(@Nonnull String key) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public Iterator<EntryBindings> iterator() {
		return Collections.<EntryBindings> emptyList().iterator();
	}

}
