package com.catascopic.template.parse;

public enum EndTag implements Tag {

	END;

	@Override
	public void handle(TemplateParser parser) {
		parser.endBlock();
	}

}
