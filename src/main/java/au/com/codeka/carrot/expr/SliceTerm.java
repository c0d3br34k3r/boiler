package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

class SliceTerm implements Term {

	private final Term seq;
	private final Term start;
	private final Term stop;
	private final Term step;

	public SliceTerm(Term seq, Term start, Term stop, Term step) {
		this.seq = seq;
		this.start = start;
		this.stop = stop;
		this.step = step;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Values.slice(seq.evaluate(scope),
				get(start, scope),
				get(stop, scope),
				get(step, scope));
	}

	private static Integer get(Term term, Scope scope) {
		return term == null
				? null
				: Values.toNumber(term.evaluate(scope)).intValue();
	}

	@Override
	public String toString() {
		return String.format("[%s SLICE %s, %s, %s]", seq, start, stop, step);
	}

}
