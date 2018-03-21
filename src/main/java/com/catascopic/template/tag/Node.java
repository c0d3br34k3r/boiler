package com.catascopic.template.tag;

import java.io.IOException;
import java.io.Writer;

import com.catascopic.template.Scope;

public interface Node {

	void render(Writer writer, Scope scope) throws IOException;

}
