package com.catascopic.template.parse;

import java.io.IOException;

import com.catascopic.template.Location;
import com.catascopic.template.Scope;

final class NewlineNode implements Node {

	private static final Node NEWLINE = new NewlineNode();

	@Override
	public void render(Appendable writer, Scope scope) throws IOException {
		writer.append(scope.newLine());
	}

	static Tag getTag(Location location) {
		return new Tag(location) {
			@Override
			void handle(TemplateParser parser) {
				parser.add(NEWLINE);
			}
		};
	}

}
