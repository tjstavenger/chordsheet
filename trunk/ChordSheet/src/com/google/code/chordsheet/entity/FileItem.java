package com.google.code.chordsheet.entity;

import java.io.File;

public class FileItem {
	private File file = null;
	private ChordSheet sheet = null;

	public FileItem(File file) {
		this.file = file;

		if (!file.isDirectory()) {
			this.sheet = new ChordSheet(file);
		}
	}

	public boolean isDirectory() {
		return sheet == null && file.isDirectory();
	}

	public File getFile() {
		return file;
	}

	public ChordSheet getSheet() {
		return sheet;
	}

	public String toString() {
		if (isDirectory()) {
			return file.getName() + "/";
		} else {
			return sheet.toString();
		}
	}
}
