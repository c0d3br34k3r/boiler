package au.com.codeka.carrot;

public class Test {

	public static void main(String[] args) throws CarrotException {
		for (Builtin builtin : Builtin.values()) {
			System.out.println(builtin.functionName());
		}
	}

}
