package com.catascopic.template.expr;

import com.catascopic.template.Scope;

public interface Term {

	Object evaluate(Scope scope);

}
