package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

/**
 * A binary {@link Term}. The left term is the accessed object, the right term
 * is the accessor.
 *
 * @author Marten Gajda
 */
class SliceTerm implements Term {

	private final Term term;
	private final Term index;

	public SliceTerm(Term term, Term index) {
		this.term = term;
		this.index = index;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Values.index(term.evaluate(scope), index.evaluate(scope));
	}

	@Override
	public String toString() {
		return String.format("[%s ACCESS %s]", term.toString(), index.toString());
	}

}
