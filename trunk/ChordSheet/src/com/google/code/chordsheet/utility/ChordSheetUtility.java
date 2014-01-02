package com.google.code.chordsheet.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;
import com.google.code.chordsheet.ui.ChordSheetEditActivity;

public class ChordSheetUtility {
	private static AlertDialog DIALOG = null;

	/**
	 * Provide a selection list of {@link Set} to add the given
	 * {@link ChordSheet} to.
	 * 
	 * @param activity
	 *            the current {@link Activity}
	 * @param sheet
	 *            the {@link ChordSheet} to add
	 */
	public static void addChordSheetToSet(final Activity activity, final ChordSheet sheet) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.add_to_set);
		ListView listView = (ListView) activity.getLayoutInflater().inflate(R.layout.set_list_view, null);
		final ArrayAdapter<Set> sets = SetUtility.loadSets(activity);
		listView.setAdapter(sets);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Set set = sets.getItem(position);
				set.getSheets().add(sheet);
				set.save();
				DIALOG.cancel();
			}
		});
		builder.setView(listView);
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.setNeutralButton(R.string.new_set, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				SetUtility.createNewSet(activity, sheet);
			}
		});
		DIALOG = builder.create();
		DIALOG.show();
	}

	/**
	 * Edit the given {@link ChordSheet}.
	 * 
	 * @param activity
	 *            current {@link Activity}
	 * @param sheet
	 *            {@link ChordSheet} to edit
	 */
	public static void editChordSheet(Activity activity, ChordSheet sheet) {
		ActivityDataUtility.getInstance().reset();
		ActivityDataUtility.getInstance().getSheets().add(sheet);
		Intent nextScreen = new Intent(activity.getApplicationContext(), ChordSheetEditActivity.class);
		activity.startActivityForResult(nextScreen, 0);
	}

	/**
	 * Create a new {@link ChordSheet}.
	 * 
	 * @param context
	 *            {@link Context} in which to create {@link AlertDialog}
	 */
	public static void createNewChordSheet(final Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.new_chordsheet_title);
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
				String title = text.getText().toString();
				ChordSheet sheet = new ChordSheet(title, ActivityDataUtility.getInstance().getCurrentFolder());
				sheet.save("{t:" + title + "}");

				ActivityDataUtility.getInstance().reset();
				ActivityDataUtility.getInstance().getSheets().add(sheet);

				Intent nextScreen = new Intent(context.getApplicationContext(), ChordSheetEditActivity.class);
				context.startActivityForResult(nextScreen, 0);
			}
		});
		builder.create().show();
	}

	/**
	 * Cannot instantiate.
	 */
	private ChordSheetUtility() {
		super();
	}
}
