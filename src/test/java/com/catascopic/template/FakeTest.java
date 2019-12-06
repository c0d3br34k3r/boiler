package com.catascopic.template;

import java.util.Map;

import org.junit.Test;

public class FakeTest {

	@Test
	public void Test() {
		Map<String, Integer> map = new LruMap<>(4);
		map.put("a", 1);
		map.put("b", 2);
		
		System.out.println(map);
	}
	
}
