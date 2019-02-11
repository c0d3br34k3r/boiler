package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

class TagCleaner {

	private List<Tag> tags = new ArrayList<>();
	private int safeLength = 0;
	private Tag cleanTag;
	private State state = State.START;

	void whitespace(Tag tag) {
		tags.add(tag);
	}

	void text(Tag tag) {
		tags.add(tag);
		state = State.NOT_CLEAN;
	}

	void statement(Tag tag) {
		tags.add(tag);
		if (state == State.START) {
			state = State.CLEAN;
			cleanTag = tag;
		} else {
			state = State.NOT_CLEAN;
		}
	}

	void comment() {
		if (state != State.NOT_CLEAN) {
			state = State.CLEAN;
		}
	}

	void endLine() {
		if (state == State.CLEAN) {
			clean();
		} else {
			tags.add(NewlineNode.NEWLINE);
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
		tags.subList(safeLength, tags.size()).clear();
		if (cleanTag != null) {
			tags.add(cleanTag);
			cleanTag = null;
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
