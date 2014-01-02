package com.google.code.chordsheet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;

import com.google.code.chordsheet.R;
import com.google.code.chordsheet.entity.ChordSheet;
import com.google.code.chordsheet.entity.Set;
import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.ChordSheetUtility;

public class ChordSheetViewActivity extends Activity {
	/**
	 * Handle touch events in {@link WebView} to swipe between songs in
	 * {@link Set}.
	 */
	private class ChordSheetGestureListener extends SimpleOnGestureListener {

		/**
		 * If next/previous chord sheet available, change to it.
		 * 
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent,
		 *      android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			ActivityDataUtility data = ActivityDataUtility.getInstance();
			boolean newSheet = false;

			if (e1.getX() > e2.getX() && data.hasNextSheet()) {
				data.nextSheet();
				newSheet = true;
			} else if (e1.getX() < e2.getX() && data.hasPreviousSheet()) {
				data.previousSheet();
				newSheet = true;
			}

			if (newSheet) {
				loadSong();
			}

			return false;
		}

	}

	/**
	 * Handle touch events in {@link WebView} to swipe between songs in
	 * {@link Set}.
	 */
	private class WebViewOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

	}

	private static final String BASE_URL = "file:///android_asset/";

	private static final String MIME_TYPE = "text/html";

	private static final String ENCODING = "UTF-8";

	private GestureDetector gestureDetector;

	/**
	 * Load the currently selected {@link ChordSheet} into the web view.
	 */
	private void loadSong() {
		ChordSheet sheet = ActivityDataUtility.getInstance().getCurrentSheet();
		String html;

		if (sheet == null) {
			html = "<html><body><p>No song to display</p></body></html>";
		} else {
			html = sheet.getHtml(true);
		}

		WebView webView = (WebView) findViewById(R.id.chordsheet_content);
		webView.loadDataWithBaseURL(BASE_URL, html, MIME_TYPE, ENCODING, null);
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

		loadSong();
	}

	/**
	 * Perform action on long press context menu selection.
	 * 
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;
		ChordSheet sheet = ActivityDataUtility.getInstance().getCurrentSheet();

		switch (item.getItemId()) {
		case R.id.chordsheet_add_to_set:
			ChordSheetUtility.addChordSheetToSet(this, sheet);
			handled = true;
			break;

		case R.id.chordsheet_edit:
			ChordSheetUtility.editChordSheet(this, sheet);
			handled = true;
			break;

		default:
			handled = super.onContextItemSelected(item);
			break;
		}

		return handled;
	}

	/**
	 * Initialize the activity.
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();
		this.gestureDetector = new GestureDetector(getApplicationContext(), new ChordSheetGestureListener());

		setContentView(R.layout.activity_chordsheet_view);

		WebView webView = (WebView) findViewById(R.id.chordsheet_content);
		webView.getSettings().setDisplayZoomControls(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setOnTouchListener(new WebViewOnTouchListener());

		registerForContextMenu(webView);

		loadSong();
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

		getMenuInflater().inflate(R.menu.chordsheet_edit_context, menu);
	}
}
