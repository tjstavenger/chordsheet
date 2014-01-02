package com.google.code.chordsheet.ui;

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
import com.google.code.chordsheet.entity.Set;
import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.SetUtility;

public class SetListFragment extends ListFragment {
	private ArrayAdapter<Set> sets;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());

		getListView().setBackgroundColor(getResources().getColor(R.color.black));
		getListView().setDivider(getResources().getDrawable(R.color.gray));
		getListView().setDividerHeight(1);

		this.sets = SetUtility.loadSets(getActivity());
		setListAdapter(sets);
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

		getActivity().getMenuInflater().inflate(R.menu.set_context, menu);
	}

	/**
	 * Perform action on long press context menu seletion.
	 * 
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;

		// Get the selected Set
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Set set = sets.getItem(info.position);

		switch (item.getItemId()) {
		case R.id.set_edit:
			ActivityDataUtility.getInstance().reset();
			ActivityDataUtility.getInstance().setSet(set);

			Intent nextScreen = new Intent(getActivity().getApplicationContext(), SetEditActivity.class);
			startActivity(nextScreen);

			handled = true;
			break;

		case R.id.set_export:
			SetUtility.exportSet(set);
			handled = true;
			break;

		case R.id.set_remove:
			sets.remove(set);
			set.delete();
			sets.notifyDataSetChanged();
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
	 * On click of an item, open the list of sheets in the set.
	 * </p>
	 * 
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Set set = sets.getItem(position);

		ActivityDataUtility.getInstance().reset();
		ActivityDataUtility.getInstance().getSheets().addAll(set.getSheets());

		Intent nextScreen = new Intent(getActivity().getApplicationContext(), ChordSheetViewActivity.class);
		startActivity(nextScreen);
	}

}
