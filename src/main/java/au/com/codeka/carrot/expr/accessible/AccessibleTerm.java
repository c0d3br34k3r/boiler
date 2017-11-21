package au.com.codeka.carrot.expr.accessible;

import javax.annotation.Nonnull;

import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.expr.Term;

/**
 * @author Marten Gajda
 */
public interface AccessibleTerm extends Term {
	@Nonnull
	Callable callable(@Nonnull Configuration config, @Nonnull Scope scope) throws CarrotException;
}
