package com.catascopic.template;

import java.io.IOException;

interface Resolver {

	void renderTemplate(String fileName, Appendable writer,
			Scope scope, Assigner assigner) throws IOException;

	void renderTextFile(String path, Appendable writer) throws IOException;

	TemplateFunction getFunction(String name);

}
