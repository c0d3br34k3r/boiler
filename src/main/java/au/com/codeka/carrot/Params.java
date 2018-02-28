package au.com.codeka.carrot;

import java.util.List;

import com.google.common.collect.ForwardingList;

public class Params extends ForwardingList<Object> {

	@Override
	protected List<Object> delegate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object get() {
		if (size() != 1) {
			throw new IllegalStateException();
		}
		return get(0);
	}

	public int getInt() {
		return (int) get();
	}

	public int getInt(int index) {
		return (int) get(index);
	}

	public String getStr() {
		return (String) get();
	}

	public String getStr(int index) {
		return (String) get(index);
	}

}
