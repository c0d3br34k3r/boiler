package au.com.codeka.carrot.expr;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
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
		if (step == null) {
			buildSliceAsc(seq, start, stop, 1);
		} else if (step > 0) {
			buildSliceAsc(seq, start, stop, step);
		} else if (step < 0) {
			buildSliceDesc(seq, start, stop, step);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static void buildSliceAsc(Sequence seq,
			Integer start,
			Integer stop,
			int step) {
		int istart;
		int istop;
		if (start == null) {
			istart = 0;
		} else {
			istart = start < 0 ? Math.max(seq.len() + start, 0) : start;
		}
		if (stop == null) {
			istop = seq.len();
		} else {
			istop = Math.min(stop < 0 ? seq.len() + stop : stop, seq.len());
		}
		for (int i = istart; i < istop; i += step) {
			seq.add(i);
		}
	}

	private static void buildSliceDesc(Sequence seq,
			Integer start,
			Integer stop,
			int step) {
		int istart;
		int istop;
		if (start == null) {
			istart = seq.len() - 1;
		} else {
			istart = Math.min(start < 0 ? seq.len() + start : start, seq.len() - 1);
		}
		if (stop == null) {
			istop = -1;
		} else {
			istop = stop < 0 ? Math.max(seq.len() + stop, -1) : stop;
		}
		for (int i = istart; i > istop; i += step) {
			seq.add(i);
		}
	}

	private interface Sequence {

		int len();

		void add(int index);
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

}
