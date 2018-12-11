package com.catascopic.template;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

interface LocalAccess {

	Object get(String name);

	void collectLocals(Map<String, Object> locals);
	
	void collectPaths(List<Path> paths);

}
