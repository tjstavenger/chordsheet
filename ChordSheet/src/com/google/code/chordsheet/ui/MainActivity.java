package com.google.code.chordsheet.ui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;
import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.ChordSheetUtility;
import com.google.code.chordsheet.utility.SetUtility;

public class MainActivity extends Activity {
	private ChordSheetListFragment chordSheetListFragment;
	private SetListFragment setListFragment;

	/**
	 * Add Songs & Plalists tabs
	 */
	private void initializeTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab songsTab = actionBar.newTab().setText("Songs");
		this.chordSheetListFragment = new ChordSheetListFragment();
		songsTab.setTabListener(new LibraryTabListener(chordSheetListFragment));
		actionBar.addTab(songsTab);

		Tab setsTab = actionBar.newTab().setText("Sets");
		this.setListFragment = new SetListFragment();
		setsTab.setTabListener(new LibraryTabListener(setListFragment));
		actionBar.addTab(setsTab);
	}

	/**
	 * Reload the {@link ChordSheet} list as the files/titles may have changed
	 * (during edit).
	 * 
	 * @see android.app.Fragment#onActivityResult(int, int,
	 *      android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		chordSheetListFragment.loadChordSheets(ActivityDataUtility.getInstance().getCurrentFolder());
	}

	/**
	 * Initialize activity.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityDataUtility.getInstance().setContext(getApplicationContext());		
		setContentView(R.layout.activity_main);

		initializeTabs();
	}

	/**
	 * Create options menu.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
		case R.id.menu_create_chordsheet:
			ChordSheetUtility.createNewChordSheet(this);
			handled = true;
			break;

		case R.id.menu_create_set:
			SetUtility.createNewSet(this);
			handled = true;
			break;

		default:
			handled = super.onMenuItemSelected(featureId, item);
			break;
		}

		return handled;
	}
}
