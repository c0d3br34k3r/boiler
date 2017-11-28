package au.com.codeka.carrot.tmpl.parse;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import au.com.codeka.carrot.CarrotException;

/**
 * Tests for {@link Tokenizer}.
 */
@RunWith(JUnit4.class)
public class TokenizerTest {
	@Test
	public void testEmpty() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("");
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testSingleFixed() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("Hello World!");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "Hello World!"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testSingleTag() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("{% Hello World! %}");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.TAG, " Hello World! "));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testSingleEcho() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("{{ Hello World! }}");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.ECHO, " Hello World! "));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testSingleComment() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("{# Hello World! #}");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.COMMENT, " Hello World! "));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testFixedTagFixed() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("Hello {% foo %} world");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "Hello "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.TAG, " foo "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, " world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testTagEchoFixed() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("{% foo %}{{ bar }} world");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.TAG, " foo "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.ECHO, " bar "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, " world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testTagEchoComment() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("{% foo %}{{ bar }}{# baz #}");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.TAG, " foo "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.ECHO, " bar "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.COMMENT, " baz "));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testTagInvalidEnd() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("stuff {% foo }} baz");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "stuff "));
		try {
			Token token = tokenizer.getNextToken();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: stuff {% foo }} baz\n                  ^\nExpected '%}'");
		}

		tokenizer = createTokenizer("blah {% foo #} baz");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "blah "));
		try {
			Token token = tokenizer.getNextToken();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: blah {% foo #} baz\n                 ^\nExpected '%}'");
		}
	}

	@Test
	public void testEchoInvalidEnd() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("stuff {{ foo %} baz");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "stuff "));
		try {
			Token token = tokenizer.getNextToken();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: stuff {{ foo %} baz\n                  ^\nExpected '}}'");
		}

		tokenizer = createTokenizer("blah {{ foo #} baz");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "blah "));
		try {
			Token token = tokenizer.getNextToken();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: blah {{ foo #} baz\n                 ^\nExpected '}}'");
		}
	}

	@Test
	public void testTagInnerOpenBrace() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello {% yada { yada %} world");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "hello "));
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.TAG, " yada { yada "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, " world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testTagInnerCloseBrace() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello {% yada } yada %} world");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "hello "));
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.TAG, " yada } yada "));
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, " world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testFixedInnerOpenBrace() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello { world");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "hello { world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testFixedInnerCloseBrace() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello } world");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "hello } world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testFixedInnerCloseTag() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello %} world");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "hello %} world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testFixedInnerCloseEcho() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello }} world");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "hello }} world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testEscapedOpenTag() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello {\\{ world");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "hello {{ world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testDoubleEscapedOpenTag() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello {\\\\{ world");
		assertThat(tokenizer.getNextToken())
				.isEqualTo(new Token(TokenType.FIXED, "hello {\\{ world"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testOpenTagEndOfInput() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello {");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "hello {"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testCloseTagEndOfInput() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello }");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "hello }"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	@Test
	public void testPercentEndOfInput() throws CarrotException, IOException {
		Tokenizer tokenizer = createTokenizer("hello %");
		assertThat(tokenizer.getNextToken()).isEqualTo(new Token(TokenType.FIXED, "hello %"));
		assertThat(tokenizer.getNextToken()).isNull();
	}

	private static Tokenizer createTokenizer(String content) {
		return new Tokenizer(new StringReader(content));
	}

}
