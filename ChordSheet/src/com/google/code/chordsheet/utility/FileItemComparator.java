package com.google.code.chordsheet.utility;

import java.util.Comparator;

import com.google.code.chordsheet.entity.FileItem;

/**
 * Directories sort first, then everything in string case insensitive,
 * alphanumeric order
 */
public class FileItemComparator implements Comparator<FileItem> {

	@Override
	public int compare(FileItem lhs, FileItem rhs) {
		int result = 0;

		if (lhs.isDirectory() && !rhs.isDirectory()) {
			result = -1;
		} else if (!lhs.isDirectory() && rhs.isDirectory()) {
			result = 1;
		} else {
			result = lhs.toString().compareToIgnoreCase(rhs.toString());
		}

		return result;
	}
}
