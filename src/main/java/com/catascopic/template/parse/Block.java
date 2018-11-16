package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

interface Block {

	void render(Appendable writer, Scope scope) throws IOException;

	void renderElse(Appendable writer, Scope scope) throws IOException;

}
