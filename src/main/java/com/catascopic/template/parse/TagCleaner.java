package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

class TagCleaner {

	private List<Tag> tags = new ArrayList<>();
	private List<Tag> buffer = new ArrayList<>();
	private Tag onlyInstruction;
	private State state = State.START;

	void whitespace(Tag tag) {
		buffer.add(tag);
	}

	void text(Tag tag) {
		buffer.add(tag);
		state = State.NOT_CLEAN;
	}

	void instruction(Tag tag) {
		buffer.add(tag);
		if (state == State.START) {
			state = State.CLEAN;
			onlyInstruction = tag;
		} else {
			state = State.NOT_CLEAN;
		}
	}

	void endLine() {
		if (state == State.CLEAN) {
			tags.add(onlyInstruction);
		} else {
			tags.addAll(buffer);
			tags.add(SpecialNode.NEWLINE);
		}
		state = State.START;
		buffer = new ArrayList<>();
	}

	void endDocument() {
		if (state == State.CLEAN) {
			tags.add(onlyInstruction);
		} else {
			tags.addAll(buffer);
		}
	}

	List<Tag> result() {
		return tags;
	}

	private enum State {

		START,
		CLEAN,
		NOT_CLEAN;
	}

}
