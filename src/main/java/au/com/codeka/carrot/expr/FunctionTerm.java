package au.com.codeka.carrot.expr;

import java.util.List;

import com.google.common.collect.Lists;

import au.com.codeka.carrot.Params;
import au.com.codeka.carrot.Scope;

class FunctionTerm implements Term {

	private String name;
	private List<Term> params;

	FunctionTerm(String name, List<Term> params) {
		this.name = name;
		this.params = params;
	}

	@Override
	public Object evaluate(Scope scope) {
		return scope.getFunction(name).apply(new Params(Lists.transform(params, scope)));
	}

}
