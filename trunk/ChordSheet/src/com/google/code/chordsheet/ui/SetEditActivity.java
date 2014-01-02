package com.google.code.chordsheet.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;
import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.SetUtility;

public class SetEditActivity extends ListActivity {

	private List<ChordSheet> sheets;
	private ArrayAdapter<ChordSheet> adapter;
	private TouchInterceptor touchInterceptor;
	private TouchInterceptor.DropListener dropListener = new TouchInterceptor.DropListener() {
		public void drop(int from, int to) {
			// Assuming that item is moved up the list
			int direction = -1;
			int loop_start = from;
			int loop_end = to;

			// For instance where the item is dragged down the list
			if (from < to) {
				direction = 1;
			}

			ChordSheet target = sheets.get(from);

			for (int i = loop_start; i != loop_end; i = i + direction) {
				sheets.set(i, sheets.get(i + direction));
			}

			sheets.set(to, target);

			reloadSheets();
		}
	};

	/**
	 * Save the changes to current {@link Set} to file.
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		ActivityDataUtility.getInstance().getSet().save();

		super.onBackPressed();
	}

	/**
	 * Perform action on long press context menu seletion.
	 * 
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;

		// Get the selected ChordSheet
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.set_item_remove:
			sheets.remove(info.position);
			reloadSheets();
			handled = true;
			break;

		default:
			handled = super.onContextItemSelected(item);
			break;
		}

		return handled;
	}

	/**
	 * Initialize activity.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_view);
		registerForContextMenu(getListView());

		getListView().setBackgroundColor(getResources().getColor(R.color.black));
		getListView().setDivider(getResources().getDrawable(R.color.gray));
		getListView().setDividerHeight(1);

		this.touchInterceptor = (TouchInterceptor) getListView();
		touchInterceptor.setDropListener(dropListener);
		registerForContextMenu(touchInterceptor);

		setTitle(ActivityDataUtility.getInstance().getSet().getTitle());

		this.sheets = ActivityDataUtility.getInstance().getSet().getSheets();
		this.adapter = new ArrayAdapter<ChordSheet>(this, R.layout.set_item_view, R.id.set_item_text, sheets);
		setListAdapter(adapter);
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

		getMenuInflater().inflate(R.menu.set_item_context, menu);
	}

	/**
	 * Create options menu.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_set_edit, menu);
		return true;
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Set set = ActivityDataUtility.getInstance().getSet();

		ActivityDataUtility.getInstance().setSheets(new ArrayList<ChordSheet>());
		ActivityDataUtility.getInstance().getSheets().addAll(set.getSheets());
		ActivityDataUtility.getInstance().setCurrentSheetIndex(position);

		Intent nextScreen = new Intent(getApplicationContext(), ChordSheetViewActivity.class);
		startActivity(nextScreen);
	}

	/**
	 * Perform an action when a menu option is selected, either creating a
	 * {@link ChordSheet} or {@link Set}.
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
		case R.id.set_export:
			SetUtility.exportSet(ActivityDataUtility.getInstance().getSet());
			handled = true;
			break;

		default:
			handled = super.onMenuItemSelected(featureId, item);
			break;
		}

		return handled;
	}

	/**
	 * Force the {@link TouchInterceptor} to reload the list of
	 * {@link ChordSheet}.
	 */
	private void reloadSheets() {
		touchInterceptor.removeViews(0, touchInterceptor.getCount());
		adapter.notifyDataSetChanged();
		touchInterceptor.invalidate();
	}

}
