package au.com.codeka.carrot.expr;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.TemplateParseException;
import au.com.codeka.carrot.bindings.MapBindings;

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

		Assert.assertEquals(evaluate("R[0]"), 1);
		Assert.assertEquals(evaluate("R[1]"), 2);
		Assert.assertEquals(evaluate("R[-1]"), 7);
		Assert.assertEquals(evaluate("R[-2]"), 6);
		Assert.assertEquals(evaluate("auto[3]"), "o");
		Assert.assertEquals(evaluate("auto[-2]"), "l");
		Assert.assertEquals(evaluate("(auto)"), "automobile");
		Assert.assertEquals(evaluate("(auto)[7]"), "i");

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
	}

	@Test
	public void testSyntaxError() {
		syntaxError("");
		syntaxError("(1)[0]");
		syntaxError("1 +");
		syntaxError("1 1");
		syntaxError("1 &! 1");
	}

	private static void syntaxError(String expr) {
		try {
			evaluate(expr);
			Assert.fail(expr + " was valid");
		} catch (TemplateParseException e) {
			System.out.println(e.getMessage());
		}
	}

	private static Object evaluate(String expr) {
		Tokenizer tokenizer = createParser(expr);
		Term term = tokenizer.parseExpression();
		tokenizer.end();
		return evaluate(term);
	}

	private static Tokenizer createParser(String str) {
		return new Tokenizer(new PushbackReader(new StringReader(str)), Tokenizer.Mode.STREAM);
	}

	private static Object evaluate(Term term) {
		System.out.println(term);
		return term.evaluate(
				new Scope(new MapBindings(ImmutableMap.<String, Object> builder()
						.put("i", 6)
						.put("R", Arrays.asList(1, 2, 3, 4, 5, 6, 7))
						.put("auto", "automobile")
						.put("foo", Collections.nCopies(10, 7))
						.put("bar", ImmutableMap.of("baz", "qux"))
						.put("quux", ImmutableMap.of("quuz", ImmutableMap.of(
								"corge", "grault",
								"garply", ImmutableList.of(3))))
						.build())));
	}

}
