package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Scope;

interface Block {

	void renderContent(Appendable writer, Scope scope) throws IOException;
	
	void renderLinked(Appendable writer, Scope scope) throws IOException;

}
