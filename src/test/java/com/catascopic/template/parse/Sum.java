package com.catascopic.template.parse;

import java.io.IOException;
import java.nio.file.Paths;

import com.catascopic.template.Template;

public class Sum {

	public static void main(String[] args) throws IOException {
		System.out.println(Template.parse(Paths.get("sum.template")).render());
	}

}
