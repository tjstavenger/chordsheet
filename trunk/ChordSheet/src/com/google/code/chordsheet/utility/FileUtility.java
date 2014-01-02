package com.google.code.chordsheet.utility;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import android.os.Environment;

import com.google.code.chordsheet.entity.ChordSheet;

public class FileUtility {
	public static final String BASE_FILE_PATH = "ChordSheet";

	private static final String NON_WORD = "[^ \\.a-zA-Z_0-9]";

	/**
	 * Trim the string and remove non-word characters.
	 * 
	 * @param filename
	 *            String to clean
	 * @return cleaned filename
	 */
	public static String cleanFilename(String filename) {
		String clean = StringUtils.stripToEmpty(filename);
		clean = StringUtils.trim(clean);
		clean = clean.replaceAll(NON_WORD, StringUtils.EMPTY);

		return clean;
	}

	/**
	 * Get the {@link ChordSheet} {@link File} found in the
	 * {@link #getBaseFolder()} with the given relative path.
	 * 
	 * @param filePath
	 *            String path relative to {@link #getBaseFolder()}
	 * @return {@link File}
	 * 
	 * @see #getRelativePath()
	 */
	public static File getRelativeFile(String filePath) {
		return new File(getBaseFolder(), filePath);
	}

	/**
	 * Get the {@link File} to the shared ChordSheet folder.
	 * 
	 * @return {@link File}
	 */
	public static File getBaseFolder() {
		File dir = new File(Environment.getExternalStorageDirectory(), BASE_FILE_PATH);
		dir.mkdirs();

		return dir;
	}

	/**
	 * Get path of given {@link File} relative to {@link #getBaseFolder()}.
	 * 
	 * @return String relative path
	 */
	public static String getRelativePath(File file) {
		String fullPath = file.getAbsolutePath();
		String basePath = getBaseFolder().getAbsolutePath();

		return StringUtils.removeStart(fullPath, basePath);
	}

	private FileUtility() {
		super();
	}
}
