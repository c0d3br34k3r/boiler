package com.catascopic.template.parse;

import static com.catascopic.template.Rendering.render;

import org.junit.Assert;
import org.junit.Test;

public class ElseTagTest {

	@Test
	public void testBasicIfElse() {
		Assert.assertEquals("foo is false", render(
				"<% if foo %>foo is true<% else %>foo is false<% end %>", 
				"foo", false));
	}

	@Test
	public void testIfElseIfElse() {
		String template = "<% if foo %>"
				+ "foo is true"
				+ "<% else if bar %>"
				+ "bar is true"
				+ "<% else %>"
				+ "neither is true"
				+ "<% end %>";

		Assert.assertEquals("foo is true", render(template,
				"foo", true, "bar", false));
		Assert.assertEquals("bar is true", render(template,
				"foo", false, "bar", true));
		Assert.assertEquals("neither is true", render(template,
				"foo", false, "bar", false));
	}

}
