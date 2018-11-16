package com.catascopic.template.parse;

import java.util.ArrayList;
import java.util.List;

class TagCleaner {

	private List<Tag> tags = new ArrayList<>();
	private List<Tag> buffer = new ArrayList<>();
	private Tag onlyInstruction;
	private int instructionTagCount;

	void notClean() {
		instructionTagCount = 2;
	}
	
	void addTag(Tag tag) {
		b
	}

	void instructionTag(Tag tag) {
		onlyInstruction = tag;
		instructionTagCount++;
	}

	void endLine() {
		if (instructionTagCount == 1) {
			tags.add(onlyInstruction);
		} else {
			tags.addAll(buffer);
			tags.add(SpecialNode.NEWLINE);
		}
		instructionTagCount = 0;
		buffer = new ArrayList<>();
	}

}
