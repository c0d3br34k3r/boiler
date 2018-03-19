package au.com.codeka.carrot;

import org.junit.Assert;
import org.junit.Test;

import au.com.codeka.carrot.expr.Values;

public class UtilTest {

	@Test
	public void addTest() {
		Assert.assertEquals(Values.add((Object) 2, 3), 5);
		Assert.assertEquals(Values.add((Object) 2.5, 3), 5.5);
		Assert.assertEquals(Values.add((Object) 2.5, 3), 5.5);
		Assert.assertEquals(Values.add("3", 4), 7);
		Assert.assertEquals(Values.add(3, "4"), 7);
		Assert.assertEquals(Values.add("3.5", 4), 7.5);
		Assert.assertEquals(Values.add("3", 4.5), 7.5);
		Assert.assertEquals(Values.add("3", "4"), "34");
		Assert.assertEquals(Values.add("q", 1), "q1");
		Assert.assertEquals(Values.add("q", "1"), "q1");
		Assert.assertEquals(Values.add(5, "r"), "5r");
		Assert.assertEquals(Values.add("a", "b"), "ab");
		Assert.assertEquals(Values.add(1, true), 2);
		Assert.assertEquals(Values.add("1", true), "1true");
		Assert.assertEquals(Values.add(false, 4), 4);
		Assert.assertEquals(Values.add(false, "y"), "falsey");
		Assert.assertEquals(Values.add(true, true), "truetrue");
	}
	
	@Test
	public void test() {
		System.out.println(Character.digit('.', 10));
	}

}
