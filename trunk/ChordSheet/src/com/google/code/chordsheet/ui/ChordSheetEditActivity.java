package com.google.code.chordsheet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;
import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.KeyUtility;

public class ChordSheetEditActivity extends Activity {
	private EditText editText;
	private ChordSheet sheet;

	/**
	 * Insert text at the current cursor position, then reposition the cursor
	 * within the new text based on the index given in newPosition.
	 * 
	 * @param text
	 *            {@link CharSequence} to insert
	 * @param newPosition
	 *            int index within the text to insert where cursor will be
	 *            positioned
	 */
	private void insertText(CharSequence text, int newPosition) {
		int start = Math.min(editText.getSelectionStart(), editText.getSelectionEnd());
		int end = Math.max(editText.getSelectionStart(), editText.getSelectionEnd());
		editText.getText().replace(start, end, text, 0, text.length());
		editText.setSelection(start + newPosition);
	}

	/**
	 * Save the changes to current {@link Set} to file.
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		sheet.save(editText.getText().toString());

		super.onBackPressed();
	}

	/**
	 * Initialize the activity.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chordsheet_edit);

		this.sheet = ActivityDataUtility.getInstance().getCurrentSheet();
		setTitle(sheet.getTitle());

		this.editText = (EditText) findViewById(R.id.chord_sheet_edit_text);
		editText.setText(sheet.getChordProText());
	}

	/**
	 * Create options menu.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_chordsheet_edit, menu);
		return true;
	}

	/**
	 * Perform an action when a menu option is selected, either transposing the
	 * chords or inserting a ChordPro directive.
	 * 
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
		case R.id.menu_chordsheet_transpose:
			transpose();
			handled = true;
			break;

		case R.id.menu_chordpro_chorus:
		case R.id.menu_chordpro_tab:
			// position after {soc} or {sot}
			insertText(item.getTitle(), 5);
			handled = true;
			break;

		case R.id.menu_chordpro_chord:
		case R.id.menu_chordpro_comment:
		case R.id.menu_chordpro_comment_italic:
		case R.id.menu_chordpro_subtitle:
		case R.id.menu_chordpro_title:
			// position before last char
			insertText(item.getTitle(), item.getTitle().length() - 1);
			handled = true;
			break;

		default:
			handled = super.onMenuItemSelected(featureId, item);
			break;
		}

		return handled;
	}

	/**
	 * Display a dialog to transpose current edited {@link ChordSheet} from one
	 * key to another.
	 */
	private void transpose() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.transpose);
		View view = (View) getLayoutInflater().inflate(R.layout.transpose_view, null);
		builder.setView(view);

		String key = KeyUtility.guessKey(editText.getText().toString());
		int keysArray;

		if (KeyUtility.isMajorKey(key)) {
			keysArray = R.array.major_chords;
		} else {
			keysArray = R.array.minor_chords;
		}

		final Spinner originalKey = (Spinner) view.findViewById(R.id.original_key);
		ArrayAdapter<CharSequence> originalAdapter = ArrayAdapter.createFromResource(this, keysArray,
				android.R.layout.simple_spinner_item);
		originalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		originalKey.setAdapter(originalAdapter);
		originalKey.setSelection(originalAdapter.getPosition(key));

		final Spinner transposedKey = (Spinner) view.findViewById(R.id.transposed_key);
		ArrayAdapter<CharSequence> transposedAdapter = ArrayAdapter.createFromResource(this, keysArray,
				android.R.layout.simple_spinner_item);
		transposedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		transposedKey.setAdapter(transposedAdapter);
		transposedKey.setSelection(transposedAdapter.getPosition(key));

		builder.setView(view);
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String transposed = KeyUtility.transpose(editText.getText().toString(),
						(String) originalKey.getSelectedItem(), (String) transposedKey.getSelectedItem());
				editText.setText(transposed);
			}
		});
		builder.create().show();
	}
}
