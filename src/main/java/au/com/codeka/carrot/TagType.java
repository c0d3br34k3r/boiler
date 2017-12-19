package au.com.codeka.carrot;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import au.com.codeka.carrot.tag.EchoTag;
import au.com.codeka.carrot.tag.ElseTag;
import au.com.codeka.carrot.tag.EndTag;
import au.com.codeka.carrot.tag.ForTag;
import au.com.codeka.carrot.tag.IfTag;
import au.com.codeka.carrot.tag.IncludeTag;
import au.com.codeka.carrot.tag.SetTag;
import au.com.codeka.carrot.tag.Tag;

/**
 * Contains a collection of tags that will be matched when parsing a template.
 */
public enum TagType {

	ECHO("echo") {

		@Override
		public Tag create() {
			return new EchoTag();
		}
	},

	IF("if") {

		@Override
		public Tag create() {
			return new IfTag();
		}
	},

	FOR("for") {

		@Override
		public Tag create() {
			return new ForTag();
		}
	},

	ELSE("else") {

		@Override
		public Tag create() {
			return new ElseTag();
		}
	},

	SET("set") {

		@Override
		public Tag create() {
			return new SetTag();
		}
	},

	INCLUDE("include") {

		@Override
		public Tag create() {
			return new IncludeTag();
		}

	},

	END("end") {

		@Override
		public Tag create() {
			return EndTag.END;
		}
	};

	private final String tagName;

	TagType(String tagName) {
		this.tagName = tagName;
	}
	
	public abstract Tag create();
	
	private static final Map<String, TagType> LOOKUP;
	
	static {
		Builder<String, TagType> builder = ImmutableMap.builder();
		for (TagType type : values()) {
			builder.put(type.tagName, type);
		}
	}
	
	public static TagType get(String name) {
		TagType type = LOOKUP.get(name);
	}

}
