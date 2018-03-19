package au.com.codeka.carrot;

import java.nio.charset.Charset;
import java.util.Map;

import au.com.codeka.carrot.expr.Func;
import au.com.codeka.carrot.expr.Values;
import au.com.codeka.carrot.expr.Builtin;

public class TemplateEngine {

	private final Bindings globalBindings;
	private final ParseCache cache;
	private final Charset charset;
	private final Map<String, Func> functions;

	public TemplateEngine(Bindings globalBindings, Charset charset) {
		test(Builtin.class);
	}

	<F extends Enum<F> & Func> void test(Class<F> funcs) {
		for (F function : funcs.getEnumConstants()) {
			functions.put(Values.separatorToCamel(function.name()), function);
		}
	}

}
