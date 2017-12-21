package au.com.codeka.carrot.expr;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;
import java.util.Collections;

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
		assertThat(evaluate("+true")).isEqualTo(1);
		assertThat(evaluate("1+1")).isEqualTo(2);
		assertThat(evaluate("1+1+1")).isEqualTo(3);
		assertThat(evaluate("1+1+1+1")).isEqualTo(4);
		assertThat(evaluate("1")).isEqualTo(1);
		assertThat(evaluate("!1")).isEqualTo(false);
		assertThat(evaluate("!!1")).isEqualTo(true);
		assertThat(evaluate("!!!1")).isEqualTo(false);
		assertThat(evaluate("2 + 2 * 2")).isEqualTo(6);
		assertThat(evaluate("2 * 2 + 2")).isEqualTo(6);
		assertThat(evaluate("2 * (2 + 2)")).isEqualTo(8);
		assertThat(evaluate("foo[4 + 4]")).isEqualTo(7);
		assertThat(evaluate("foo[6] * 2")).isEqualTo(14);
		assertThat(evaluate("bar.baz")).isEqualTo("qux");
		assertThat(evaluate("quux.quuz.corge")).isEqualTo("grault");
		assertThat(evaluate("quux['qu' + 'uz']['corge']")).isEqualTo("grault");
		assertThat(evaluate("quux['quuz']['garply'][0]")).isEqualTo(3);
		assertThat(evaluate("'1' + 1")).isEqualTo("11");
		assertThat(evaluate("'foo' + 'bar'")).isEqualTo("foobar");
		assertThat(evaluate("true + 'bar'")).isEqualTo("truebar");
		assertThat(evaluate("true + 9")).isEqualTo(10);
	}

	private Object evaluate(String expr) throws CarrotException {
		return evaluate(createParser(expr).parseExpression());
	}

	private StatementParser createParser(String str) throws CarrotException {
		return new StatementParser(new Tokenizer(new StringReader(str)));
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
