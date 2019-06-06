package com.catascopic.template;

import java.util.Map;

interface LocalAccess extends Lookup {

	void collect(Map<String, Object> collected);

}
