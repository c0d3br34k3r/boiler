package com.catascopic.template.value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.catascopic.template.Context;
import com.catascopic.template.NullContext;
import com.catascopic.template.TemplateRenderException;
import com.catascopic.template.TrackingReader;
import com.catascopic.template.expr.Tokenizer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.google.common.math.IntMath;

public final class Values {

	private Values() {}

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
		if (value instanceof Iterable) {
			return !((Iterable<?>) value).iterator().hasNext();
		}
		return value != null;
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
		throw new TemplateRenderException("cannot convert <%s> (%s) to a number",
				value, value.getClass().getName());
	}

	private static Number tryConvertNumber(Object value) {
		if (value instanceof Number) {
			return (Number) value;
		}
		if (value instanceof Boolean) {
			return (Boolean) value ? 1 : 0;
		}
		if (value instanceof String) {
			String str = (String) value;
			try {
				if (str.indexOf('.') != -1) {
					return Double.parseDouble(str);
				}
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
				// continue
			}
		}
		return null;
	}

	private static Number parseNumber(String value) {
		try {
			if (value.contains(".")) {
				return Double.parseDouble(value);
			}
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new TemplateRenderException(e, "cannot convert <%s> (%s) to a number",
					value, value.getClass().getName());
		}
	}

	public static Number negate(Object value) {
		Number num = toNumber(value);
		if (num instanceof Integer) {
			return -num.intValue();
		}
		return -num.doubleValue();
	}

	public static Number invert(Object value) {
		return ~toNumber(value).intValue();
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
		} else if (o1 instanceof Iterable && o2 instanceof Iterable) {
			return Iterables.concat((Iterable<?>) o1, (Iterable<?>) o2);
		}
		// TODO: should we be able to add things that aren't strings?
		return String.valueOf(o1) + o2;
	}

	public static Number add(Number n1, Number n2) {
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() + n2.intValue();
		}
		return n1.doubleValue() + n2.doubleValue();
	}

	public static Object multiply(Object o1, Object o2) {
		Number n1 = tryConvertNumber(o1);
		Number n2 = tryConvertNumber(o2);
		if (n1 == null) {
			return Strings.repeat(String.valueOf(o1), n2.intValue());
		}
		if (n2 == null) {
			return Strings.repeat(String.valueOf(o2), n1.intValue());
		}
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() * n2.intValue();
		}
		return n1.doubleValue() * n2.doubleValue();
	}

	public static Number divide(Object o1, Object o2) {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() / n2.intValue();
		}
		return n1.doubleValue() / n2.doubleValue();
	}

	public static Number power(Object o1, Object o2) {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return IntMath.pow(n1.intValue(), n2.intValue());
		}
		return Math.pow(n1.doubleValue(), n2.doubleValue());
	}

	public static Number modulo(Object o1, Object o2) {
		Number n1 = toNumber(o1);
		Number n2 = toNumber(o2);
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return n1.intValue() % n2.intValue();
		}
		return n1.doubleValue() % n2.doubleValue();
	}

	public static Number abs(Number num) {
		if (num instanceof Integer) {
			return Math.abs(num.intValue());
		}
		return Math.abs(num.doubleValue());
	}

	public static Iterable<?> toIterable(Object iterable) {
		if (iterable instanceof Iterable) {
			return (Iterable<?>) iterable;
		}
		if (iterable instanceof String) {
			return new StringAsList((String) iterable);
		}
		throw new TemplateRenderException("<%s> (%s) is not iterable", iterable,
				iterable.getClass().getName());
	}

	public static boolean equal(Object o1, Object o2) {
		// allow for int and double with equal value
		if (o1 instanceof Number && o2 instanceof Number) {
			return compare((Number) o1, (Number) o2) == 0;
		}
		return Objects.equals(o1, o2);
	}

	public static int compare(Object o1, Object o2) {
		return compare(toNumber(o1), toNumber(o2));
	}

	public static int compare(Number n1, Number n2) {
		if (n1 instanceof Integer && n2 instanceof Integer) {
			return Integer.compare(n1.intValue(), n2.intValue());
		}
		return Double.compare(n1.doubleValue(), n2.doubleValue());
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
			String part = parts.next();
			result.append(Character.toUpperCase(part.charAt(0)))
					.append(part.substring(1).toLowerCase());
		}
		return result.toString();
	}

	public static String capitalize(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
	}

	public static int len(Object obj) {
		if (obj instanceof String) {
			return ((String) obj).length();
		}
		if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		}
		if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		}
		throw new TemplateRenderException("<%s> (%s) does not have a length",
				obj, obj.getClass().getName());
	}

	public static Object indexOf(Object seq, Object item) {
		if (seq instanceof String) {
			return ((String) seq).indexOf(requireString(item));
		}
		if (seq instanceof List) {
			return ((List<?>) seq).indexOf(item);
		}
		throw notSequence(seq);
	}

	public static Object indexOf(Object seq, Object item, int fromIndex) {
		if (seq instanceof String) {
			return ((String) seq).indexOf(requireString(item), fromIndex);
		}
		if (seq instanceof List) {
			List<?> list = (List<?>) seq;
			return list.subList(fromIndex, list.size()).indexOf(item);
		}
		throw notSequence(seq);
	}

	public static Object lastIndexOf(Object seq, Object item) {
		if (seq instanceof String) {
			return ((String) seq).lastIndexOf(requireString(item));
		}
		if (seq instanceof List) {
			return ((List<?>) seq).lastIndexOf(item);
		}
		throw notSequence(seq);
	}

	public static Object lastIndexOf(Object seq, Object item, int fromIndex) {
		if (seq instanceof String) {
			return ((String) seq).lastIndexOf(requireString(item), fromIndex);
		}
		if (seq instanceof List) {
			return ((List<?>) seq).subList(0, fromIndex).lastIndexOf(item);
		}
		throw notSequence(seq);
	}

	private static String requireString(Object item) {
		if (!(item instanceof String)) {
			throw new TemplateRenderException("<%s> (%s) is not a String",
					item, item.getClass().getName());
		}
		return (String) item;
	}

	private static TemplateRenderException notSequence(Object obj) {
		return new TemplateRenderException("<%s> (%s) is not a sequence",
				obj, obj.getClass().getName());
	}

	public static List<Integer> range(int stop) {
		return range(0, stop);
	}

	public static List<Integer> range(int start, int stop) {
		return range(start, stop, 1);
	}

	public static List<Integer> range(int start, int stop, int step) {
		return new Range(start, Math.max(ceilDivide(stop - start, step), 0), step);
	}

	@VisibleForTesting
	static int ceilDivide(int p, int q) {
		return p / q + (p % q == 0 ? 0 : 1);
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

	public static String slice(String str, Integer start, Integer stop, Integer step) {
		StringBuilder builder = new StringBuilder();
		for (int i : sliceRange(start, stop, step, str.length())) {
			builder.append(str.charAt(i));
		}
		return builder.toString();
	}

	public static <E> List<E> slice(final List<E> list, Integer start, Integer stop, Integer step) {
		return Lists.transform(sliceRange(start, stop, step, list.size()),
				new Function<Integer, E>() {

					@Override
					public E apply(Integer input) {
						return list.get(input);
					}
				});
	}

	private static List<Integer> sliceRange(Integer start, Integer stop, Integer step, int len) {
		int actualStep = step == null ? 1 : step;
		if (actualStep == 0) {
			throw new TemplateRenderException("step cannot be 0");
		}
		int actualStart;
		int actualStop;
		if (actualStep > 0) {
			if (start == null) {
				actualStart = 0;
			} else {
				actualStart = start < 0 ? Math.max(len + start, 0) : start;
			}
			if (stop == null) {
				actualStop = len;
			} else {
				actualStop = Math.min(getSliceIndex(stop, len), len);
			}
		} else {
			if (start == null) {
				actualStart = len - 1;
			} else {
				actualStart = Math.min(getSliceIndex(start, len), len - 1);
			}
			if (stop == null) {
				actualStop = -1;
			} else {
				actualStop = stop < 0 ? Math.max(len + stop, -1) : stop;
			}
		}
		return range(actualStart, actualStop, actualStep);
	}

	private static int getSliceIndex(int index, int len) {
		return index < 0 ? len + index : index;
	}

	public static Object slice(Object seq, Integer start, Integer stop, Integer step) {
		if (seq instanceof String) {
			return slice((String) seq, start, stop, step);
		}
		if (seq instanceof List) {
			return slice((List<?>) seq, start, stop, step);
		}
		throw new TemplateRenderException("<%s> (%s) is not indexable",
				seq, seq.getClass().getName());
	}

	public static Object index(Object indexable, Object index) {
		if (indexable instanceof Map) {
			return ((Map<?, ?>) indexable).get(String.valueOf(index));
		}
		// TODO: alternate indexable interface?
		return index(indexable, toNumber(index).intValue());
	}

	public static Object index(Object seq, int index) {
		if (seq instanceof List) {
			return index((List<?>) seq, index);
		}
		if (seq instanceof String) {
			return index((String) seq, index);
		}
		throw new TemplateRenderException("<%s> (%s) is not indexable",
				seq, seq.getClass().getName());
	}

	public static String index(String str, int index) {
		return String.valueOf(str.charAt(getIndex(index, str.length())));
	}

	public static <E> E index(List<E> list, int index) {
		return list.get(getIndex(index, list.size()));
	}

	public static int getIndex(int index, int len) {
		int adjusted = index < 0 ? len + index : index;
		if (adjusted < 0 || adjusted >= len) {
			throw new TemplateRenderException("index %s is out of bounds", index);
		}
		return adjusted;
	}

	public static String pad(String str, int minLength, char padChar, boolean left) {
		int padCount = minLength - str.length();
		if (padCount <= 0) {
			return str;
		}
		char[] buf = new char[minLength];
		if (left) {
			Arrays.fill(buf, 0, padCount, padChar);
			str.getChars(0, str.length(), buf, padCount);
		} else {
			str.getChars(0, str.length(), buf, 0);
			Arrays.fill(buf, str.length(), minLength, padChar);
		}
		return new String(buf);
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

	public static Number sum(Iterable<?> seq) {
		double sum = 0.0;
		for (Object value : seq) {
			Number n = tryConvertNumber(value);
			if (n == null) {
				throw new TemplateRenderException("cannot convert <%s> (%s) to a number",
						value, value.getClass().getName());
			}
			sum += n.doubleValue();
		}
		return tryConvertToInt(sum);
	}

	public static Number tryConvertToInt(double d) {
		if (Math.rint(d) == d) {
			return (int) d;
		}
		return d;
	}

	public static Number tryConvertToInt(Number n) {
		if (n instanceof Integer) {
			return n;
		}
		return tryConvertToInt(n.doubleValue());
	}

	public static Iterable<List<?>> entries(Map<?, ?> map) {
		return Iterables.transform(map.entrySet(), ENTRIES);
	}

	private static final Function<Entry<?, ?>, List<?>> ENTRIES = new Function<Entry<?, ?>, List<
			?>>() {

		@Override
		public List<?> apply(Entry<?, ?> input) {
			return Arrays.asList(input.getKey(), input.getValue());
		}
	};

	public static Iterable<List<?>> enumerate(Iterable<?> iterable) {
		return new Enumeration(iterable);
	}

	public static Iterable<List<?>> stream(Iterable<?> iterable) {
		return new Stream(iterable);
	}

	public static Iterable<List<?>> zip(Iterable<?> iterable) {
		return new Zip(iterable);
	}

	public static Object eval(String expression) {
		return eval(expression, NullContext.CONTEXT);
	}

	public static Object eval(String expression, Context context) {
		return new Tokenizer(TrackingReader.create(new StringReader(expression)))
				.parseExpression().evaluate(context);
	}

	public static String uneval(Object obj) {
		if (obj instanceof String) {
			return escape((String) obj);
		}
		if (obj instanceof Iterable) {
			return uneval((Iterable<?>) obj);
		}
		if (obj instanceof Map) {
			return uneval((Map<?, ?>) obj);
		}
		if (obj instanceof Number || obj instanceof Boolean) {
			return obj.toString();
		}
		if (obj == null) {
			return "null";
		}
		throw new TemplateRenderException("<%s> (%s) has no evaluable string form",
				obj, obj.getClass().getName());
	}

	public static String uneval(Iterable<?> iterable) {
		Iterator<?> iter = iterable.iterator();
		if (!iter.hasNext()) {
			return "[]";
		}
		StringBuilder builder = new StringBuilder().append('[');
		for (;;) {
			builder.append(uneval(iter.next()));
			if (!iter.hasNext()) {
				return builder.append(']').toString();
			}
			builder.append(", ");
		}
	}

	public static String uneval(Map<?, ?> map) {
		if (map.isEmpty()) {
			return "{}";
		}
		Iterator<? extends Entry<?, ?>> iter = map.entrySet().iterator();
		StringBuilder builder = new StringBuilder().append('{');
		for (;;) {
			Entry<?, ?> entry = iter.next();
			Object key = entry.getKey();
			if (!(key instanceof String)) {
				throw new TemplateRenderException("key <%s> (%s) is not a String",
						key, key.getClass().getName());
			}
			builder.append(escape((String) key))
					.append(": ")
					.append(uneval(entry.getValue()));
			if (!iter.hasNext()) {
				return builder.append('}').toString();
			}
			builder.append(", ");
		}
	}

	private static final Escaper ESCAPER = Escapers.builder()
			.addEscape('\\', "\\\\")
			.addEscape('\r', "\\r")
			.addEscape('\n', "\\n")
			.addEscape('\t', "\\t")
			.addEscape('"', "\\\"")
			.build();

	public static String escape(String str) {
		return "\"" + ESCAPER.escape(str) + "\"";
	}

	public static Iterable<String> splitLines(String str) {
		// TODO: make this better but don't use regex
		BufferedReader reader = new BufferedReader(new StringReader(str));
		List<String> result = new ArrayList<>();
		for (;;) {
			String line;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			if (line == null) {
				return result;
			}
			result.add(line);
		}
	}

}
