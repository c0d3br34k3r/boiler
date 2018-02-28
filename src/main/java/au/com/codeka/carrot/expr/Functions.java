package au.com.codeka.carrot.expr;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

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

	public static Iterable<Integer> range(int stop) {
		return range(0, stop);
	}

	public static Iterable<Integer> range(int start, int stop) {
		return range(start, stop, 1);
	}

	public static List<Integer> range(final int start, int stop,
			final int step) {
		final int size = Math.max((stop - start) / step, 0);
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

	public static Object slice(Object seq, int start) {
		return slice(seq, start, null, null);
	}

	public static Object slice(Object seq, Integer start, Integer end) {
		return slice(seq, start, end, null);
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

	public static String slice(String str, int start) {
		return slice(str, start, null, null);
	}

	public static String slice(String str, Integer start, Integer stop) {
		return slice(str, start, stop, null);
	}

	public static String slice(final String str,
			Integer start,
			Integer stop,
			Integer step) {
		final StringBuilder builder = new StringBuilder();
		buildSlice(new Sequence() {

			@Override
			public int len() {
				return str.length();
			}

			@Override
			public void add(int index) {
				builder.append(str.charAt(index));
			}
		}, start, stop, step);
		return builder.toString();
	}

	public static <E> List<E> slice(List<E> list, int start) {
		return slice(list, start, null, null);
	}

	public static <E> List<E> slice(List<E> list, Integer start, Integer stop) {
		return slice(list, start, stop, null);
	}

	public static <E> List<E> slice(final List<E> list,
			Integer start,
			Integer stop,
			Integer step) {
		final List<E> builder = new ArrayList<>();
		buildSlice(new Sequence() {

			@Override
			public int len() {
				return list.size();
			}

			@Override
			public void add(int index) {
				builder.add(list.get(index));
			}
		}, start, stop, step);
		return builder;
	}

	private static void buildSlice(Sequence seq,
			Integer start,
			Integer stop,
			Integer step) {
		int istep = step == null ? 1 : step;
		if (istep == 0) {
			throw new IllegalArgumentException();
		}
		int istart;
		int istop;
		if (start == null) {
			istart = istep > 0 ? 0 : seq.len() - 1;
		} else {
			istart = start < 0 ? seq.len() + start : start;
		}
		if (stop == null) {
			istop = istep > 0 ? seq.len() : -1;
		} else {
			istop = stop < 0 ? seq.len() + stop : stop;
		}
		int cmp = -Integer.signum(istep);
		for (int i = istart; Integer.compare(i, istop) == cmp; i += istep) {
			seq.add(i);
		}
	}

	private interface Sequence {

		int len();

		void add(int index);
	}

}
