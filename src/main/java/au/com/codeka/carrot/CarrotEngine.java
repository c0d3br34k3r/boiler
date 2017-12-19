package au.com.codeka.carrot;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import au.com.codeka.carrot.bindings.MapBindings;
import au.com.codeka.carrot.helpers.HtmlHelper;
import au.com.codeka.carrot.resource.ResourceLocator;
import au.com.codeka.carrot.resource.ResourceName;
import au.com.codeka.carrot.tmpl.Node;
import au.com.codeka.carrot.tmpl.TemplateParser;
import au.com.codeka.carrot.tmpl.parse.Parser;

/**
 * {@link CarrotEngine} is the root of the carrot system. You create an instance
 * of this, make it global or static, load templates and process them from here.
 */
public class CarrotEngine {

	private final Configuration config;
	private final MapBindings globalBindings;
	private final ParseCache parseCache;
//	private final TemplateParser templateParser;

	/**
	 * Constructs a new {@link CarrotEngine} with the given
	 * {@link Configuration}.
	 *
	 * <p>
	 * The configuration is immutable, so you should create it with all of the
	 * settings you need first.
	 *
	 * @param config The {@link Configuration} to construct this engine with.
	 */
	public CarrotEngine(Configuration config) {
		this(config, new MapBindings.Builder());
	}

	/**
	 * Constructs a new {@link CarrotEngine} with the given
	 * {@link Configuration} and initial set of global bindings.
	 *
	 * <p>
	 * The configuration is immutable, so you should create it with all of the
	 * settings you need first.
	 *
	 * @param config The {@link Configuration} to construct this engine with.
	 * @param globalBindingsBuilder A {@link MapBindings.Builder} that you can
	 *        pre-configure with some global objects that you want to access
	 *        from every template rendered by this engine.
	 */
	public CarrotEngine(Configuration config, MapBindings.Builder globalBindingsBuilder) {
		this.config = config;
		// TODO: ???
		this.globalBindings = globalBindingsBuilder
				.set("html", new HtmlHelper())
				.build();
		this.parseCache = new ParseCache(config);
//		this.templateParser = new TemplateParser(config);
	}

	/**
	 * @return The {@link Configuration}. The configuration is mutable, so you
	 *         are able to modify settings on the value returned here and they
	 *         will take effect on the current {@link CarrotEngine}.
	 */
	public Configuration getConfig() {
		return config;
	}

	/**
	 * @return A map of the global variables. These bindings will be accessible
	 *         in all templates processed by this {@link CarrotEngine}.
	 */
	public MapBindings getGlobalBindings() {
		return globalBindings;
	}

	/**
	 * Process the template with the given filename, writing the results to the
	 * given {@link Writer}.
	 *
	 * @param writer A {@link Writer} to write the results of processing the
	 *        given template to.
	 * @param file The {@link ResourceName} of the template file, which will be
	 *        located by our configured {@link ResourceLocator}.
	 * @param scope The {@link Scope} we're rendering into.
	 *
	 * @throws CarrotException Thrown if any errors occur.
	 */
	public void process(
			Writer writer,
			Path file,
			Scope scope) throws CarrotException {
		Node node = parseCache.getNode(file);
		if (node == null) {
			Reader reader = Files.newBufferedReader(file, config.getCharset());
			node = TemplateParser.parse(new Parser(reader), config);
			parseCache.addNode(file, node);
		}
		try {
			node.render(this, writer, scope);
		} catch (IOException e) {
			throw new CarrotException(e, node.getPointer());
		}
	}

	/**
	 * Process the template with the given filename, writing the results to the
	 * given {@link Writer}.
	 *
	 * @param writer A {@link Writer} to write the results of processing the
	 *        given template to.
	 * @param path
	 * @param bindings A mapping of string to variables that make up the
	 *        bindings for this template.
	 *
	 * @throws CarrotException Thrown if any errors occur.
	 */
	public void process(
			Writer writer,
			Path path,
			Bindings bindings) throws CarrotException {
		Scope scope = new Scope(globalBindings);
		Preconditions.checkNotNull(bindings);
		if (bindings != null) {
			scope.push(bindings);
		}
		process(writer, path, scope);
	}

	/**
	 * Process the template with the given filename, and returns the result as a
	 * string.
	 *
	 * @param file
	 * @param bindings A mapping of string to variables that make up the
	 *        bindings for this template.
	 * @return The processed template, as a string.
	 *
	 * @throws CarrotException Thrown if any errors occur.
	 */
	public String process(Path file, @Nullable Bindings bindings) throws CarrotException {
		StringWriter writer = new StringWriter();
		process(writer, file, bindings);
		return writer.getBuffer().toString();
	}

}
