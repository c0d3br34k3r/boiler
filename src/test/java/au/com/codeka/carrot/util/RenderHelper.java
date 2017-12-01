package au.com.codeka.carrot.util;

import java.util.HashMap;
import java.util.Map;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;
import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.resource.MemoryResourceLocator;

/**
 * Helpers for rendering templates in tests.
 */
public class RenderHelper {
	public static String render(String content, Object... bindings) throws CarrotException {
		CarrotEngine engine = new CarrotEngine(new Configuration.Builder()
				.setLogger(new Configuration.Logger() {
					@Override
					public void print(Configuration.Logger.Level level, String msg) {
						if (level.compareTo(Configuration.Logger.Level.DEBUG) > 0) {
							System.err.println(msg);
						}
					}
				})
				.setResourceLocator(new MemoryResourceLocator.Builder().add("index", content))
				.build());

		Map<String, Object> bindingsMap = new HashMap<>();
		for (int i = 0; i < bindings.length; i += 2) {
			bindingsMap.put(bindings[i].toString(), bindings[i + 1]);
		}

		return engine.process("index", new MapBindings(bindingsMap));
	}
}
