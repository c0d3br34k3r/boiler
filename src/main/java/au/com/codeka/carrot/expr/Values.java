package au.com.codeka.carrot.expr;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.TemplateParseException;

public class Values {

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
			return !((Iterable<?>) value).iterator().hasNext();
		}
		return value != null;
	}

	public static Number negate(Object value) {
		Number num = toNumber(value);
		if (num instanceof Integer) {
			return -num.intValue();
		}
		return -num.doubleValue();
	}

	public static Number toNumber(Object value) {
		if (value instanceof Number) {
			return (Number) value;
		}
		if (value instanceof String) {
			return parseNumber((String) value);
		}
		if (value instanceof Boolean) {
			return (Boolean) value ? 1 : 0;
		}
		throw new TemplateParseException(
				"cannot convert %s (%s) to a number", value, value.getClass());
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
		// TODO: should we be able to add things that aren't strings?
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
			} catch (@SuppressWarnings("unused") NumberFormatException e) {
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

	public static Number divide(Object o1, Object o2) {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() / n2.intValue();
		}
		return n1.doubleValue() / n2.doubleValue();
	}

	public static Number multiply(Object o1, Object o2) {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() * n2.intValue();
		}
		return n1.doubleValue() * n2.doubleValue();
	}

	public static Number modulo(Object o1, Object o2) {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() % n2.intValue();
		}
		return n1.doubleValue() % n2.doubleValue();
	}

	public static Iterable<?> toCollection(final Object iterable) {
		if (iterable instanceof Iterable) {
			return (Iterable<?>) iterable;
		}
		if (iterable instanceof Map) {
			return ((Map<?, ?>) iterable).keySet();
		}
		if (iterable instanceof String) {
			return new StringIterable((String) iterable);
		}
		throw new TemplateParseException(
				"not iterable: %s (%s)", iterable, iterable.getClass());
	}

	public static boolean isEqual(Object lhs, Object rhs) {
		if (lhs instanceof Number || rhs instanceof Number) {
			return compare(lhs, rhs) == 0;
		}
		return Objects.equals(lhs, rhs);
	}

	public static int compare(Object a, Object b) {
		return compare(toNumber(a), toNumber(b));
	}

	public static int compare(Number a, Number b) {
		if (a instanceof Integer && b instanceof Integer) {
			return Integer.compare(a.intValue(), b.intValue());
		}
		return Double.compare(a.doubleValue(), b.doubleValue());
	}

	public static String toString(Object o) {
		if (o instanceof Number && !(o instanceof Integer)) {
			Number n = (Number) o;
			double d = n.doubleValue();
			if (Math.rint(d) == d) {
				return Integer.toString(n.intValue());
			}
		}
		return o.toString();
	}

	private static class StringIterable extends AbstractList<String> {

		private final String str;

		public StringIterable(String str) {
			this.str = str;
		}

		@Override
		public String get(int index) {
			return String.valueOf(str.charAt(index));
		}

		@Override
		public int size() {
			return str.length();
		}
	}

	private static final CharMatcher UPPER = CharMatcher.inRange('A', 'Z');

	public static String camelToSeparator(String str) {
		return camelToSeparator(str, "_");
	}

	// TODO: figure out uppercase words?
	public static String camelToSeparator(String str, String separator) {
		int start = 0;
		StringBuilder result = new StringBuilder();
		for (;;) {
			int index = UPPER.indexIn(str, start);
			if (index == -1) {
				return result.append(str.substring(start)).toString();
			}
			result.append(str.substring(start, index)).append(separator)
					.append(Character.toLowerCase(str.charAt(index)));
			start = index + 1;
		}
	}

	public static String separatorToCamel(String str) {
		return separatorToCamel(str, "_");
	}

	public static String separatorToCamel(String str, String separator) {
		StringBuilder result = new StringBuilder();
		Iterator<String> parts = Splitter.on(separator).split(str).iterator();
		result.append(parts.next());
		while (parts.hasNext()) {
			result.append(capitalize(parts.next()));
		}
		return result.toString();
	}

	public static String capitalize(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static int len(Object obj) {
		if (obj instanceof String) {
			return ((String) obj).length();
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		}
		throw new IllegalArgumentException();
	}

	public static List<Integer> range(int stop) {
		return range(0, stop);
	}

	public static List<Integer> range(int start, int stop) {
		return range(start, stop, 1);
	}

	public static List<Integer> range(final int start, int stop,
			final int step) {
		final int size = Math.max(ceilDivide(stop - start, step), 0);
		return new AbstractList<Integer>() {

			@Override
			public Integer get(int index) {
				if (index < 0 || index >= size) {
					throw new IndexOutOfBoundsException();
				}
				return start + step * index;
			}

			@Override
			public int size() {
				return size;
			}
		};
	}

	@VisibleForTesting
	static int ceilDivide(int p, int q) {
		return p / q + (p % q == 0 ? 0 : 1);
	}

	public static String slice(String str, Integer start, Integer stop,
			Integer step) {
		StringBuilder builder = new StringBuilder();
		for (int i : sliceRange(start, stop, step, str.length())) {
			builder.append(str.charAt(i));
		}
		return builder.toString();
	}

	public static <E> List<E> slice(final List<E> list, Integer start,
			Integer stop, Integer step) {
		return Lists.transform(sliceRange(start, stop, step, list.size()),
				new Function<Integer, E>() {
					@Override
					public E apply(Integer input) {
						return list.get(input);
					}
				});
	}

	private static List<Integer> sliceRange(Integer start, Integer stop,
			Integer step, int len) {
		int istep = step == null ? 1 : step;
		if (istep == 0) {
			throw new IllegalArgumentException();
		}
		int istart;
		int istop;
		if (istep > 0) {
			if (start == null) {
				istart = 0;
			} else {
				istart = start < 0 ? Math.max(len + start, 0) : start;
			}
			if (stop == null) {
				istop = len;
			} else {
				istop = Math.min(getIndex(stop, len), len);
			}
		} else {
			if (start == null) {
				istart = len - 1;
			} else {
				istart = Math.min(getIndex(start, len), len - 1);
			}
			if (stop == null) {
				istop = -1;
			} else {
				istop = stop < 0 ? Math.max(len + stop, -1) : stop;
			}
		}
		return range(istart, istop, istep);
	}

	@VisibleForTesting
	static List<Integer> strictSliceRange(Integer start, Integer stop,
			Integer step, int len) {
		int istep = step == null ? 1 : step;
		if (istep == 0) {
			throw new IllegalArgumentException();
		}
		int istart;
		int istop;
		if (start == null) {
			istart = istep > 0 ? 0 : len - 1;
		} else {
			istart = getIndex(start, len);
		}
		if (stop == null) {
			istop = istep > 0 ? len : -1;
		} else {
			istop = getIndex(stop, len);
		}
		return range(istart, istop, istep);
	}

	public static Object slice(Object seq,
			Integer start,
			Integer stop,
			Integer step) {
		if (seq instanceof String) {
			return slice((String) seq, start, stop, step);
		}
		if (seq instanceof List) {
			return slice((List<?>) seq, start, stop, step);
		}
		throw new IllegalArgumentException();
	}

	public static Object slice(Object seq, int start) {
		return slice(seq, start, null, null);
	}

	public static Object slice(Object seq, Integer start, Integer end) {
		return slice(seq, start, end, null);
	}

	public static String slice(String str, int start) {
		return slice(str, start, null, null);
	}

	public static String slice(String str, Integer start, Integer stop) {
		return slice(str, start, stop, null);
	}

	public static <E> List<E> slice(List<E> list, int start) {
		return slice(list, start, null, null);
	}

	public static <E> List<E> slice(List<E> list, Integer start, Integer stop) {
		return slice(list, start, stop, null);
	}

	public static Object index(Object indexable, Object index) {
		if (indexable instanceof Map) {
			return ((Map<?, ?>) indexable).get(toString(index));
		}
		if (indexable instanceof Bindings) {
			return ((Bindings) indexable).resolve(toString(index));
		}
		return index(indexable, toNumber(index).intValue());
	}

	public static Object index(Object seq, int index) {
		if (seq instanceof String) {
			return index((String) seq, index);
		}
		if (seq instanceof List) {
			return index((List<?>) seq, index);
		}
		throw new TemplateParseException("%s is not an index in %s", index, seq);
	}

	public static String index(String str, int i) {
		return String.valueOf(str.charAt(getIndex(i, str.length())));
	}

	public static <E> E index(List<E> list, int i) {
		return list.get(getIndex(i, list.size()));
	}

	private static int getIndex(int i, int len) {
		return i < 0 ? len + i : i;
	}

	private static final Ordering<Object> ORDER = new Ordering<Object>() {

		@Override
		public int compare(Object left, Object right) {
			return Values.compare(left, right);
		}
	};

	public static Object min(Iterable<?> seq) {
		return ORDER.min(seq);
	}

	public static Object max(Iterable<?> seq) {
		return ORDER.max(seq);
	}

}
