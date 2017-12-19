package au.com.codeka.carrot.tag;

/**
 * The "end" tag, for all tags that end blocks.
 */
public class EndTag extends Tag {
	
	public static final Tag END = new EndTag();
	
	private EndTag() {}
}
