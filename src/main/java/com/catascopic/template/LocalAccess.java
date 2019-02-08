package com.catascopic.template;

import java.nio.file.Path;
import java.util.Map;

interface LocalAccess {

	Object get(String name);

	Map<String, Object> scopedLocals();

	Path path();

}
