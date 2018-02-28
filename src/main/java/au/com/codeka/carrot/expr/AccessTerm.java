package au.com.codeka.carrot.expr;

import java.util.List;
import java.util.Map;

import au.com.codeka.carrot.Bindings;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.ValueHelper;

/**
 * A binary {@link Term}. The left term is the accessed object, the right term
 * is the accessor.
 *
 * @author Marten Gajda
 */
class AccessTerm implements Term {

	private final Term term;
	private final Term accessor;

	public AccessTerm(Term term, Term accessor) {
		this.term = term;
		this.accessor = accessor;
	}

	@Override
	public Object evaluate(Configuration config, Scope scope) throws CarrotException {
		Object termValue = term.evaluate(config, scope);
		Object accessValue = accessor.evaluate(config, scope);
		if (termValue instanceof Map) {
			return ((Map<?, ?>) termValue).get(accessValue.toString());
		}
		if (termValue instanceof Bindings) {
			return ((Bindings) termValue).resolve(accessValue.toString());
		}
		if (termValue instanceof List) {
			int index = ValueHelper.toNumber(accessValue).intValue();
			List<?> list = (List<?>) termValue;
			return list.get(index < 0 ? list.size() + index : index);
		}
		if (termValue instanceof String) {
			int index = ValueHelper.toNumber(accessValue).intValue();
			String str = (String) termValue;
			return String.valueOf(str.charAt(index < 0 ? str.length() + index : index));
		}
		throw new CarrotException("Cannot access key " + accessValue + " in " + termValue);
	}

	@Override
	public String toString() {
		return String.format("[%s ACCESS %s]", term.toString(), accessor.toString());
	}

}
