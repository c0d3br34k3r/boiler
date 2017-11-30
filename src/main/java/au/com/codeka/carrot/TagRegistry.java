package au.com.codeka.carrot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

import au.com.codeka.carrot.tag.BlockTag;
import au.com.codeka.carrot.tag.EchoTag;
import au.com.codeka.carrot.tag.ElseTag;
import au.com.codeka.carrot.tag.EndTag;
import au.com.codeka.carrot.tag.ExtendsTag;
import au.com.codeka.carrot.tag.ForTag;
import au.com.codeka.carrot.tag.IfTag;
import au.com.codeka.carrot.tag.IncludeTag;
import au.com.codeka.carrot.tag.SetTag;
import au.com.codeka.carrot.tag.Tag;

/**
 * Contains a collection of tags that will be matched when parsing a template.
 */
public class TagRegistry {

	public static Builder newBuilder() {
		return new Builder();
	}

	private Map<String, Supplier<? extends Tag>> lookup =
			new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private List<Entry> entries;

	private TagRegistry(Map<String, Supplier<? extends Tag>> lookup, List<Entry> entries) {
		this.lookup = lookup;
		this.entries = ImmutableList.copyOf(entries);
	}

	public Tag createTag(String tagName) {
		Supplier<? extends Tag> supplier = lookup.get(tagName);
		if (supplier != null) {
			return supplier.get();
		}
		for (Entry entry : entries) {
			if (entry.matcher.apply(tagName)) {
				return entry.creator.get();
			}
		}
		return null;
	}

	private static class Entry {

		private final Predicate<String> matcher;
		private final Supplier<? extends Tag> creator;

		public Entry(Predicate<String> matcher, Supplier<? extends Tag> creator) {
			this.matcher = matcher;
			this.creator = creator;
		}
	}

	public static class Builder {

		private Map<String, Supplier<? extends Tag>> lookup =
				new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		private List<Entry> entries = new ArrayList<>();

		public Builder() {
			// @formatter:off
			add("echo", new Supplier<Tag>() {
				@Override public Tag get() { return new EchoTag(); }
			});
			add("if", new Supplier<Tag>() {
				@Override public Tag get() { return new IfTag(); }
			});
			add("for", new Supplier<Tag>() {
				@Override public Tag get() { return new ForTag(); }
			});
			add("else", new Supplier<Tag>() {
				@Override public Tag get() { return new ElseTag(); }
			});
			add("extends", new Supplier<Tag>() {
				@Override public Tag get() { return new ExtendsTag(); }
			});
			add("block", new Supplier<Tag>() {
				@Override public Tag get() { return new BlockTag(); }
			});
			add("set", new Supplier<Tag>() {
				@Override public Tag get() { return new SetTag(); }
			});
			add("include", new Supplier<Tag>() {
				@Override public Tag get() { return new IncludeTag(); }
			});
			// @formatter:on
			add(new Predicate<String>() {
				@Override
				public boolean apply(String input) {
					return input.toLowerCase().startsWith("end");
				}
			}, new Supplier<Tag>() {
				@Override
				public Tag get() {
					return new EndTag();
				}
			});
		}

		public Builder add(String name, Supplier<? extends Tag> supplier) {
			lookup.put(name, supplier);
			return this;
		}

		public Builder add(Predicate<String> matcher, Supplier<? extends Tag> supplier) {
			entries.add(new Entry(matcher, supplier));
			return this;
		}

		public TagRegistry build(Configuration config) {
			return new TagRegistry(lookup, entries);
		}
	}

	private enum BaseTags implements Supplier<Tag> {
		ECHO("echo") {
			@Override
			public Tag get() {
				return new EchoTag();
			}
		},
		IF("if") {
			@Override
			public Tag get() {
				return new IfTag();
			}
		},
		FOR("for") {
			@Override
			public Tag get() {
				return new ForTag();
			}
		},
		ELSE("else") {
			@Override
			public Tag get() {
				return new ElseTag();
			}
		},
		EXTENDS("extends") {
			@Override
			public Tag get() {
				return new ExtendsTag();
			}
		},
		BLOCK("block") {
			@Override
			public Tag get() {
				return new BlockTag();
			}
		},
		SET("set") {
			@Override
			public Tag get() {
				return new SetTag();
			}
		},
		INCLUDE("include") {
			@Override
			public Tag get() {
				return new IncludeTag();
			}
		},
		END("end") {
			@Override
			public Tag get() {
				return new EndTag();
			}
		};

		private final String tagName;

		BaseTags(String tagName) {
			this.tagName = tagName;
		}
	}

}
