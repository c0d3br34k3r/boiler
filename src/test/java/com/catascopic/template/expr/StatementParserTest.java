package com.catascopic.template.expr;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.catascopic.template.PositionReader;
import com.catascopic.template.TestUtil;
import com.catascopic.template.Values;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(Parameterized.class)
public class StatementParserTest {

	@Parameter(0)
	public Object expected;

	@Parameter(1)
	public String expression;

	@Test
	public void testEval() {
		Assert.assertEquals(expected, evaluate(expression));
	}

	private static Object evaluate(String expr) {
		return evaluate(expr, true);
	}

	private static Object evaluate(String expr, boolean print) {
		Tokenizer tokenizer = createParser(expr);
		Term term;
		term = tokenizer.parseTerm();
		tokenizer.end();
		if (print) {
			System.out.printf("%-24s %s%n", expr, term);
		}
		return evaluate(term);
	}

	private static Tokenizer createParser(String str) {
		return new Tokenizer(new PositionReader(new StringReader(str)),
				Tokenizer.Mode.STREAM);
	}

	private static Object evaluate(Term term) {
		return term.evaluate(TestUtil.testScope(ImmutableMap
				.<String, Object> builder()
				.put("i", 6)
				.put("R", Arrays.asList(1, 2, 3, 4, 5, 6, 7))
				.put("word", "automobile")
				.put("foo", Collections.nCopies(10, 7))
				.put("bar", ImmutableMap.of("baz", "qux"))
				.put("quux", ImmutableMap.of("quuz", ImmutableMap.of(
						"corge", "grault",
						"garply", ImmutableList.of(3))))
				.build()));
	}

	@Parameters(name = "{1} = {0}")
	public static List<Object[]> params() {
		return Arrays.asList(new Object[][] {
			{ 1, "+true" },
			{ 2, "1+1" },
			{ 3, "1+1+1" },
			{ 4, "1+1+1+1" },
			{ 1, "1" },
			{ false, "!1" },
			{ true, "!!1" },
			{ false, "!!!1" },
			{ 6, "2 + 2 * 2" },
			{ 6, "2 * 2 + 2" },
			{ 8, "2 * (2 + 2)" },

			{ 7, "foo[4 + 4]" },
			{ 14, "foo[6] * 2" },
			{ "qux", "bar.baz" },
			{ "grault", "quux.quuz.corge" },
			{ "grault", "quux['qu' + 'uz']['corge']" },
			{ 3, "quux['quuz']['garply'][0]" },
			{ "ab\"", "'ab\\\"'" },

			{ 1, "R[0]" },
			{ 2, "R[1]" },
			{ 7, "R[-1]" },
			{ 6, "R[-2]" },

			{ "o", "word[3]" },
			{ "b", "word[i]" },
			{ "mobile", "word[4:]" },
			{ "automob", "word[:-3]" },
			{ "utom", "word[1:5]" },
			{ "elibomotua", "word[::-1]" },
			{ "atm", "word[:5:2]" },
			{ "tol", "word[2::3]" },
			{ "uooi", "word[1:-1:2]" },

			{ "mob", "word[4:][:3]" },
			{ "automobus", "word[:-4] + 'bus'" },
			{ "automobile", "(word)" },
			{ "i", "(word)[7]" },
			{ "l", "word[-2]" },

			{ 2, "'1' + 1" },
			{ "foobar", "'foo' + 'bar'" },
			{ "truebar", "true + 'bar'" },
			{ 10, "true + 9" },

			{ 4, "1 ? 4 : 5" },
			{ 12, "false ? 4 : 2 * 6" },
			{ 4, "i ? 4 : 5" },
			{ 9, "i - 6 ? 2 + 5 : 3 * 3" },
			{ 8, "1 + (-1 ? 2 + 5 : 3 * 3)" },
			{ 9, "1 + -1 ? 2 + 5 : 3 * 3" },
			{ 2, "true ? false ? 1 : 2 : 3" },
			{ 5, "(true ? 0 : 1) ? 4 : 5" },

			{ "u", "'auto'[1]" },
			{ "u", "('auto')[1]" },
			{ "a", "('foo' + 'bar')[4]" },
			{ Arrays.asList(1, "foo", 3),
				"[1, 'foo', 3]" },
			{ 2, "[1, 2, 3][1]" },
			{ ImmutableMap.of("alpha", 1, "beta", 2), "{alpha: 1, beta: 2}" },
			{ 2, "{alpha: 1, beta: 2}.beta" },
			{ 1, "{alpha: 1, beta: 2}['alpha']" },
			{ 7, "{a: 1, b: [true, false][1] ? 4 : 7}['b']" },

			{ true, "bool(256)" },
			{ false, "bool([])" },
			{ 5, "len('abcde')" },
			{ 21, "len([1, 'foo', []]) * 7" },
			{ 9, "indexOf('uncopyrightable', 'h')" },
			{ Values.range(5), "range(5)" },
			{ 4, "max(range(5))" },
			{ 100, "max(7 * 13, 100)" },
			{ "HELLO", "upper('hello')" },
			{ "1 to the 2 to the 3", "join([1, 2, 3], ' to the ')" },
			{ "h.e.l.l.o", "join('hello', '.')" },
			{ "this-is-a-test", "camelToSeparator('thisIsATest', '-')" },
			{ "this is a test", "collapse('  this \r\n is\ta   test ')" },

			{ 3, "6 / 2" },
			{ 1, "6 / 4" },
			{ 1.5, "6.0 / 4.0" },
			{ 1.5, "6 / 4.0" },
			{ 1.5, "6.0 / 4" },

			{ true, "1 < 2" },
			{ true, "2 > 1" },
			{ false, "1 > 1" },
			{ true, "1 >= 1" },
			{ false, "1 < 1" },
			{ true, "1 <= 1" },
			{ true, "1 > .999" },
			{ true, "1 < .999 + .01" },
			{ true, "1 == 1" },
			{ false, "1 != 1" },
			{ false, "'foo' != 'foo'" },
			{ true, "'foo' == 'foo'" },
			{ false, "'foo' == 'bar'" },
			{ true, "'foo' != 'bar'" },
			{ false, "true && false" },
			{ true, "true || false" },
			{ false, "false || false" },
			{ true, "true && true" },
			{ 1, "29 % 4" },

		});
	}

}
