package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

interface Node {

	void render(Appendable writer, Scope scope) throws IOException;

}
