package com.google.code.chordsheet.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;

public class SetUtility {
	private static final String[] SET_FILES = { "set" };
	private static final String HTML_EXTENSION = ".html";
	private static final String ZIP_EXTENSION = ".zip";

	/**
	 * Create a new {@link Set}.
	 * 
	 * @param context
	 *            {@link Context} in which to create {@link AlertDialog}
	 */
	public static void createNewSet(Context context) {
		createNewSet(context, null);
	}

	/**
	 * Create a new {@link Set}, adding the given {@link ChordSheet} to the new
	 * {@link Set}.
	 * 
	 * @param context
	 *            {@link Context} in which to create {@link AlertDialog}
	 * @param sheet
	 *            {@link ChordSheet} to add to new {@link Set}
	 */
	public static void createNewSet(Context context, final ChordSheet sheet) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.new_set_name);
		final EditText text = new EditText(context);
		builder.setView(text);
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Set set = new Set(text.getText().toString());

				if (sheet != null) {
					set.getSheets().add(sheet);
				}

				set.save();
			}
		});
		builder.create().show();
	}

	/**
	 * Export the HTML {@link ChordSheet} in one ZIP file.
	 * 
	 * @param set
	 *            {@link Set} to export
	 */
	public static void exportSet(Set set) {
		ZipOutputStream zos = null;

		try {
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
					FileUtility.getRelativeFile(FilenameUtils.getBaseName(set.getFile().getName()) + ZIP_EXTENSION))));

			for (ChordSheet sheet : set.getSheets()) {
				String filename = FilenameUtils.getBaseName(sheet.getFile().getName()) + HTML_EXTENSION;
				byte[] bytes = StringUtility.getBytes(sheet.getHtml(false));
				ZipEntry entry = new ZipEntry(filename);
				zos.putNextEntry(entry);
				zos.write(bytes);
				zos.closeEntry();
			}
		} catch (IOException e) {
			// Ignore
		} finally {
			IOUtils.closeQuietly(zos);
		}
	}

	/**
	 * Load an {@link ArrayAdapter} with the available {@link Set}.
	 */
	public static ArrayAdapter<Set> loadSets(Context context) {
		ArrayAdapter<Set> sets = new ArrayAdapter<Set>(context, R.layout.text_view);

		Collection<File> files = FileUtils.listFiles(FileUtility.getBaseFolder(), SET_FILES, false);

		for (File file : files) {
			sets.add(new Set(file));
		}

		sets.sort(new SetComparator());

		return sets;
	}

	/**
	 * Cannot instantiate
	 */
	private SetUtility() {
		super();
	}
}
