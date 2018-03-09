package au.com.codeka.carrot;

public class StringReader {

	private String str;
	private int length;
	private int next = 0;

	public StringReader(String str) {
		this.str = str;
		this.length = str.length();
	}

	public int next() {
		if (next < length) {
			return str.charAt(next++);
		}
		return -1;
	}

	public void unread() {
		next--;
	}

}
