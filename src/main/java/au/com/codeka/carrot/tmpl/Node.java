package au.com.codeka.carrot.tmpl;

import java.io.IOException;
import java.io.Writer;

import au.com.codeka.carrot.Scope;

public interface Node {

	void render(Writer writer, Scope scope) throws IOException;

}
