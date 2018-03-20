package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

class ConditionalTerm implements Term {

	private final Term condition;
	private final Term first;
	private final Term second;

	public ConditionalTerm(Term condition, Term first, Term second) {
		this.condition = condition;
		this.first = first;
		this.second = second;
	}

	@Override
	public Object evaluate(Scope scope) {
		return Values.isTrue(condition.evaluate(scope))
				? first.evaluate(scope)
				: second.evaluate(scope);
	}

	@Override
	public String toString() {
		return String.format("(%s ? %s : %s)", condition, first, second);
	}

}
