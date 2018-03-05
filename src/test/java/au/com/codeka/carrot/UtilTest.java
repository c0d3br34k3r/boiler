package au.com.codeka.carrot;

import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

	@Test
	public void addTest() {
		Assert.assertEquals(ValueHelper.add((Object) 2, 3), 5);
		Assert.assertEquals(ValueHelper.add((Object) 2.5, 3), 5.5);
		Assert.assertEquals(ValueHelper.add((Object) 2.5, 3), 5.5);
		Assert.assertEquals(ValueHelper.add("3", 4), 7);
		Assert.assertEquals(ValueHelper.add(3, "4"), 7);
		Assert.assertEquals(ValueHelper.add("3.5", 4), 7.5);
		Assert.assertEquals(ValueHelper.add("3", 4.5), 7.5);
		Assert.assertEquals(ValueHelper.add("3", "4"), "34");
		Assert.assertEquals(ValueHelper.add("q", 1), "q1");
		Assert.assertEquals(ValueHelper.add("q", "1"), "q1");
		Assert.assertEquals(ValueHelper.add(5, "r"), "5r");
		Assert.assertEquals(ValueHelper.add("a", "b"), "ab");
		Assert.assertEquals(ValueHelper.add(1, true), 2);
		Assert.assertEquals(ValueHelper.add("1", true), "1true");
		Assert.assertEquals(ValueHelper.add(false, 4), 4);
		Assert.assertEquals(ValueHelper.add(false, "y"), "falsey");
		Assert.assertEquals(ValueHelper.add(true, true), "truetrue");
	}
	
	@Test
	public void test() {
		System.out.println(Character.digit('.', 10));
	}

}
