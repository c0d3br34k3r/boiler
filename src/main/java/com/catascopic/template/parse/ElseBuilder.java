package com.catascopic.template.parse;

import com.catascopic.template.TemplateParseException;

interface ElseBuilder {

	Node build();

	TemplateParseException notAllowed();

}
