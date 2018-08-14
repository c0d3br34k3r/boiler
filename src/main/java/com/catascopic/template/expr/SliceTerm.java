package com.catascopic.template.expr;

import com.catascopic.template.Scope;
import com.catascopic.template.Values;

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
		Object value = term.evaluate(scope);
		return value == null
				? null
				: Values.toNumber(value).intValue();
	}

	@Override
	public String toString() {
		return String.format("(SLICE %s, %s, %s, %s)", seq, start, stop, step);
	}

}
