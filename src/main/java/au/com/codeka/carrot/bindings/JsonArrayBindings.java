package au.com.codeka.carrot.bindings;

import java.util.AbstractList;

import com.google.gson.JsonArray;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.ValueHelper;

/**
 * {@link Bindings} based on the values in a {@link JsonArray}.
 *
 * @author Marten Gajda
 */
public final class JsonArrayBindings extends AbstractList<Object> {

	private final JsonArray jsonArray;

	public JsonArrayBindings(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	@Override
	public Object get(int index) {
		return ValueHelper.jsonHelper(jsonArray.get(index));
	}

	@Override
	public int size() {
		return jsonArray.size();
	}

}
