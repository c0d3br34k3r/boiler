package com.catascopic.template;

import java.util.List;

public interface Context {

	Object get(String name);

	Object call(String functionName, List<Object> arguments);

}
