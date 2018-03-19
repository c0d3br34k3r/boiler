package au.com.codeka.carrot;

import java.util.List;

import com.google.common.collect.ForwardingList;

public class Params extends ForwardingList<Object> {

	private List<Object> list;

	public Params(List<Object> list) {
		this.list = list;
	}

	@Override
	protected List<Object> delegate() {
		return list;
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

	public int getIntOrDefault(int index, int defaultValue) {
		return getOrDefault(index, defaultValue);
	}

	public String getStr() {
		return (String) get();
	}

	public String getStr(int index) {
		return (String) get(index);
	}

	public String getStrOrDefault(int index, String defaultValue) {
		return getOrDefault(index, defaultValue);
	}

	@SuppressWarnings("unchecked")
	private <T> T getOrDefault(int index, T defaultValue) {
		return index < size() ? (T) get(index) : defaultValue;
	}

}
