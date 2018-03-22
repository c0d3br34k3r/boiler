package au.com.codeka.carrot.tag;

import static au.com.codeka.carrot.util.RenderHelper.render;
import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.catascopic.template.CarrotException;
import com.catascopic.template.tag.EchoTag;

/**
 * Tests for {@link EchoTag}.
 */
@RunWith(JUnit4.class)
public class EchoTagTest {

	@Test
	public void testMaths() throws CarrotException, IOException {
		assertThat(render("{{ a + 1 }}", "a", 2)).isEqualTo("3");
		assertThat(render("{{ 4 * a }}", "a", 3)).isEqualTo("12");
		assertThat(render("{{ a / b }}", "a", 10, "b", 5)).isEqualTo("2");
		assertThat(render("{{ a / b }}", "a", 10.1, "b", 5)).isEqualTo("2.02");
		assertThat(render("{{ a / (b + 1) }}", "a", 10.1, "b", 4)).isEqualTo("2.02");
		assertThat(render("{{ 'foo' + 'bar' }}", "a", 10.1, "b", 4)).isEqualTo("foobar");
	}

}
