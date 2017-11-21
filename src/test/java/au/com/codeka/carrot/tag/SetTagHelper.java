package au.com.codeka.carrot.tag;

import au.com.codeka.carrot.CarrotException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static au.com.codeka.carrot.util.RenderHelper.render;
import static com.google.common.truth.Truth.assertThat;

/**
 * Tests for {@link SetTag}.
 */
@RunWith(JUnit4.class)
public class SetTagHelper {
	@Test
	public void testSimple() throws CarrotException {
		assertThat(render("{% set foo %}Hello World{% end %}foo={{ foo }}"))
				.isEqualTo("foo=Hello World");
	}

	@Test
	public void testUnescaped() throws CarrotException {
		assertThat(render("{% set foo %}Hello <b>World</b>{% end %}foo={{ html.safe(foo) }}"))
				.isEqualTo("foo=Hello <b>World</b>");
	}

	@Test
	public void testExpression() throws CarrotException {
		assertThat(render("{% set foo = 'bar' %}foo={{ foo }}"))
				.isEqualTo("foo=bar");
	}

	@Test
	public void testNumericExpression() throws CarrotException {
		assertThat(render("{% set foo = 1 + 2 %}foo={{ foo }}"))
				.isEqualTo("foo=3");
	}

	@Test
	public void testExpansion() throws CarrotException {
		assertThat(render("{% set foo, bar = baz %}foo={{ foo }}, bar={{ bar }}", "baz",
				Arrays.asList("bing", "bong")))
						.isEqualTo("foo=bing, bar=bong");
	}

	@Test
	public void testIterableExpansion() throws CarrotException {
		assertThat(render("{% set foo, bar = \"bing\", \"bong\" %}foo={{ foo }}, bar={{ bar }}"))
				.isEqualTo("foo=bing, bar=bong");
	}

	@Test
	public void testSwap() throws CarrotException {
		assertThat(render(
				"{% set foo, bar = \"bing\", \"bong\" %}{% set foo, bar = bar, foo %}foo={{ foo }}, bar={{ bar }}"))
						.isEqualTo("foo=bong, bar=bing");
		assertThat(render(
				"{% set foo, bar, baz = \"bing\", \"bong\", \"bang\" %}{% set foo, bar, baz = bar, baz, foo %}foo={{ foo }}, bar={{ bar }}, baz={{ baz }}"))
						.isEqualTo("foo=bong, bar=bang, baz=bing");
	}
}
