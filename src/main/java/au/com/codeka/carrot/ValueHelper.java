package au.com.codeka.carrot;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import au.com.codeka.carrot.bindings.JsonArrayAsList;
import au.com.codeka.carrot.bindings.JsonObjectAsBindings;

/**
 * Various helpers for working with {@link Object}s.
 */
public class ValueHelper {

	/**
	 * Does the given value represent "true". For example, it's a Boolean that's
	 * true, a non-zero integer, etc.
	 *
	 * @param value the value to test
	 * @return true if the value is thruthy, false otherwise
	 * @throws CarrotException when the value cannot be determined to be true or
	 *         false
	 */
	public static boolean isTrue(Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}
		if (value instanceof String) {
			return !((String) value).isEmpty();
		}
		// these truthy values deviate from JavaScript's behavior FWIW
		if (value instanceof Collection) {
			return !((Collection<?>) value).isEmpty();
		}
		if (value instanceof Map) {
			return !((Map<?, ?>) value).isEmpty();
		}
		if (value instanceof Bindings) {
			return !((Bindings) value).isEmpty();
		}
		if (value instanceof Iterable) {
			// evaluate non-empty iterables to true, empty iterables to false
			return !((Iterable<?>) value).iterator().hasNext();
		}
		// any unknown non-null, non-boolean value evaluates to true
		return value != null;
	}

	/**
	 * Returns the negative of the given value. For example, if you pass in 1
	 * then -1 is returned, etc.
	 *
	 * @param value The value to negate.
	 * @return The negated value.
	 * @throws CarrotException if the value can't be converted to a number
	 */
	public static Number negate(Object value) throws CarrotException {
		Number num = toNumber(value);
		if (num instanceof Integer) {
			return -num.intValue();
		}
		return -num.doubleValue();
	}

	/**
	 * Converts the given value to a {@link Number}.
	 *
	 * @param value The value to convert.
	 * @return A {@link Number} that the value represents.
	 * @throws CarrotException Thrown if the value can't be converted to a
	 *         number.
	 */
	public static Number toNumber(Object value) throws CarrotException {
		if (value instanceof Number) {
			return (Number) value;
		}
		if (value instanceof String) {
			return parseNumber((String) value);
		}
		if (value instanceof Boolean) {
			return (Boolean) value ? 1 : 0;
		}
		throw new CarrotException(
				"cannot convert " + value + " (" + value.getClass() + ") to a number.");
	}

	private static Number parseNumber(String value) {
		if (value.contains(".")) {
			return Double.parseDouble(value);
		}
		return Integer.parseInt(value);
	}

	public static Object add(Object o1, Object o2) {
		if (o1 instanceof Number) {
			Number n1 = (Number) o1;
			if (o2 instanceof Number) {
				return add(n1, (Number) o2);
			}
			Number n2 = tryConvertNumber(o2);
			if (n2 != null) {
				return add(n1, n2);
			}
		} else if (o2 instanceof Number) {
			Number n1 = tryConvertNumber(o1);
			if (n1 != null) {
				return add(n1, (Number) o2);
			}
		}
		// should we be able to add things that aren't strings?
		return o1.toString() + o2.toString();
	}

	private static Number tryConvertNumber(Object value) {
		if (value instanceof String) {
			String str = (String) value;
			try {
				if (str.contains(".")) {
					return Double.parseDouble(str);
				}
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
				// continue
			}
		} else if (value instanceof Boolean) {
			return (Boolean) value ? 1 : 0;
		}
		return null;
	}

	public static Number add(Number a, Number b) {
		if (a instanceof Integer && b instanceof Integer) {
			return a.intValue() + b.intValue();
		}
		return a.doubleValue() + b.doubleValue();
	}

	/**
	 * Divides the left hand side by the right hand side, and returns the
	 * result.
	 *
	 * @param o1 The left hand side of the division.
	 * @param o2 The right hand side of the division.
	 * @return The result of "lhs / rhs".
	 * @throws CarrotException Thrown is either of the values cannot be
	 *         converted to a number.
	 */
	public static Number divide(Object o1, Object o2) throws CarrotException {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() / n2.intValue();
		}
		return n1.doubleValue() / n2.doubleValue();
	}

	/**
	 * Multiplies the left hand side by the right hand side, and returns the
	 * result.
	 *
	 * @param o1 The left hand side of the multiplication.
	 * @param o2 The right hand side of the multiplication.
	 * @return The result of "lhs * rhs".
	 * @throws CarrotException Thrown is either of the values cannot be
	 *         converted to a number.
	 */
	public static Number multiply(Object o1, Object o2) throws CarrotException {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() * n2.intValue();
		}
		return n1.doubleValue() * n2.doubleValue();
	}

	public static Number modulo(Object o1, Object o2) throws CarrotException {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() % n2.intValue();
		}
		return n1.doubleValue() % n2.doubleValue();
	}

	/**
	 * Convert the given value to a list of object, as if it were an iterable.
	 * If the value is itself an array or a list then it's just returned
	 * in-place. Otherwise it will be converted to an {@link ArrayList}.
	 *
	 * @param collection The value to "iterate".
	 * @return A {@link List} that can actually be iterated.
	 * @throws CarrotException If the value is not iterable.
	 */
	public static Collection<?> toCollection(final Object collection) throws CarrotException {
		if (collection == null) {
			return Collections.emptySet();
		}
		if (collection instanceof Collection) {
			return (Collection<?>) collection;
		}
		if (collection instanceof Iterable) {
			return ImmutableList.copyOf((Iterable<?>) collection);
		}
		if (collection instanceof Map) {
			return ((Map<?, ?>) collection).keySet();
		}
		// TODO: arrays suck
		if (collection.getClass().isArray()) {
			final int length = Array.getLength(collection);
			return new AbstractList<Object>() {

				@Override
				public Object get(int index) {
					return Array.get(collection, index);
				}

				@Override
				public int size() {
					return length;
				}
			};
		}
		throw new CarrotException(
				"not iterable: " + collection + " (" + collection.getClass() + ")");
	}

	/**
	 * Tests the equality of the two given values.
	 *
	 * @param lhs The left-hand side you want to test for equality.
	 * @param rhs The right-hand side you want to test for equality.
	 * @return A value to indicate whether the value is true or false.
	 * @throws CarrotException If there's an error evaluating the objects.
	 */
	public static boolean isEqual(Object lhs, Object rhs) throws CarrotException {
		if (lhs instanceof Number || rhs instanceof Number) {
			return compare(lhs, rhs) == 0;
		}
		return Objects.equals(lhs, rhs);
	}

	/**
	 * Performs a numerical comparison on the two operands (assuming they are
	 * both convertible to numbers).
	 *
	 * @param a The left hand side to compare.
	 * @param b The right hand side to compare.
	 * @return Less than zero if lhs is less than rhs, zero if lhs is equal to
	 *         rhs, and greater than zero if lhs is greater than rhs.
	 * @throws CarrotException if either of the objects cannot be converted to
	 *         numbers.
	 */
	public static int compare(Object a, Object b) throws CarrotException {
		return compare(toNumber(a), toNumber(b));
	}

	public static int compare(Number a, Number b) {
		if (a instanceof Integer && b instanceof Integer) {
			return Integer.compare(a.intValue(), b.intValue());
		}
		return Double.compare(a.doubleValue(), b.doubleValue());
	}

	// TODO: JSON has questionable value...
	public static Object jsonHelper(Object object) {
		if (!(object instanceof JsonElement)) {
			return object;
		}
		JsonElement json = (JsonElement) object;
		if (json.isJsonObject()) {
			return new JsonObjectAsBindings(json.getAsJsonObject());
		}
		if (json.isJsonArray()) {
			return new JsonArrayAsList(json.getAsJsonArray());
		}
		if (json.isJsonPrimitive()) {
			JsonPrimitive primitive = json.getAsJsonPrimitive();
			if (primitive.isNumber()) {
				Number number = primitive.getAsNumber();
				double doubleValue = number.doubleValue();
				if (doubleValue == Math.rint(doubleValue) && doubleValue < Integer.MAX_VALUE) {
					return number.intValue();
				}
				return doubleValue;
			}
			if (primitive.isString()) {
				return primitive.getAsString();
			}
			if (primitive.isBoolean()) {
				return primitive.getAsBoolean();
			}
		}
		if (json.isJsonNull()) {
			return null;
		}
		throw new IllegalArgumentException();
	}

}
