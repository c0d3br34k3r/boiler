package au.com.codeka.carrot.util;

import com.catascopic.template.CarrotException;
import com.catascopic.template.eval.Lazy;

/**
 * @author Marten Gajda
 */
public final class MockLazyTerm implements Lazy {
	private final Object result;

	public MockLazyTerm(Object result) {
		this.result = result;
	}

	@Override
	public Object value() throws CarrotException {
		return result;
	}
}
