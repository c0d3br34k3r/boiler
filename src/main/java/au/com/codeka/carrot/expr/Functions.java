package au.com.codeka.carrot.expr;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import au.com.codeka.carrot.ValueHelper;

public class Functions {

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
	static List<Integer> strictSliceRange(Integer start, Integer stop, Integer step, int len) {
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

	public static Object index(Object seq, int i) {
		if (seq instanceof String) {
			return index((String) seq, i);
		}
		if (seq instanceof List) {
			return index((List<?>) seq, i);
		}
		throw new IllegalArgumentException();
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

	public static Object min(Collection<?> seq) {
		Iterator<?> iter = seq.iterator();
		Number min = (Number) iter.next();
		while (iter.hasNext()) {
			Number next = (Number) iter.next();
			if (ValueHelper.compare(next, min) < 0) {
				min = next;
			}
		}
		return min;
	}

	public static Object max(Collection<?> seq) {
		Iterator<?> iter = seq.iterator();
		Number min = (Number) iter.next();
		while (iter.hasNext()) {
			Number next = (Number) iter.next();
			if (ValueHelper.compare(next, min) > 0) {
				min = next;
			}
		}
		return min;
	}

}
