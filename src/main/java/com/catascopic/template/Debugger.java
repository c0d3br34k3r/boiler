package com.catascopic.template;

import java.io.IOException;

public interface Debugger {

	void print(Location location, String message) throws IOException;

}
