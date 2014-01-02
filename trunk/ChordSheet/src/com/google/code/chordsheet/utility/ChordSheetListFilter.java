package com.google.code.chordsheet.utility;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Accept directories and chordpro files
 */
public class ChordSheetListFilter implements FilenameFilter {
	private static final String[] CHORDPRO_FILES = { "cho", "crd", "chordpro", "chopro" };

	@Override
	public boolean accept(File dir, String filename) {
		File file = new File(dir, filename);
		String extension = FilenameUtils.getExtension(filename);

		return file.isDirectory() || ArrayUtils.contains(CHORDPRO_FILES, extension);
	}
}
