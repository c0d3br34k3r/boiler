package com.catascopic.template.expr;

import com.catascopic.template.Context;

public interface Term {

	Object evaluate(Context context);

}
