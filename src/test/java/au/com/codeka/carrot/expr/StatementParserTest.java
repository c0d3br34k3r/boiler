package au.com.codeka.carrot.expr;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.MapBindings;

/**
 * Tests for {@link StatementParserTest}.
 */
@RunWith(JUnit4.class)
public class StatementParserTest {

	@Test
	public void testBinaryOperation() throws CarrotException {
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
		Assert.assertEquals(evaluate("'1' + 1"), "11");
		Assert.assertEquals(evaluate("'foo' + 'bar'"), "foobar");
		Assert.assertEquals(evaluate("true + 'bar'"), "truebar");
		Assert.assertEquals(evaluate("true + 9"), 10);
	}

	@Test
	public void testSyntaxError() {
		syntaxError("1 1");
	}

	private void syntaxError(String expr) {
		try {
			evaluate(expr);
			Assert.fail(expr + " was valid");
		} catch (CarrotException e) {
			System.out.println(e.getMessage());
		}
	}

	private Object evaluate(String expr) throws CarrotException {
		Tokenizer tokenizer = createParser(expr);
		Term term = tokenizer.parseExpression();
		tokenizer.consume(TokenType.END);
		return evaluate(term);
	}

	private Tokenizer createParser(String str) {
		return new Tokenizer(new PushbackReader(new StringReader(str)), Tokenizer.Mode.STREAM);
	}

	private Object evaluate(Term term) throws CarrotException {
		System.out.println(term);
		return term.evaluate(
				new Configuration.Builder().build(),
				new Scope(new MapBindings(ImmutableMap.<String, Object> of("foo",
						Collections.nCopies(10, 7), "bar", ImmutableMap.of("baz", "qux"), "quux",
						ImmutableMap.of("quuz", ImmutableMap.of("corge", "grault", "garply",
								ImmutableList.of(3)))))));
	}

}
