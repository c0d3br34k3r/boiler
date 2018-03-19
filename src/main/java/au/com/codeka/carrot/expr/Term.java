package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

/**
 * A generic term.
 *
 * @author Marten Gajda
 */
public interface Term {

	Object evaluate(Scope scope);

}
