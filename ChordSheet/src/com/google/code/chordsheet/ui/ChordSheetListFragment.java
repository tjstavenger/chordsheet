package com.google.code.chordsheet.ui;

import java.io.File;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.FileItem;
import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.ChordSheetListFilter;
import com.google.code.chordsheet.utility.ChordSheetUtility;
import com.google.code.chordsheet.utility.FileItemComparator;
import com.google.code.chordsheet.utility.FileUtility;

/**
 * Search for and display chordpro files.
 */
public class ChordSheetListFragment extends ListFragment {
	private ArrayAdapter<FileItem> files;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());

		getListView().setBackgroundColor(getResources().getColor(R.color.black));
		getListView().setDivider(getResources().getDrawable(R.color.gray));
		getListView().setDividerHeight(1);

		this.files = new ArrayAdapter<FileItem>(getActivity(), R.layout.text_view);
		setListAdapter(files);

		loadChordSheets(FileUtility.getBaseFolder());
	}

	/**
	 * Reload the {@link ChordSheet} as the files/titles may have changed
	 * (during edit).
	 * 
	 * @see android.app.Fragment#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		loadChordSheets(ActivityDataUtility.getInstance().getCurrentFolder());
	}

	/**
	 * Load all sheets in the given folder.
	 * 
	 * @param folder
	 */
	public void loadChordSheets(File folder) {
		if (folder.exists()) {
			ActivityDataUtility.getInstance().setCurrentFolder(folder);
			files.clear();

			if (!FileUtility.getBaseFolder().equals(folder)) {
				files.add(new FileItem(folder.getParentFile()));
			}

			for (File file : folder.listFiles(new ChordSheetListFilter())) {
				files.add(new FileItem(file));
			}
		}

		files.sort(new FileItemComparator());
	}

	/**
	 * Create long press context menu.
	 * 
	 * @see android.app.Fragment#onCreateContextMenu(android.view.ContextMenu,
	 *      android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		FileItem fileItem = files.getItem(info.position);

		if (!fileItem.isDirectory()) {
			getActivity().getMenuInflater().inflate(R.menu.chordsheet_context, menu);
		}
	}

	/**
	 * Perform action on long press context menu selection.
	 * 
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;

		// Get the selected ChordSheet
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FileItem fileItem = files.getItem(info.position);
		final ChordSheet sheet = fileItem.getSheet();

		switch (item.getItemId()) {
		case R.id.chordsheet_add_to_set:
			ChordSheetUtility.addChordSheetToSet(getActivity(), sheet);
			handled = true;
			break;

		case R.id.chordsheet_edit:
			ChordSheetUtility.editChordSheet(getActivity(), sheet);
			handled = true;
			break;

		case R.id.chordsheet_remove:
			fileItem.getFile().delete();
			loadChordSheets(ActivityDataUtility.getInstance().getCurrentFolder());
			handled = true;
			break;

		default:
			handled = super.onContextItemSelected(item);
			break;
		}

		return handled;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * On click of an item, either recurse into the directory or load the song.
	 * </p>
	 * 
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		FileItem fileItem = files.getItem(position);

		if (fileItem.isDirectory()) {
			loadChordSheets(fileItem.getFile());
		} else {
			ActivityDataUtility.getInstance().reset();
			ActivityDataUtility.getInstance().getSheets().add(fileItem.getSheet());

			Intent nextScreen = new Intent(getActivity().getApplicationContext(), ChordSheetViewActivity.class);
			startActivityForResult(nextScreen, 0);
		}
	}

}
