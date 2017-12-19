package au.com.codeka.carrot.expr.unary;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;

/**
 * An unary {@link Term}. It has an {@link UnaryOperator} and a {@link Term} to
 * which the operator is applied.
 *
 * @author Marten Gajda
 */
public final class UnaryTerm implements Term {

	private final UnaryOperator operator;
	private final Term term;

	public UnaryTerm(UnaryOperator operation, Term term) {
		this.operator = operation;
		this.term = term;
	}

	@Override
	public Object evaluate(Configuration config, Scope scope) throws CarrotException {
		return operator.apply(term.evaluate(config, scope));
	}

	@Override
	public String toString() {
		return String.format("[%s %s]", operator, term);
	}

}
