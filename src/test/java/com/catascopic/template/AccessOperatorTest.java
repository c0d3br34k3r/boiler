package com.catascopic.template;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.catascopic.template.Values;
import com.google.common.collect.ImmutableMap;

/**
 * @author Marten Gajda
 */
public class AccessOperatorTest {

	@Test
	public void testApply() throws Exception {
		assertThat(Values.index(ImmutableMap.of("1", "a", "2", "b", "3", "c"), "1")).isEqualTo("a");
		assertThat(Values.index(ImmutableMap.of("1", "a", "2", "b", "3", "c"), "2")).isEqualTo("b");
		assertThat(Values.index(ImmutableMap.of("1", "a", "2", "b", "3", "c"), "3")).isEqualTo("c");
		assertThat(Values.index(Arrays.asList("a", "b", "c"), 0)).isEqualTo("a");
		assertThat(Values.index(Arrays.asList("a", "b", "c"), 1)).isEqualTo("b");
		assertThat(Values.index(Arrays.asList("a", "b", "c"), 2)).isEqualTo("c");
	}

}
