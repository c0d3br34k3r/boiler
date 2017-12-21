package au.com.codeka.carrot.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.annotation.Nullable;

import au.com.codeka.carrot.CarrotEngine;
import au.com.codeka.carrot.CarrotException;
import au.com.codeka.carrot.Configuration;

/**
 * An implementation of {@link ResourceLocator} that loads files from the file
 * system.
 */
public class FileResourceLocator implements ResourceLocator {
	private final Configuration config;
	private final File baseFile;

	/**
	 * Constructs a new {@link FileResourceLocator} using the given
	 * {@link Configuration} and base path to search for resources in.
	 *
	 * @param config The {@link Configuration} you used to construct the
	 *        {@link CarrotEngine}.
	 * @param basePath The path path to search for resources in.
	 */
	public FileResourceLocator(Configuration config, String basePath) {
		this.config = config;
		this.baseFile = new File(basePath);
	}

	@Override
	public ResourceName findResource(@Nullable ResourceName parent, String name)
			throws CarrotException {
		File file = new File(name);
		if (file.isAbsolute()) {
			return new FileResourceName(null, file.getName(), file);
		}

		if (parent != null) {
			file = new File(((FileResourceName) parent).getFile(), name);
			if (file.exists() && file.isFile()) {
				return new FileResourceName(parent, name, file);
			}
		}

		file = new File(baseFile, name);
		if (file.exists() && file.isFile()) {
			return new FileResourceName(null, name, file);
		}

		throw new CarrotException(
				new FileNotFoundException("[parent = " + parent + "] [name = " + name + "] [base = "
						+ baseFile + "]"));
	}

	@Override
	public ResourceName findResource(String name) throws CarrotException {
		return findResource(null, name);
	}

	@Override
	public long getModifiedTime(ResourceName resourceName) throws CarrotException {
		return ((FileResourceName) resourceName).getFile().lastModified();
	}

	@Override
	public Reader getReader(ResourceName resourceName) throws CarrotException {
		try {
			return new InputStreamReader(
					new FileInputStream(((FileResourceName) resourceName).getFile()),
					config.getCharset());
		} catch (IOException e) {
			throw new CarrotException(e);
		}
	}

	/**
	 * A builder for {@link FileResourceLocator}.
	 */
	public static class Builder implements ResourceLocator.Builder {
		private String basePath;

		public Builder() {}

		public Builder(String basePath) {
			this.basePath = basePath;
		}

		public Builder setBasePath(String basePath) {
			this.basePath = basePath;
			return this;
		}

		@Override
		public ResourceLocator build(Configuration config) {
			return new FileResourceLocator(config, basePath);
		}
	}

	/**
	 * Our version of {@link ResourceName} that represents file system files.
	 */
	private static class FileResourceName extends AbstractResourceName {
		private final File file;

		public FileResourceName(@Nullable ResourceName parent, String name, File file) {
			super(parent, name);
			this.file = file;
		}

		public File getFile() {
			return file;
		}

		@Override
		public ResourceName getParent() {
			File parent = file.getParentFile();
			return new FileResourceName(null, parent.getName(), parent);
		}

		@Override
		public String toString() {
			return file.getAbsolutePath();
		}

		@Override
		public int hashCode() {
			return file.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof FileResourceName
					&& ((FileResourceName) other).file.equals(file);
		}
	}
}
