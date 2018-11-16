package com.catascopic.template.parse;

public enum EndTag implements Tag {

	END;

	@Override
	public void build(TemplateParser parser) {
		parser.endBlock();
	}

}
