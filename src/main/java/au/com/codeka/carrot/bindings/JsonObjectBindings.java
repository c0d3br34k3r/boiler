package au.com.codeka.carrot.bindings;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.ValueHelper;

/**
 * {@link Bindings} based on the values in a {@link JsonObject}.
 *
 * @author Marten Gajda
 */
public final class JsonObjectBindings implements Bindings, Iterable<EntryBindings> {
	private final JsonObject jsonObject;

	public JsonObjectBindings(JsonObject JsonObject) {
		this.jsonObject = JsonObject;
	}

	@Override
	public Object resolve(@Nonnull String key) {
		return ValueHelper.jsonHelper(jsonObject.get(key));
	}

	@Override
	public boolean isEmpty() {
		return jsonObject.entrySet().size() == 0;
	}

	@Override
	public Iterator<EntryBindings> iterator() {
		final Iterator<Entry<String, JsonElement>> keys = jsonObject.entrySet().iterator();

		// return an iterator of Map Entries which allows iterating json objects
		// like this:
		// {% for item in json %}
		// {{ item.key }} -> {{ item.value }}
		// {% end %}
		return new Iterator<EntryBindings>() {
			@Override
			public boolean hasNext() {
				return keys.hasNext();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Remove not supported.");
			}

			@Override
			public EntryBindings next() {
				final Entry<String, JsonElement> entry = keys.next();
				return new EntryBindings(entry.getKey(), ValueHelper.jsonHelper(entry.getValue()));
			}
		};
	}
}
