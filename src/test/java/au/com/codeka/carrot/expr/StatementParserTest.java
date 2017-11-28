package au.com.codeka.carrot.expr;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;
import java.util.Collections;

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
		assertThat(evaluate(createStatementParser("1+1").parseTerm())).isEqualTo(2);
		assertThat(evaluate(createStatementParser("1+1+1").parseTerm())).isEqualTo(3);
		assertThat(evaluate(createStatementParser("1+1+1+1").parseTerm())).isEqualTo(4);
		assertThat(evaluate(createStatementParser("1").parseTerm())).isEqualTo(1);
		assertThat(evaluate(createStatementParser("!1").parseTerm())).isEqualTo(false);
		assertThat(evaluate(createStatementParser("!!1").parseTerm())).isEqualTo(true);
		assertThat(evaluate(createStatementParser("!!!1").parseTerm())).isEqualTo(false);
		assertThat(evaluate(createStatementParser("2 + 2 * 2").parseTerm())).isEqualTo(6);
		assertThat(evaluate(createStatementParser("(2 + 2) * 2").parseTerm())).isEqualTo(8);
		assertThat(evaluate(createStatementParser("foo[4 + 4]").parseTerm())).isEqualTo(7);
		assertThat(evaluate(createStatementParser("foo[6]*2").parseTerm())).isEqualTo(14);
	}

	// @Test
	// public void test() throws CarrotException {
	// Tokenizer tokenizer = new Tokenizer(new StringReader("!1"));
	// Token token;
	// do {
	// token = tokenizer.require(EnumSet.allOf(TokenType.class));
	// System.out.println(token);
	// } while (token.getType() != TokenType.EOF);
	// }

	@SuppressWarnings("unchecked")
	@Test
	public void testIterableTerms() throws CarrotException {
		assertThat((Iterable<Object>) evaluate(createStatementParser("1, 2").parseTermsIterable()))
				.containsAllOf(1L, 2L);
		assertThat(
				(Iterable<Object>) evaluate(createStatementParser("1, 2, 3").parseTermsIterable()))
						.containsAllOf(1L, 2L, 3L);
		assertThat((Iterable<Object>) evaluate(
				createStatementParser("1, 2 + 5, \"3\", 4").parseTermsIterable())).containsAllOf(1L,
						7L, "3", 4L);
	}

	private StatementParser createStatementParser(String str) throws CarrotException {
		return new StatementParser(
				new Tokenizer(new StringReader(str)));
	}

	private Object evaluate(Term term) throws CarrotException {
		return term.evaluate(
				new Configuration.Builder().build(),
				new Scope(new MapBindings(ImmutableMap.<String, Object> of("foo",
						Collections.nCopies(10, 7)))));
	}

}
