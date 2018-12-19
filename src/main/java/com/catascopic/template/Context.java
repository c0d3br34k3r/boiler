package com.catascopic.template;

import java.util.List;

import com.catascopic.template.eval.Term;
import com.google.common.base.Function;

public interface Context extends Function<Term, Object> {

	Object get(String name);

	Object call(String functionName, List<Object> arguments);

}
