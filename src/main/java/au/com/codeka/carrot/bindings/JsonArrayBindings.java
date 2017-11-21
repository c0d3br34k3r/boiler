package au.com.codeka.carrot.bindings;

import java.util.Iterator;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.ValueHelper;

/**
 * {@link Bindings} based on the values in a {@link JsonArray}.
 *
 * @author Marten Gajda
 */
public final class JsonArrayBindings implements Bindings, Iterable<JsonElement> {
	private final JsonArray jsonArray;

	public JsonArrayBindings(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	@Override
	public Object resolve(@Nonnull String key) {
		return ValueHelper.jsonHelper(jsonArray.get(Integer.parseInt(key)));
	}

	@Override
	public boolean isEmpty() {
		return jsonArray.size() == 0;
	}

	@Override
	public Iterator<JsonElement> iterator() {
		return jsonArray.iterator();
	}
}
