package au.com.codeka.carrot;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.base.Joiner;

public class Test2 {

	@Test
	public void test() {
		System.out.println(Joiner.on(", ").join(new Zip(Arrays.asList("abcc", "dexf", "ghij"))));
	}
	
	
}
