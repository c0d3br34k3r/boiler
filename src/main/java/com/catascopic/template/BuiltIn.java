package com.catascopic.template;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.catascopic.template.value.Values;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * The built-in functions.
 */
public enum BuiltIn implements TemplateFunction {

	/**
	 * <code>bool(x)</code>
	 * <p>
	 * Returns the boolean value of its argument. 0, 0.0, false, null,
	 * <code>''</code>, <code>[]</code>, and <code>{}</code> are considered
	 * false and all other values are considered true.
	 */
	BOOL {

		@Override
		public Object apply(Params params) {
			return params.getBoolean(0);
		}
	},

	/**
	 * <code>float(x)</code>
	 * <p>
	 * Converts a numeric type to a floating point number (the double type in
	 * Java).
	 */
	FLOAT {

		@Override
		public Object apply(Params params) {
			return params.getDouble(0);
		}
	},

	/**
	 * <code>int(x)</code>
	 * <p>
	 * Converts a numeric type or boolean to an integer (the int type in Java).
	 * The integer equivalent of a boolean value is 1 for true and 0 for false.
	 */
	INT {

		@Override
		public Object apply(Params params) {
			return params.getInt(0);
		}
	},

	/**
	 * <code>str(x)</code>
	 * <p>
	 * Converts a value to a string by invoking its toString method.
	 */
	STR {

		@Override
		public Object apply(Params params) {
			return params.getString(0);
		}
	},

	/**
	 * <code>len(x)</code>
	 * <p>
	 * Returns the length of a string, the size of a list, or the number of
	 * entries in a map.
	 */
	LEN {

		@Override
		public Object apply(Params params) {
			return Values.len(params.get(0));
		}
	},

	/**
	 * <code>min(seq)</code><br>
	 * OR<br>
	 * <code>min(num*)</code>
	 * <p>
	 * Returns the smallest numeric value in a sequence. This method accepts
	 * either a sequence as a single argument, or two or more numeric arguments.
	 */
	MIN {

		@Override
		public Object apply(Params params) {
			return Values.min(params.size() == 1 ? params.getIterable(0) : params.asList());
		}
	},

	/**
	 * <code>max(seq)</code><br>
	 * OR<br>
	 * <code>max(num*)</code>
	 * <p>
	 * Returns the largest numeric value in a sequence. This method accepts
	 * either a sequence as a single argument, or two or more numeric arguments.
	 */
	MAX {

		@Override
		public Object apply(Params params) {
			return Values.max(params.size() == 1 ? params.getIterable(0) : params.asList());
		}
	},

	/**
	 * <code>abs(num)</code>
	 * <p>
	 * Returns the absolute value of a numeric value.
	 */
	ABS {

		@Override
		public Object apply(Params params) {
			return Values.abs(params.getNumber(0));
		}
	},

	/**
	 * <code>sqrt(num)</code>
	 * <p>
	 * Returns the square root of a numeric value. Always returns a
	 * floating-point value.
	 */
	SQRT {

		@Override
		public Object apply(Params params) {
			return Math.sqrt(params.getNumber(0).doubleValue());
		}
	},

	/**
	 * <code>range(stop)</code><br>
	 * OR<br>
	 * <code>range(start, stop, step?)</code>
	 * <p>
	 * Returns a sequence of integers from a starting point, inclusive, up to a
	 * stopping point, exclusive, counting by a step value. If one argument is
	 * passed, 0 is the start, the given value is the end, and the step is 1.
	 * Otherwise, the given values are the start, stop, and (optional) step.
	 * This functions identically to the range built-in type in Python 3.
	 */
	RANGE {

		@Override
		public Object apply(Params params) {
			switch (params.size()) {
			case 0:
				throw new TemplateRenderException("range must have at least 1 param");
			case 1:
				return Values.range(params.getInt(0));
			default:
				return Values.range(params.getInt(0), params.getInt(1), params.getInt(2, 1));
			}
		}
	},

	/**
	 * <code>enumerate(seq)</code>
	 * <p>
	 * Converts a sequence to a new sequence of pairs, whose first element is
	 * the index within the sequence, and whose second element is the item from
	 * the original sequence.
	 * <p>
	 * For example, <code>enumerate(['a', 'b', 'c'])</code> returns <code>[[0,
	 * 'a'], [1, 'b'], [2, 'c']]</code>.
	 * <p>
	 * This functions identically to the enumerate function in Python 3.
	 */
	ENUMERATE {

		@Override
		public Object apply(Params params) {
			return Values.enumerate(params.getIterable(0));
		}
	},

	/**
	 * <code>stream(seq)</code>
	 * <p>
	 * Similar to <code>enumerate</code>, returns a sequence of triples
	 * consisting of the index of the item within the sequence, a boolean
	 * indicating whether the item is the last in the sequence, and the item
	 * itself.
	 * <p>
	 * For example, <code>zip(['a', 'b', 'c'])</code> returns
	 * <code>[[0, false, 'a'], [1, false, 'b'], [2, true, 'c']]</code>.
	 */
	STREAM {

		@Override
		public Object apply(Params params) {
			return Values.stream(params.getIterable(0));
		}
	},

	/**
	 * <code>zip(seq)</code>
	 * <p>
	 * Returns a sequence that aggregates elements from two or more sequences.
	 * The nth element of the resulting sequence is a list of the elements in
	 * the nth positions in each sequences passed in. If the sequences are of
	 * different lengths, the resulting sequence is as long as the shortest one.
	 * <p>
	 * For example, <code>zip(['a', 'b', 'c'], ['d', 'e', 'f'])</code> returns
	 * <code>[['a', 'd'], ['b', 'e'],
	 * ['c', 'f']]</code>.
	 * <p>
	 * This functions identically to the zip function in Python 3.
	 */
	ZIP {

		@Override
		public Object apply(Params params) {
			return Values.zip(params.size() == 1 ? params.getIterable(0) : params.asList());
		}
	},

	/**
	 * <code>keys(map)</code>
	 * <p>
	 * Returns a sequence of the keys in the map.
	 */
	KEYS {

		@Override
		public Object apply(Params params) {
			return params.getMap(0).keySet();
		}
	},

	/**
	 * <code>values(map)</code>
	 * <p>
	 * Returns a sequence of the values in the map.
	 */
	VALUES {

		@Override
		public Object apply(Params params) {
			return params.getMap(0).values();
		}
	},

	/**
	 * <code>entries(map)</code>
	 * <p>
	 * Returns a sequence of the values in the map.
	 */
	ENTRIES {

		@Override
		public Object apply(Params params) {
			return Values.entries(params.getMap(0));
		}
	},

	/**
	 * <code>contains(str, substr)</code><br>
	 * OR<br>
	 * <code>contains(seq, x)</code>
	 * <p>
	 * Returns true if the given string contains the given substring, or if the
	 * given sequence contains the given value.
	 */
	CONTAINS {

		@Override
		public Object apply(Params params) {
			Object seq = params.get(0);
			if (seq instanceof String) {
				return ((String) seq).contains(params.getString(1));
			}
			if (seq instanceof Collection) {
				return ((Collection<?>) seq).contains(params.get(1));
			}
			throw new TemplateRenderException("%s (%s) is not a container",
					seq, seq.getClass().getName());
		}
	},

	/**
	 * <code>capitalize(str)</code>
	 * <p>
	 * Returns a string with the first character of the given string in upper
	 * case, and the rest in lower case.
	 */
	CAPITALIZE {

		@Override
		public Object apply(Params params) {
			return Values.capitalize(params.getString(0));
		}
	},

	/**
	 * <code>replace(str, find, replace)</code>
	 * <p>
	 * Returns a string with all instances of a given substring replaced with
	 * another string.
	 */
	REPLACE {

		@Override
		public Object apply(Params params) {
			return params.getString(0).replace(params.getString(1), params.getString(2));
		}
	},

	/**
	 * <code>startsWith(str, prefix)</code>
	 * <p>
	 * Returns true if the given string starts with the given substring.
	 */
	STARTS_WITH {

		@Override
		public Object apply(Params params) {
			return params.getString(0).startsWith(params.getString(1));
		}
	},

	/**
	 * <code>endsWith(str, suffix)<code>
	 * <p>
	 * Returns true if the given string ends with the given substring.
	 */
	ENDS_WITH {

		@Override
		public Object apply(Params params) {
			return params.getString(0).endsWith(params.getString(1));
		}
	},

	/**
	 * <code>indexOf(str, substr, index?)</code><br>
	 * OR<br>
	 * <code>indexOf(seq, x, index?)</code>
	 * <p>
	 * Returns the first index of a substring within a string, or an value
	 * within a sequence, searching forwards from the given index. Returns -1 if
	 * none was found. The value of the index defaults to 0.
	 */
	INDEX_OF {

		@Override
		public Object apply(Params params) {
			return Values.indexOf(params.get(0), params.get(1), params.getInt(2, 0));
		}
	},

	/**
	 * <code>lastIndexOf(str, substr, index?)</code><br>
	 * OR<br>
	 * <code>lastIndexOf(seq, x, index?)</code>
	 * <p>
	 * Returns the last index of a substring within a string, or an value within
	 * a sequence, searching backwards from the given index. Returns -1 if none
	 * was found. The value of the index defaults to the length of the string or
	 * sequence.
	 */
	LAST_INDEX_OF {

		@Override
		public Object apply(Params params) {
			if (params.size() <= 2) {
				return Values.lastIndexOf(params.get(0), params.get(1));
			}
			return Values.lastIndexOf(params.get(0), params.get(1), params.getInt(2));
		}
	},

	/**
	 * <code>join(seq, separator)</code>
	 * <p>
	 * Returns a string that concatenates the values in the sequence with
	 * interleaving occurences of the separator.
	 */
	JOIN {

		@Override
		public Object apply(Params params) {
			return Joiner.on(params.getString(1)).join(Values.toIterable(params.get(0)));
		}
	},

	/**
	 * <code>split(str, separator)</code>
	 * <p>
	 * Returns a sequence containing the substrings of the given string that
	 * occur between occurences of the separator.
	 */
	SPLIT {

		@Override
		public Object apply(Params params) {
			return Splitter.on(params.getString(1)).splitToList(params.getString(0));
		}
	},

	/**
	 * <code>splitLines(str)</code>
	 * <p>
	 * Returns a sequence containing the lines (separated by LF or CRLF) of the
	 * given string.
	 */
	SPLIT_LINES {

		@Override
		public Object apply(Params params) {
			return Values.splitLines(params.getString(0));
		}
	},

	/**
	 * <code>upper(str)</code>
	 * <p>
	 * Returns a copy of the given string with all letters in upper case.
	 */
	UPPER {

		@Override
		public Object apply(Params params) {
			return params.getString(0).toUpperCase();
		}
	},

	/**
	 * <code>lower(str)</code>
	 * <p>
	 * Returns a copy of the given string with all letters in lower case.
	 */
	LOWER {

		@Override
		public Object apply(Params params) {
			return params.getString(0).toLowerCase();
		}
	},

	/**
	 * <code>trim(str)</code>
	 * <p>
	 * Returns a copy of the given string with all whitespace at the beginning
	 * and end removed.
	 */
	TRIM {

		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimFrom(params.getString(0));
		}
	},

	/**
	 * <code>collapse(str, replaceChar?)</code>
	 * <p>
	 * Like trim, but also reduces all subsequences of whitespace not at the
	 * beginning or end with the replacement character. By default, the value of
	 * <code>replaceChar</code> is <code>' '</code>.
	 */
	COLLAPSE {

		@Override
		public Object apply(Params params) {
			return CharMatcher.whitespace().trimAndCollapseFrom(
					params.getString(0), params.getChar(1, " "));
		}
	},

	/**
	 * <code>separatorToCamel(str, separator?)</code>
	 * <p>
	 * Converts a string in a separator format to lower camel case. For example,
	 * <code>separatorToCamel('big-blue-dog', '-')</code> returns
	 * <code>'bigBlueDog'</code>. The default value of <code>separator</code> is
	 * <code>'_'</code>.
	 */
	SEPARATOR_TO_CAMEL {

		@Override
		public Object apply(Params params) {
			return Values.separatorToCamel(params.getString(0), params.getString(1, "_"));
		}
	},

	/**
	 * <code>camelToSeparator(str, separator?)</code>
	 * <p>
	 * Converts a string in lower camel case to a separator format. For example,
	 * <code>camelToSeparator('bigBlueDog')</code> returns
	 * <code>'big_blue_dog'</code>. The default value of <code>separator</code>
	 * is <code>'_'</code>.
	 */
	CAMEL_TO_SEPARATOR {

		@Override
		public Object apply(Params params) {
			return Values.camelToSeparator(params.getString(0), params.getString(1, "_"));
		}
	},

	/**
	 * <code>pad(str, length, padChar?, align?)</code>
	 * <p>
	 * Returns a string padded to the given length using repetitions of the
	 * given character. A true alignment value specifies left-aligned padding;
	 * false is right-aligned. The default value of <code>padChar</code> is
	 * <code>' '</code> and the default value of <code>align</code> is true.
	 * <p>
	 * Examples:
	 * <ol>
	 * <li><code>pad('cat', 5)</code> returns <code>'  cat'</code>
	 * <li><code>pad(42, 4, '0')<code> returns <code>'0042'</code>
	 * <li><code>pad('dog', 6, '*', false)</code> returns 'dog***'</code>
	 * </ol>
	 */
	PAD {

		@Override
		public Object apply(Params params) {
			return Values.pad(params.getString(0), params.getInt(1),
					params.getChar(2, " "), params.getBoolean(3, true));
		}
	},

	/**
	 * <code>template(filePath, paramMap?)</code>
	 * <p>
	 * Evaluates a template at the specified path, which may be an absolute
	 * path, or relative to the location of the current template. That
	 * template's scope has access to all local variables defined in the current
	 * scope, as well as any variables defined by paramMap. Any variables set by
	 * the called template (including those in the map) will not affect the
	 * value of the variables in the current scope. The value of
	 * <code>paramMap</code> defaults to <code>{}</code>.
	 */
	TEMPLATE {

		@Override
		public Object apply(Params params) {
			final Map<String, ?> map = params.getMap(1, Collections.<String, Object> emptyMap());
			try {
				return params.scope().renderTemplate(params.getString(0), map);
			} catch (IOException e) {
				throw new TemplateRenderException(e);
			}
		}
	},

	/**
	 * <code>textFile(filePath)</code>
	 * <p>
	 * Returns the text content of the file at the specified path, which may be
	 * an absolute path, or relative to the location of the current template.
	 */
	TEXT_FILE {

		@Override
		public Object apply(Params params) {
			try {
				return params.scope().renderTextFile(params.getString(0));
			} catch (IOException e) {
				throw new TemplateRenderException(e);
			}
		}
	},

	/**
	 * <code>locals()</code>
	 * <p>
	 * Returns a map of all local variables in the current scope, associating
	 * each variable name with its current value.
	 */
	LOCALS {

		@Override
		public Object apply(Params params) {
			return params.scope().locals();
		}
	},

	/**
	 * <code>eval(expression)</code>
	 * <p>
	 * Evaluates the string as an expression, using the same parsing syntax as a
	 * regular evaluable segment. The expression may reference variables in the
	 * current scope.
	 */
	EVAL {

		@Override
		public Object apply(Params params) {
			return Values.eval(params.getString(0), params.scope());
		}
	},

	/**
	 * <code>uneval(x)</code>
	 * <p>
	 * Returns a JSON-compatible string representation of the value, which can
	 * be parsed back to an equivalent value using <code>eval</code>. The
	 * argument may be a boolean, string, number, list, or map (lists and maps
	 * must contain only these types as well).
	 */
	UNEVAL {

		@Override
		public Object apply(Params params) {
			return Values.uneval(params.get(0));
		}
	};

	// TODO: other possibilities:
	// isUpper
	// isLower
	// regexReplace
	// group
	// sum
	// floor/ceiling/round
	// stringCompare
	// substringBefore/substringAfter
	// date and time functions
	// distinctValues

}
