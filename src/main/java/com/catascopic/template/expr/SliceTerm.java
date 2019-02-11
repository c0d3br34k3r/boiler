package com.catascopic.template.expr;

import com.catascopic.template.Context;
import com.catascopic.template.value.Values;

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
	public Object evaluate(Context context) {
		return Values.slice(seq.evaluate(context),
				get(start, context),
				get(stop, context),
				get(step, context));
	}

	private static Integer get(Term term, Context context) {
		Object value = term.evaluate(context);
		return value == null
				? null
				: Values.toNumber(value).intValue();
	}

	@Override
	public String toString() {
		if (step == NullTerm.NULL) {
			return String.format("%s[%s:%s]",
					seq,
					nullTermToEmpty(start),
					nullTermToEmpty(stop));
		}
		return String.format("%s[%s:%s:%s]",
				seq,
				nullTermToEmpty(start),
				nullTermToEmpty(stop),
				step);
	}

	private static String nullTermToEmpty(Term term) {
		return term == NullTerm.NULL ? "" : term.toString();
	}

}
