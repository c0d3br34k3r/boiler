package au.com.codeka.carrot.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.Scope;
import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.tmpl.TemplateParser;
import au.com.codeka.carrot.tmpl.parse.SegmentParser;

/**
 * Helpers for rendering templates in tests.
 */
public class RenderHelper {

	public static String render(String content, Object... bindings)
			throws CarrotException, IOException {

		Map<String, Object> bindingsMap = new HashMap<>();
		for (int i = 0; i < bindings.length; i += 2) {
			bindingsMap.put(bindings[i].toString(), bindings[i + 1]);
		}

		Configuration config = new Configuration.Builder().build();
		StringWriter writer = new StringWriter();
		TemplateParser.parse(new SegmentParser(new StringReader(content)), config)
				.render(new CarrotEngine(config), writer, new Scope(new MapBindings(bindingsMap)));
		return writer.toString();
	}

}
