package au.com.codeka.carrot.expr;

import au.com.codeka.carrot.Scope;

public interface Term {

	Object evaluate(Scope scope);

}
