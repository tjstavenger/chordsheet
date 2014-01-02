package com.google.code.chordsheet.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;

/**
 * Singleton to store data passed between activities.
 */
public class ActivityDataUtility {
	private static final ActivityDataUtility INSTANCE = new ActivityDataUtility();
	
	private Context context;
	private List<ChordSheet> sheets = new ArrayList<ChordSheet>();
	
	private int currentSheetIndex = 0;

	private Set set;
	
	private File currentFolder;


	/**
	 * Get the singleton instance
	 * 
	 * @return {@link ActivityDataUtility}
	 */
	public static ActivityDataUtility getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Singleton, cannot instantiate
	 */
	private ActivityDataUtility() {
		super();
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @return the currentFolder
	 */
	public File getCurrentFolder() {
		return currentFolder;
	}

	/**
	 * Return the current sheet.
	 * 
	 * @return {@link ChordSheet}
	 * @see #getSheets()
	 * @see #getCurrentSheetIndex()
	 */
	public ChordSheet getCurrentSheet() {
		ChordSheet sheet = null;
		
		if (currentSheetIndex >= 0 && currentSheetIndex < sheets.size()) {
			sheet = sheets.get(currentSheetIndex); 
		}
		
		return sheet;
	}

	/**
	 * @return the currentSheet
	 */
	public int getCurrentSheetIndex() {
		return currentSheetIndex;
	}

	/**
	 * @return the set
	 */
	public Set getSet() {
		return set;
	}

	/**
	 * @return the sheets
	 */
	public List<ChordSheet> getSheets() {
		return sheets;
	}

	/**
	 * True if it would be valid to call {@link #nextSheet()} and then
	 * {@link #getCurrentSheet()} without an error.
	 * 
	 * @return boolean
	 */
	public boolean hasNextSheet() {
		return sheets.size() > (currentSheetIndex + 1);
	}

	/**
	 * True if it would be valid to call {@link #previousSheet()} and then
	 * {@link #getCurrentSheet()} without an error.
	 * 
	 * @return boolean
	 */
	public boolean hasPreviousSheet() {
		return sheets.size() > 0 && currentSheetIndex > 0;
	}

	/**
	 * Increment {@link #currentSheetIndex}
	 */
	public void nextSheet() {
		currentSheetIndex++;
	}

	/**
	 * Decrement {@link #currentSheetIndex}
	 */
	public void previousSheet() {
		currentSheetIndex--;
	}

	/**
	 * Reset the data for reuse in the next activity.
	 */
	public void reset() {
		sheets.clear();
		this.currentSheetIndex = 0;
		
		this.set = null;
	}
	
	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @param currentFolder the currentFolder to set
	 */
	public void setCurrentFolder(File currentFolder) {
		this.currentFolder = currentFolder;
	}

	/**
	 * @param currentSheet
	 *            the currentSheet to set
	 */
	public void setCurrentSheetIndex(int currentSheet) {
		this.currentSheetIndex = currentSheet;
	}

	/**
	 * @param set the set to set
	 */
	public void setSet(Set set) {
		this.set = set;
	}

	/**
	 * @param sheets
	 *            the sheets to set
	 */
	public void setSheets(List<ChordSheet> sheets) {
		this.sheets = sheets;
	}
	
}
