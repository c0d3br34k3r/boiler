package au.com.codeka.carrot.expr;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;
import java.util.Collections;
import java.util.EnumSet;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
		assertThat(evaluate(createParser("+true").parseTerm())).isEqualTo(1);
		assertThat(evaluate(createParser("1+1").parseTerm())).isEqualTo(2);
		assertThat(evaluate(createParser("1+1+1").parseTerm())).isEqualTo(3);
		assertThat(evaluate(createParser("1+1+1+1").parseTerm())).isEqualTo(4);
		assertThat(evaluate(createParser("1").parseTerm())).isEqualTo(1);
		assertThat(evaluate(createParser("!1").parseTerm())).isEqualTo(false);
		assertThat(evaluate(createParser("!!1").parseTerm())).isEqualTo(true);
		assertThat(evaluate(createParser("!!!1").parseTerm())).isEqualTo(false);
		assertThat(evaluate(createParser("2 + 2 * 2").parseTerm())).isEqualTo(6);
		assertThat(evaluate(createParser("2 * 2 + 2").parseTerm())).isEqualTo(6);
		assertThat(evaluate(createParser("2 * (2 + 2)").parseTerm())).isEqualTo(8);
		assertThat(evaluate(createParser("foo[4 + 4]").parseTerm())).isEqualTo(7);
		assertThat(evaluate(createParser("foo[6]*2").parseTerm())).isEqualTo(14);
		assertThat(evaluate(createParser("'1' + 1").parseTerm())).isEqualTo("11");
		assertThat(evaluate(createParser("'foo' + 'bar'").parseTerm())).isEqualTo("foobar");
	}

	@SuppressWarnings("unchecked")
	@Ignore
	@Test
	public void testIterableTerms() throws CarrotException {
		assertThat((Iterable<Object>) evaluate(createParser("1, 2").parseTermsIterable()))
				.containsAllOf(1, 2);
		assertThat(
				(Iterable<Object>) evaluate(createParser("1, 2, 3").parseTermsIterable()))
						.containsAllOf(1, 2, 3);
		assertThat((Iterable<Object>) evaluate(
				createParser("1, 2 + 5, \"3\", 4").parseTermsIterable())).containsAllOf(1,
						7, "3", 4);
	}

	private StatementParser createParser(String str) throws CarrotException {
		return new StatementParser(new Tokenizer(new StringReader(str)));
	}

	private Object evaluate(Term term) throws CarrotException {
		return term.evaluate(
				new Configuration.Builder().build(),
				new Scope(new MapBindings(ImmutableMap.<String, Object> of("foo",
						Collections.nCopies(10, 7)))));
	}

}
