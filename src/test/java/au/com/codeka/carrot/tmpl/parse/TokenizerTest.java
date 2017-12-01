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
 * Tests for {@link ContentParser}.
 */
@RunWith(JUnit4.class)
public class TokenizerTest {
	@Test
	public void testEmpty() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("");
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleFixed() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("Hello World!");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "Hello World!"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleTag() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("{% Hello World! %}");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.TAG, " Hello World! "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleEcho() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("{{ Hello World! }}");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.ECHO, " Hello World! "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleComment() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("{# Hello World! #}");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.COMMENT, " Hello World! "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedTagFixed() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("Hello {% foo %} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "Hello "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.TAG, " foo "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagEchoFixed() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("{% foo %}{{ bar }} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.TAG, " foo "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.ECHO, " bar "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagEchoComment() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("{% foo %}{{ bar }}{# baz #}");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.TAG, " foo "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.ECHO, " bar "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.COMMENT, " baz "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagInvalidEnd() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("stuff {% foo }} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "stuff "));
		try {
			Content token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: stuff {% foo }} baz\n                  ^\nExpected '%}'");
		}

		tokenizer = createTokenizer("blah {% foo #} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "blah "));
		try {
			Content token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: blah {% foo #} baz\n                 ^\nExpected '%}'");
		}
	}

	@Test
	public void testEchoInvalidEnd() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("stuff {{ foo %} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "stuff "));
		try {
			Content token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: stuff {{ foo %} baz\n                  ^\nExpected '}}'");
		}

		tokenizer = createTokenizer("blah {{ foo #} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "blah "));
		try {
			Content token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: blah {{ foo #} baz\n                 ^\nExpected '}}'");
		}
	}

	@Test
	public void testTagInnerOpenBrace() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello {% yada { yada %} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "hello "));
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.TAG, " yada { yada "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagInnerCloseBrace() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello {% yada } yada %} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "hello "));
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.TAG, " yada } yada "));
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerOpenBrace() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello { world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "hello { world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerCloseBrace() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello } world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "hello } world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerCloseTag() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello %} world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "hello %} world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerCloseEcho() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello }} world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "hello }} world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testEscapedOpenTag() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello {\\{ world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "hello {{ world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testDoubleEscapedOpenTag() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello {\\\\{ world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Content(ContentType.FIXED, "hello {\\{ world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testOpenTagEndOfInput() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello {");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "hello {"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testCloseTagEndOfInput() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello }");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "hello }"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testPercentEndOfInput() throws CarrotException, IOException {
		ContentParser tokenizer = createTokenizer("hello %");
		assertThat(tokenizer.getNext()).isEqualTo(new Content(ContentType.FIXED, "hello %"));
		assertThat(tokenizer.getNext()).isNull();
	}

	private static ContentParser createTokenizer(String content) {
		return new ContentParser(new StringReader(content));
	}

}
