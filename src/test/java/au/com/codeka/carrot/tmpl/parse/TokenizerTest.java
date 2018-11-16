package au.com.codeka.carrot.tmpl.parse;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.catascopic.template.CarrotException;
import com.catascopic.template.parse.NodeType;
import com.catascopic.template.parse.TagParser;

/**
 * Tests for {@link TagParser}.
 */
@RunWith(JUnit4.class)
public class TokenizerTest {
	@Test
	public void testEmpty() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("");
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleFixed() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("Hello World!");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "Hello World!"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleTag() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("{% Hello World! %}");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.TAG, " Hello World! "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleEcho() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("{{ Hello World! }}");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.ECHO, " Hello World! "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testSingleComment() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("{# Hello World! #}");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.COMMENT, " Hello World! "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedTagFixed() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("Hello {% foo %} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "Hello "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.TAG, " foo "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagEchoFixed() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("{% foo %}{{ bar }} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.TAG, " foo "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.ECHO, " bar "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagEchoComment() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("{% foo %}{{ bar }}{# baz #}");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.TAG, " foo "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.ECHO, " bar "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.COMMENT, " baz "));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagInvalidEnd() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("stuff {% foo }} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "stuff "));
		try {
			Segment token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: stuff {% foo }} baz\n                  ^\nExpected '%}'");
		}

		tokenizer = createTokenizer("blah {% foo #} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "blah "));
		try {
			Segment token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: blah {% foo #} baz\n                 ^\nExpected '%}'");
		}
	}

	@Test
	public void testEchoInvalidEnd() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("stuff {{ foo %} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "stuff "));
		try {
			Segment token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: stuff {{ foo %} baz\n                  ^\nExpected '}}'");
		}

		tokenizer = createTokenizer("blah {{ foo #} baz");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "blah "));
		try {
			Segment token = tokenizer.getNext();
			fail("Expected ParseException, got: " + token);
		} catch (CarrotException e) {
			assertThat(e.getMessage())
					.isEqualTo("???\n1: blah {{ foo #} baz\n                 ^\nExpected '}}'");
		}
	}

	@Test
	public void testTagInnerOpenBrace() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello {% yada { yada %} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "hello "));
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.TAG, " yada { yada "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testTagInnerCloseBrace() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello {% yada } yada %} world");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "hello "));
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.TAG, " yada } yada "));
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, " world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerOpenBrace() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello { world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "hello { world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerCloseBrace() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello } world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "hello } world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerCloseTag() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello %} world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "hello %} world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testFixedInnerCloseEcho() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello }} world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "hello }} world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testEscapedOpenTag() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello {\\{ world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "hello {{ world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testDoubleEscapedOpenTag() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello {\\\\{ world");
		assertThat(tokenizer.getNext())
				.isEqualTo(new Segment(NodeType.FIXED, "hello {\\{ world"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testOpenTagEndOfInput() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello {");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "hello {"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testCloseTagEndOfInput() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello }");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "hello }"));
		assertThat(tokenizer.getNext()).isNull();
	}

	@Test
	public void testPercentEndOfInput() throws CarrotException, IOException {
		TagParser tokenizer = createTokenizer("hello %");
		assertThat(tokenizer.getNext()).isEqualTo(new Segment(NodeType.FIXED, "hello %"));
		assertThat(tokenizer.getNext()).isNull();
	}

	private static TagParser createTokenizer(String content) {
		return new TagParser(new StringReader(content));
	}

}
