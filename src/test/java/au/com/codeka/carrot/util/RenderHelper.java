package au.com.codeka.carrot.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.catascopic.template.CarrotEngine;
import com.catascopic.template.CarrotException;
import com.catascopic.template.Configuration;
import com.catascopic.template.Scope;
import com.catascopic.template.bindings.MapBindings;
import com.catascopic.template.parse.TagParser;
import com.catascopic.template.parse.TagParser;

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
		TagParser.parse(new TagParser(new StringReader(content)), config)
				.render(new CarrotEngine(config), writer, new Scope(new MapBindings(bindingsMap)));
		return writer.toString();
	}

}
