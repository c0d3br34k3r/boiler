package com.catascopic.template.parse;

public enum EndTag implements Tag {

	END;

	@Override
	public void build(BlockBuilder builder) {
		builder.end();
	}

}
