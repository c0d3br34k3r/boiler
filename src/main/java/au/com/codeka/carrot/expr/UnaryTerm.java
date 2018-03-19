package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

/**
 * An unary {@link Term}. It has an {@link UnaryOperator} and a {@link Term} to
 * which the operator is applied.
 *
 * @author Marten Gajda
 */
class UnaryTerm implements Term {

	private final UnaryOperator operator;
	private final Term term;

	public UnaryTerm(UnaryOperator operation, Term term) {
		this.operator = operation;
		this.term = term;
	}

	@Override
	public Object evaluate(Scope scope) {
		return operator.apply(term.evaluate(scope));
	}

	@Override
	public String toString() {
		return String.format("[%s %s]", operator, term);
	}

}
