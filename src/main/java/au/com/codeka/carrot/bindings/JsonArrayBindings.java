package au.com.codeka.carrot.bindings;

import java.util.Iterator;

import javax.annotation.Nonnull;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.ValueHelper;

/**
 * {@link Bindings} based on the values in a {@link JsonArray}.
 *
 * @author Marten Gajda
 */
public final class JsonArrayBindings implements Bindings, Iterable<Object> {

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
	public Iterator<Object> iterator() {
		return Iterators.transform(jsonArray.iterator(), new Function<JsonElement, Object>() {

			@Override
			public Object apply(JsonElement input) {
				return ValueHelper.jsonHelper(input);
			}
		});
	}

}
