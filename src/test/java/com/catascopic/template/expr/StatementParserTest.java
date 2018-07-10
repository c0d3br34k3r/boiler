package com.catascopic.template.expr;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.catascopic.template.Scope;
import com.catascopic.template.TemplateParseException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnit4.class)
public class StatementParserTest {

	@Test
	public void testBinaryOperation() {

		Assert.assertEquals(1, evaluate("+true"));
		Assert.assertEquals(2, evaluate("1+1"));
		Assert.assertEquals(3, evaluate("1+1+1"));
		Assert.assertEquals(4, evaluate("1+1+1+1"));
		Assert.assertEquals(1, evaluate("1"));
		Assert.assertEquals(false, evaluate("!1"));
		Assert.assertEquals(true, evaluate("!!1"));
		Assert.assertEquals(false, evaluate("!!!1"));
		Assert.assertEquals(6, evaluate("2 + 2 * 2"));
		Assert.assertEquals(6, evaluate("2 * 2 + 2"));
		Assert.assertEquals(8, evaluate("2 * (2 + 2)"));

		Assert.assertEquals(7, evaluate("foo[4 + 4]"));
		Assert.assertEquals(14, evaluate("foo[6] * 2"));
		Assert.assertEquals("qux", evaluate("bar.baz"));
		Assert.assertEquals("grault", evaluate("quux.quuz.corge"));
		Assert.assertEquals("grault", evaluate("quux['qu' + 'uz']['corge']"));
		Assert.assertEquals(3, evaluate("quux['quuz']['garply'][0]"));
		Assert.assertEquals("ab\"", evaluate("'ab\\\"'"));

		Assert.assertEquals(1, evaluate("R[0]"));
		Assert.assertEquals(2, evaluate("R[1]"));
		Assert.assertEquals(7, evaluate("R[-1]"));
		Assert.assertEquals(6, evaluate("R[-2]"));

		Assert.assertEquals("o", evaluate("word[3]"));
		Assert.assertEquals("b", evaluate("word[i]"));
		Assert.assertEquals("mobile", evaluate("word[4:]"));
		Assert.assertEquals("automob", evaluate("word[:-3]"));
		Assert.assertEquals("utom", evaluate("word[1:5]"));
		Assert.assertEquals("elibomotua", evaluate("word[::-1]"));
		Assert.assertEquals("atm", evaluate("word[:5:2]"));
		Assert.assertEquals("tol", evaluate("word[2::3]"));
		Assert.assertEquals("uooi", evaluate("word[1:-1:2]"));

		Assert.assertEquals("mob", evaluate("word[4:][:3]"));
		Assert.assertEquals("automobus", evaluate("word[:-4] + 'bus'"));
		Assert.assertEquals("automobile", evaluate("(word)"));
		Assert.assertEquals("i", evaluate("(word)[7]"));
		Assert.assertEquals("l", evaluate("word[-2]"));

		Assert.assertEquals(2, evaluate("'1' + 1"));
		Assert.assertEquals("foobar", evaluate("'foo' + 'bar'"));
		Assert.assertEquals("truebar", evaluate("true + 'bar'"));
		Assert.assertEquals(10, evaluate("true + 9"));

		Assert.assertEquals(4, evaluate("1 ? 4 : 5"));
		Assert.assertEquals(12, evaluate("false ? 4 : 2 * 6"));
		Assert.assertEquals(12, evaluate("false ? 4 : 2 * 6"));
		Assert.assertEquals(4, evaluate("i ? 4 : 5"));
		Assert.assertEquals(9, evaluate("i - 6 ? 2 + 5 : 3 * 3"));
		Assert.assertEquals(8, evaluate("1 + (-1 ? 2 + 5 : 3 * 3)"));
		Assert.assertEquals(9, evaluate("1 + -1 ? 2 + 5 : 3 * 3"));
		Assert.assertEquals(2, evaluate("true ? false ? 1 : 2 : 3"));
		Assert.assertEquals(5, evaluate("(true ? 0 : 1) ? 4 : 5"));

		Assert.assertEquals("u", evaluate("'auto'[1]"));
		Assert.assertEquals("u", evaluate("('auto')[1]"));
		Assert.assertEquals("a", evaluate("('foo' + 'bar')[4]"));
		Assert.assertEquals(Arrays.asList(1, "foo", 3), evaluate(
				"[1, 'foo', 3]"));
		Assert.assertEquals(2, evaluate("[1, 2, 3][1]"));
		Assert.assertEquals(ImmutableMap.of("alpha", 1, "beta", 2), evaluate(
				"{alpha: 1, beta: 2}"));
		Assert.assertEquals(2, evaluate("{alpha: 1, beta: 2}.beta"));
		Assert.assertEquals(1, evaluate("{alpha: 1, beta: 2}['alpha']"));
		Assert.assertEquals(7, evaluate(
				"{a: 1, b: [true, false][1] ? 4 : 7}['b']"));

		Assert.assertEquals(true, evaluate("bool(256)"));
		Assert.assertEquals(false, evaluate("bool([])"));
		Assert.assertEquals(5, evaluate("len('abcde')"));
		Assert.assertEquals(21, evaluate("len([1, 'foo', []]) * 7"));
		Assert.assertEquals(9, evaluate("indexOf('uncopyrightable', 'h')"));
		Assert.assertEquals(Values.range(5), evaluate("range(5)"));
		Assert.assertEquals(4, evaluate("max(range(5))"));
		Assert.assertEquals(100, evaluate("max(7 * 13, 100)"));
		Assert.assertEquals("HELLO", evaluate("upper('hello')"));
		Assert.assertEquals("1 to the 2 to the 3", evaluate(
				"join([1, 2, 3], ' to the ')"));
		Assert.assertEquals("h.e.l.l.o", evaluate("join('hello', '.')"));
		Assert.assertEquals("this-is-a-test", evaluate(
				"camelToSeparator('thisIsATest', '-')"));
		Assert.assertEquals("this is a test", evaluate(
				"collapse('  this \r\n is\ta   test ')"));
	}

	@Test
	public void testSyntaxError() {
		syntaxError("");
		syntaxError("(1)[0]");
		syntaxError("(1)[::-1]");
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
		syntaxError("len(17)");
		syntaxError("word[:]");
		syntaxError("word[::]");
		syntaxError("word[4::]");
		syntaxError("word[:4:]");
		syntaxError("word[1:5:]");
		syntaxError("word[::0]");
		syntaxError("word[10]");
		syntaxError("word[-11]");
	}

	private static void syntaxError(String expr) {
		try {
			evaluate(expr, false);
			Assert.fail(expr + " was valid");
		} catch (TemplateParseException e) {
			System.out.printf("%-24s %s%n", expr, e.getMessage());
		}
	}

	private static Object evaluate(String expr) {
		return evaluate(expr, true);
	}

	private static Object evaluate(String expr, boolean print) {
		Tokenizer tokenizer = createParser(expr);
		Term term;
		term = tokenizer.parseExpression();
		tokenizer.end();
		if (print) {
			System.out.printf("%-24s %s%n", expr, term);
		}
		return evaluate(term);
	}

	private static Tokenizer createParser(String str) {
		return new Tokenizer(new PushbackReader(new StringReader(str)),
				Tokenizer.Mode.STREAM);
	}

	private static Object evaluate(Term term) {
		// System.out.println(term);
		return term.evaluate(
				new Scope(ImmutableMap.<String, Object>builder()
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
