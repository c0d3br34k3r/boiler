package com.catascopic.template.parse;

enum EndTag implements Tag {

	END;

	@Override
	public void handle(TemplateParser parser) {
		parser.endBlock();
	}

}
