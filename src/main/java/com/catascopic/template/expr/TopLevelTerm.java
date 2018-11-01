package com.catascopic.template.expr;

import com.catascopic.template.TemplateEvalException;
import com.catascopic.template.Scope;

class TopLevelTerm implements Term {

	private final Term term;
	private final int lineNumber;
	private final int columnNumber;

	TopLevelTerm(Term term, int lineNumber, int columnNumber) {
		this.term = term;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	@Override
	public Object evaluate(Scope scope) {
		try {
			return term.evaluate(scope);
		} catch (TemplateEvalException e) {
			throw new LocatedEvalException(e, this);
		}
	}

	int lineNumber() {
		return lineNumber;
	}

	int columnNumber() {
		return columnNumber;
	}

	@Override
	public String toString() {
		return term.toString();
	}

}
