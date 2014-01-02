package com.google.code.chordsheet.entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.google.code.chordsheet.utility.FileUtility;
import com.google.code.chordsheet.utility.StringUtility;

public class Set {
	private static final String SET_EXTENSION = ".set";

	private File file;
	private String title;
	private List<ChordSheet> sheets = new ArrayList<ChordSheet>();

	public Set(String title) {
		this.title = title;

		// TODO move to SetUtility?
		String filename = FileUtility.cleanFilename(title) + SET_EXTENSION;
		this.file = new File(FileUtility.getBaseFolder(), filename);
	}

	public Set(File file) {
		this.file = file;
		load();
	}

	/**
	 * Load the set from {@link #file}.
	 */
	public void load() {
		try {
			LineIterator lineIterator = FileUtils.lineIterator(file, StringUtility.FILE_ENCODING);

			while (lineIterator.hasNext()) {
				String line = lineIterator.nextLine();
				String clean = StringUtility.clean(line);

				if (StringUtils.isBlank(title)) {
					this.title = clean;
				} else {
					sheets.add(new ChordSheet(FileUtility.getRelativeFile(clean)));
				}
			}
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * Save the set to {@link #file}.
	 */
	public void save() {
		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(title);
			writer.newLine();

			for (ChordSheet sheet : sheets) {
				writer.write(FileUtility.getRelativePath(sheet.getFile()));
				writer.newLine();
			}
		} catch (IOException e) {
			// ignore
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	/**
	 * Delete the {@link File} associated with this {@link Set}.
	 */
	public void delete() {
		file.delete();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return getTitle();
	}

	public List<ChordSheet> getSheets() {
		return sheets;
	}

	public void setSheets(List<ChordSheet> sheets) {
		this.sheets = sheets;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
}
