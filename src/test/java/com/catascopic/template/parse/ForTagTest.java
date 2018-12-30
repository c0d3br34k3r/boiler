package com.catascopic.template.parse;

import static com.catascopic.template.Rendering.render;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableMap;

@RunWith(Parameterized.class)
public class ForTagTest {

	@Parameter(0)
	public String expected;
	@Parameter(1)
	public String template;
	@Parameter(2)
	public Map<String, Object> params;

	@Test
	public void test() {
		Assert.assertEquals(expected, render(template, params));
	}

	// @formatter:off
	@Parameters(name = "{1} with {2} = {0}")
	public static List<Object[]> params() {
		return Arrays.asList(new Object[][] {
			{
				"foo (3)(1)(4)(1)(5) bar",
				"foo @{for n in values}(${n})@{end} bar",
				ImmutableMap.of("values", Arrays.asList(3, 1, 4, 1, 5)) 
			}, {
				"foo 3-1-4-1-5 bar",
				"foo @{for n in values[:-1]}${n}-@{end}${values[-1]} bar",
				ImmutableMap.of("values", Arrays.asList(3, 1, 4, 1, 5))
			}, {
				"foo 3-1-4-1-5 bar",
				"foo @{for i, n in enumerate(values)}${n}@{if i != len(values) - 1}-@{end}@{end} bar",
				ImmutableMap.of("values", Arrays.asList(3, 1, 4, 1, 5))
			}, {
				"foo (3)(1)(4)(1)(5) bar",
				"foo @{for n in [3, 1, 4, 1, 5]}(${n})@{end} bar",
				ImmutableMap.of()
			}, {
				"foo [a=1][b=2][c=3] bar",
				"foo @{for k, v in entries(map)}[${k}=${v}]@{end} bar",
				ImmutableMap.of("map", ImmutableMap.of("a", 1, "b", 2, "c", 3))
			},
		});
	}

}
