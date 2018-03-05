package au.com.codeka.carrot;

import java.io.IOException;
import java.io.Reader;

public class StringReader extends Reader {

	private String str;
	private int length;
	private int next = 0;
	private int mark = 0;

	public StringReader(String s) {
		this.str = s;
		this.length = s.length();
	}

	@Override
	public int read() {
		if (next < length) {
			return str.charAt(next++);
		}
		return -1;
	}
	
	public void unread() {
		next--;
	}

	@Override
	public int read(char cbuf[], int off, int len) {
		if ((off < 0) || (off > cbuf.length) || (len < 0) ||
				((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		}
		if (len == 0) {
			return 0;
		}
		if (next >= length) {
			return -1;
		}
		int n = Math.min(length - next, len);
		str.getChars(next, next + n, cbuf, off);
		next += n;
		return n;
	}

	@Override
	public long skip(long ns) {
		if (next >= length) {
			return 0;
		}
		// Bound skip by beginning and end of the source
		long n = Math.min(length - next, ns);
		n = Math.max(-next, n);
		next += n;
		return n;
	}

	@Override
	public boolean ready() throws IOException {
		return true;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		if (readAheadLimit < 0) {
			throw new IllegalArgumentException();
		}
		mark = next;
	}

	/**
	 * Resets the stream to the most recent mark, or to the beginning of the
	 * string if it has never been marked.
	 *
	 * @exception IOException If an I/O error occurs
	 */
	@Override
	public void reset() throws IOException {
		next = mark;
	}

	@Override
	public void close() {
		// do nothing
	}

}
