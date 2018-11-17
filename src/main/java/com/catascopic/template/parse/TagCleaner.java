package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

import com.catascopic.template.Location;

class TagCleaner {

	private List<Tag> tags = new ArrayList<>();
	private int safeLength = 0;
	private Tag onlyInstruction;
	private State state = State.START;

	void whitespace(Tag tag) {
		tags.add(tag);
	}

	void text(Tag tag) {
		tags.add(tag);
		state = State.NOT_CLEAN;
	}

	void instruction(Tag tag) {
		tags.add(tag);
		if (state == State.START) {
			state = State.CLEAN;
			onlyInstruction = tag;
		} else {
			state = State.NOT_CLEAN;
		}
	}

	void endLine(Tag newline) {
		if (state == State.CLEAN) {
			clean();
		} else {
			tags.add(newline);
		}
		safeLength = tags.size();
		state = State.START;
	}

	void endDocument() {
		if (state == State.CLEAN) {
			clean();
		}
	}

	private void clean() {
		tags.set(safeLength, onlyInstruction);
		tags.subList(safeLength + 1, tags.size()).clear();
	}

	List<Tag> result() {
		return tags;
	}

	private enum State {

		START,
		CLEAN,
		NOT_CLEAN;
	}

	void setLocation(Location location) {
		// TODO Auto-generated method stub
		
	}

}
