package au.com.codeka.carrot.expr.accessible;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterators;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;
import au.com.codeka.carrot.expr.Lazy;

public final class Access {

	private Access() {}

	public static Object access(Object left, Lazy right) throws CarrotException {
		return access(left, right.value());
	}

	public static Object access(Object value, Object accessor) throws CarrotException {
		if (value == null) {
			return null;
		}
		if (value instanceof Map) {
			return ((Map<?, ?>) value).get(accessor);
		}
		if (value instanceof Bindings) {
			return ((Bindings) value).resolve(accessor.toString());
		}
		if (value instanceof List) {
			return ((List<?>) value).get(ValueHelper.toNumber(accessor).intValue());
		}
		if (value.getClass().isArray()) {
			return Array.get(value, ValueHelper.toNumber(accessor).intValue());
		}
		if (value instanceof Iterable && accessor instanceof Number) {
			return Iterators.get(((Iterable<?>) value).iterator(),
					ValueHelper.toNumber(accessor).intValue());
		}
		throw new CarrotException("Cannot access key " + accessor + " in " + value);
	}

}
