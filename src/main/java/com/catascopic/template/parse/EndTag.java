package com.catascopic.template.parse;

import com.catascopic.template.Location;

class EndTag implements Tag {

	EndTag(Location location) {

	}

	@Override
	public void handle(TemplateParser parser) {
		parser.endBlock();
	}

	@Override
	public String toString() {
		return "end";
	}

}
