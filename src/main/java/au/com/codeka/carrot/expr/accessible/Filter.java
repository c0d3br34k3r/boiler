package au.com.codeka.carrot.expr.accessible;

public abstract class Filter<E> {

	public final Object apply(Object arg) {
		@SuppressWarnings("unchecked")
		E cast = (E) arg;
		return filter(cast);
	}

	protected abstract Object filter(E arg);

	public static final Filter<String> UPPER_CASE = new Filter<String>() {

		@Override
		protected Object filter(String arg) {
			return arg.toUpperCase();
		}
	};

}
