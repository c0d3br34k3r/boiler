package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

class IndexTerm implements Term {

	private final Term term;
	private final Term index;

	public IndexTerm(Term term, Term index) {
		this.term = term;
		this.index = index;
	}

	@Override
	public Object evaluate(Scope scope) {
		Object indexable = term.evaluate(scope);
		if (indexable == null) {
			throw new IllegalArgumentException("cannot index null");
		}
		return Values.index(indexable, index.evaluate(scope));
	}

	@Override
	public String toString() {
		return String.format("(%s INDEX %s)", term.toString(), index.toString());
	}

}
