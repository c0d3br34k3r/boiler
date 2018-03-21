package au.com.codeka.carrot.expr;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.catascopic.template.Scope;
import com.catascopic.template.expr.Term;
import com.catascopic.template.expr.Tokenizer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Tests for {@link StatementParserTest}.
 */
@RunWith(JUnit4.class)
public class StatementParserTest {

	@Test
	public void testBinaryOperation() {

		Assert.assertEquals(evaluate("+true"), 1);
		Assert.assertEquals(evaluate("1+1"), 2);
		Assert.assertEquals(evaluate("1+1+1"), 3);
		Assert.assertEquals(evaluate("1+1+1+1"), 4);
		Assert.assertEquals(evaluate("1"), 1);
		Assert.assertEquals(evaluate("!1"), false);
		Assert.assertEquals(evaluate("!!1"), true);
		Assert.assertEquals(evaluate("!!!1"), false);
		Assert.assertEquals(evaluate("2 + 2 * 2"), 6);
		Assert.assertEquals(evaluate("2 * 2 + 2"), 6);
		Assert.assertEquals(evaluate("2 * (2 + 2)"), 8);

		Assert.assertEquals(evaluate("foo[4 + 4]"), 7);
		Assert.assertEquals(evaluate("foo[6] * 2"), 14);
		Assert.assertEquals(evaluate("bar.baz"), "qux");
		Assert.assertEquals(evaluate("quux.quuz.corge"), "grault");
		Assert.assertEquals(evaluate("quux['qu' + 'uz']['corge']"), "grault");
		Assert.assertEquals(evaluate("quux['quuz']['garply'][0]"), 3);
		Assert.assertEquals(evaluate("'ab\\\"'"), "ab\"");

		Assert.assertEquals(evaluate("R[0]"), 1);
		Assert.assertEquals(evaluate("R[1]"), 2);
		Assert.assertEquals(evaluate("R[-1]"), 7);
		Assert.assertEquals(evaluate("R[-2]"), 6);

		Assert.assertEquals(evaluate("word[3]"), "o");
		Assert.assertEquals(evaluate("word[:]"), "automobile");
		Assert.assertEquals(evaluate("word[4:]"), "mobile");
		Assert.assertEquals(evaluate("word[:-3]"), "automob");
		Assert.assertEquals(evaluate("word[1:5]"), "utom");
		Assert.assertEquals(evaluate("word[::]"), "automobile");
		Assert.assertEquals(evaluate("word[4::]"), "mobile");
		Assert.assertEquals(evaluate("word[:4:]"), "auto");
		Assert.assertEquals(evaluate("word[::-1]"), "elibomotua");
		Assert.assertEquals(evaluate("word[:5:2]"), "atm");
		Assert.assertEquals(evaluate("word[1:5:]"), "utom");
		Assert.assertEquals(evaluate("word[2::3]"), "tol");
		Assert.assertEquals(evaluate("word[1:-1:2]"), "uooi");

		Assert.assertEquals(evaluate("word[4:][:3]"), "mob");
		Assert.assertEquals(evaluate("word[:]+'y'"), "automobiley");
		Assert.assertEquals(evaluate("word[:-4] + 'bus'"), "automobus");
		Assert.assertEquals(evaluate("(word)"), "automobile");
		Assert.assertEquals(evaluate("(word)[7]"), "i");
		Assert.assertEquals(evaluate("word[-2]"), "l");

		Assert.assertEquals(evaluate("'1' + 1"), 2);
		Assert.assertEquals(evaluate("'foo' + 'bar'"), "foobar");
		Assert.assertEquals(evaluate("true + 'bar'"), "truebar");
		Assert.assertEquals(evaluate("true + 9"), 10);

		Assert.assertEquals(evaluate("1 ? 4 : 5"), 4);
		Assert.assertEquals(evaluate("false ? 4 : 2 * 6"), 12);
		Assert.assertEquals(evaluate("i ? 4 : 5"), 4);
		Assert.assertEquals(evaluate("i - 6 ? 2 + 5 : 3 * 3"), 9);
		Assert.assertEquals(evaluate("1 + (-1 ? 2 + 5 : 3 * 3)"), 8);
		Assert.assertEquals(evaluate("1 + -1 ? 2 + 5 : 3 * 3"), 9);
		Assert.assertEquals(evaluate("true ? false ? 1 : 2 : 3"), 2);
		Assert.assertEquals(evaluate("(true ? 0 : 1) ? 4 : 5"), 5);

		Assert.assertEquals(evaluate("'auto'[1]"), "u");
		Assert.assertEquals(evaluate("('auto')[1]"), "u");
		Assert.assertEquals(evaluate("('foo' + 'bar')[4]"), "a");
		Assert.assertEquals(evaluate("[1, 'foo', 3]"), Arrays.asList(1, "foo", 3));
		Assert.assertEquals(evaluate("[1, 2, 3][1]"), 2);
		Assert.assertEquals(evaluate("{alpha: 1, beta: 2}"),
				ImmutableMap.of("alpha", 1, "beta", 2));
		Assert.assertEquals(evaluate("{alpha: 1, beta: 2}.beta"), 2);
		Assert.assertEquals(evaluate("{alpha: 1, beta: 2}['alpha']"), 1);
		Assert.assertEquals(evaluate("{a: 1, b: [true, false][1] ? 4 : 7}['b']"), 7);

		Assert.assertEquals(evaluate("len('abcde')"), 5);
		Assert.assertEquals(evaluate("len([1, 'foo', []]) * 7"), 21);
		Assert.assertEquals(evaluate("range(5)"), Arrays.asList(0, 1, 2, 3, 4));
		Assert.assertEquals(evaluate("max(range(5))"), 4);
		Assert.assertEquals(evaluate("max(7 * 13, 100)"), 100);
		Assert.assertEquals(evaluate("upper('hello')"), "HELLO");
		Assert.assertEquals(evaluate("join([1, 2, 3], ' to the ')"), "1 to the 2 to the 3");
		Assert.assertEquals(evaluate("join('hello', '.')"), "h.e.l.l.o");
	}

	@Test
	public void testSyntaxError() {
		syntaxError("");
		syntaxError("(1)[0]");
		syntaxError("'abc'[:::]");
		syntaxError("'abc'[::");
		syntaxError("'abc'[1 2]");
		syntaxError("'abc'[1 2 3]");
		syntaxError("'abc'[1:2 3]");
		syntaxError("'abc'[1 2:3]");
		syntaxError("'abc'[1:2:3:]");
		syntaxError("'abc'[1:2:3:4]");
		syntaxError("1 +");
		syntaxError("1 1");
		syntaxError("1 &! 1");
		syntaxError("max('a', 'b')");
	}

	private static void syntaxError(String expr) {
		try {
			evaluate(expr);
			Assert.fail(expr + " was valid");
		} catch (Exception e) {
			System.out.printf("%-24s %s (%s)%n", expr, e.getMessage(), e.getClass()
					.getSimpleName());
		}
	}

	private static Object evaluate(String expr) {
		Tokenizer tokenizer = createParser(expr);
		Term term;
		term = tokenizer.parseExpression();
		tokenizer.end();
		System.out.printf("%-24s %s%n", expr, term);
		return evaluate(term);
	}

	private static Tokenizer createParser(String str) {
		return new Tokenizer(new PushbackReader(new StringReader(str)), Tokenizer.Mode.STREAM);
	}

	private static Object evaluate(Term term) {
		// System.out.println(term);
		return term.evaluate(
				new Scope(ImmutableMap.<String, Object> builder()
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

}
