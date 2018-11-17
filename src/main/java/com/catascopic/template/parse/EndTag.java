package com.catascopic.template.parse;

import com.catascopic.template.Location;

final class EndTag extends Tag {

	EndTag(Location location) {
		super(location);
	}

	@Override
	public void handle(TemplateParser parser) {
		parser.endBlock();
	}

}
