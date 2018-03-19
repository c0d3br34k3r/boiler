package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

/**
 * A binary {@link Term}. It has a left and a right (Sub-){@link Term} as well
 * as a {@link BinaryOperator}.
 *
 * @author Marten Gajda
 */
class BinaryTerm implements Term {

	private final Term left;
	private final BinaryOperator operator;
	private final Term right;

	BinaryTerm(Term left, BinaryOperator operation, Term right) {
		this.left = left;
		this.operator = operation;
		this.right = right;
	}

	@Override
	public Object evaluate(Scope scope) {
		return operator.apply(left.evaluate(scope), new LazyValue(scope, right));
	}

	@Override
	public String toString() {
		return String.format("[%s %s %s]", left, operator, right);
	}

}
