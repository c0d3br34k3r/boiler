package com.catascopic.template.parse;

import com.catascopic.template.Locatable;
import com.catascopic.template.Location;

abstract class Tag implements Locatable {

	private Location location;

	Tag(Location location) {
		this.location = location;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	abstract void handle(TemplateParser parser);

}
