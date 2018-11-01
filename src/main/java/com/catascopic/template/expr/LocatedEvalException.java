package com.catascopic.template.expr;

import com.catascopic.template.TemplateEvalException;

@SuppressWarnings("serial")
public class LocatedEvalException extends RuntimeException {

	public LocatedEvalException(TemplateEvalException cause,
			TopLevelTerm expression) {
		super(String.format("at line %d, column %d: %s",
				expression.lineNumber() + 1,
				expression.columnNumber() + 1,
				expression), cause);
	}

}
