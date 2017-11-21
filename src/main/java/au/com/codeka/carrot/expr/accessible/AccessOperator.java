package au.com.codeka.carrot.expr.accessible;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterators;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.ValueHelper;
import au.com.codeka.carrot.expr.Lazy;
import au.com.codeka.carrot.expr.binary.BinaryOperator;

/**
 * The accessor operator like in {@code a.b} or {@code a[b]}. It tries to access
 * the key, index, field or method {@code b} of object {@code a}.
 *
 * @author Marten Gajda
 */
public final class AccessOperator implements BinaryOperator {
	@Override
	public Object apply(Object left, Lazy right) throws CarrotException {
		return access(left, right.value());
	}

	private Object access(Object value, Object accessor) throws CarrotException {
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
			// provide indexed access to Iterables
			// beware, for large Iterables this can be very slow
			return Iterators.get(((Iterable<?>) value).iterator(),
					ValueHelper.toNumber(accessor).intValue());
		}
		// Do some reflection. First, check for a field with the given name.
		try {
			String name = accessor.toString();
			Field field = value.getClass().getField(name);
			field.setAccessible(true);
			return field.get(value);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			// Just keep trying.
		}

		// Next, try a method with the given name
		try {
			String name = accessor.toString();
			Method method = value.getClass().getMethod(name);
			method.setAccessible(true);
			return method.invoke(value);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			// Just keep trying.
		}

		// TODO: yeesh...

		// Next, try a getter method with the given name (that is, if name is
		// "foo" try "getFoo").
		try {
			String name = accessor.toString();
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
			name = "get" + name;
			Method method = value.getClass().getMethod(name);
			method.setAccessible(true);
			return method.invoke(value);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			// Just keep trying.
			throw new CarrotException(e);
		}
		// throw new CarrotException("Cannot access key '" + accessor + "' in '"
		// + value + "'");
	}
}
