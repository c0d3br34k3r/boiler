package com.catascopic.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SymbolTest {

	public static void main(String[] args) throws IOException {
		TemplateEngine engine = TemplateEngine.create(
				Settings.builder().addFunctions(Trig.class).build());

		int start = 30;

		Files.write(Paths.get("symbol.svg"), engine.render(Paths.get("symbol.template"),
				ImmutableMap.of("rectangleColor", "fcb670",
						"circleColor", "0022fe",
						"triangleColor", "ff59ad",
						"angles", ImmutableList.of(
								Math.toRadians(start),
								Math.toRadians(start + 120),
								Math.toRadians(start + 240)))).getBytes());
	}

	private enum Trig implements TemplateFunction {
		SIN {

			@Override
			public Object apply(Params params) {
				return Math.sin(params.getDouble(0));
			}
		},
		COS {

			@Override
			public Object apply(Params params) {
				return Math.cos(params.getDouble(0));
			}
		},
		TAN {

			@Override
			public Object apply(Params params) {
				return Math.tan(params.getDouble(0));
			}
		};
	}

}
