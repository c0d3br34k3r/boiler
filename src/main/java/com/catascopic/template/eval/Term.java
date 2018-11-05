package com.catascopic.template.eval;

import com.catascopic.template.Scope;

public interface Term {

	Object evaluate(Scope scope);

}
