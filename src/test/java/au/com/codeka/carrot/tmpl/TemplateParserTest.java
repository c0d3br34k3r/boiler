package au.com.codeka.carrot.tmpl;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.catascopic.template.CarrotException;
import com.catascopic.template.Configuration;
import com.catascopic.template.parse.Node;
import com.catascopic.template.parse.TemplateParser;
import com.catascopic.template.parse.TagNode;
import com.catascopic.template.parse.TemplateParser;
import com.catascopic.template.parse.TextNode;
import com.catascopic.template.resource.ResourcePointer;
import com.catascopic.template.tag.EchoTag;
import com.catascopic.template.tag.IfTag;
import com.google.common.io.LineReader;

/**
 * Tests for {@link TemplateParser}.
 */
@RunWith(JUnit4.class)
public class TemplateParserTest {
	@Test
	public void testEmptyTree() {
		Node node = parseTemplate("");
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren()).isEmpty();
	}

	@Test
	public void testSingleFixedToken() {
		Node node = parseTemplate("Hello World");
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren()).hasSize(1);
		assertThat(node.getChildren().consume(0)).isInstanceOf(TextNode.class);
		assertThat(((TextNode) node.getChildren().consume(0)).getContent()).isEqualTo("Hello World");
	}

	@Test
	public void testFixedCommentFixed() {
		Node node = parseTemplate("Hello{# foo #}World");
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren()).hasSize(2);
		assertThat(node.getChildren().consume(0)).isInstanceOf(TextNode.class);
		assertThat(((TextNode) node.getChildren().consume(0)).getContent()).isEqualTo("Hello");
		assertThat(node.getChildren().consume(1)).isInstanceOf(TextNode.class);
		assertThat(((TextNode) node.getChildren().consume(1)).getContent()).isEqualTo("World");
	}

	@Test
	public void testFixedEchoFixed() {
		Node node = parseTemplate("Hello{{ foo }}World");
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren()).hasSize(3);
		assertThat(node.getChildren().consume(0)).isInstanceOf(TextNode.class);
		assertThat(((TextNode) node.getChildren().consume(0)).getContent()).isEqualTo("Hello");
		assertThat(node.getChildren().consume(1)).isInstanceOf(TagNode.class);
		assertThat(((TagNode) node.getChildren().consume(1)).getTag()).isInstanceOf(EchoTag.class);
		assertThat(node.getChildren().consume(2)).isInstanceOf(TextNode.class);
		assertThat(((TextNode) node.getChildren().consume(2)).getContent()).isEqualTo("World");
	}

	@Test
	public void testIfFixedEnd() {
		Node node = parseTemplate("{% if foo %}Hello World{% end %}");
		assertThat(node.getChildren()).isNotNull();
		assertThat(node.getChildren()).hasSize(1);
		assertThat(node.getChildren().consume(0)).isInstanceOf(TagNode.class);

		TagNode ifNode = (TagNode) node.getChildren().consume(0);
		assertThat(ifNode.getTag()).isInstanceOf(IfTag.class);

		assertThat(ifNode.getChildren()).isNotNull();
		assertThat(ifNode.getChildren()).hasSize(1);
		assertThat(ifNode.getChildren().consume(0)).isInstanceOf(TextNode.class);
		assertThat(((TextNode) ifNode.getChildren().consume(0)).getContent()).isEqualTo("Hello World");
	}

	private Node parseTemplate(String input) {
		TemplateParser templateParser = new TemplateParser(new Configuration.Builder().build());
		try {
			Node node =
					templateParser.parse(new TemplateParser(
							new LineReader(new ResourcePointer(null), new StringReader(input))));
			assertThat(node).isNotNull();
			return node;
		} catch (CarrotException e) {
			throw new RuntimeException(e);
		}
	}
}
