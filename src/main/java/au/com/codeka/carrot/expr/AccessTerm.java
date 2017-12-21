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
		if (termValue == null) {
			throw new CarrotException("null has no properties");
		}
		if (termValue instanceof Map) {
			return ((Map<?, ?>) termValue).get(accessValue.toString());
		}
		if (termValue instanceof Bindings) {
			return ((Bindings) termValue).resolve(accessValue.toString());
		}
		if (termValue instanceof List) {
			return ((List<?>) termValue).get(ValueHelper.toNumber(accessValue).intValue());
		}
//		if (termValue.getClass().isArray()) {
//			return Array.get(termValue, ValueHelper.toNumber(accessValue).intValue());
//		}
//		if (termValue instanceof Iterable && accessValue instanceof Number) {
//			return Iterators.get(((Iterable<?>) termValue).iterator(),
//					ValueHelper.toNumber(accessValue).intValue());
//		}
		throw new CarrotException("Cannot access key " + accessValue + " in " + termValue);
	}

	@Override
	public String toString() {
		return String.format("[%s ACCESS %s]", term.toString(), accessor.toString());
	}

}
