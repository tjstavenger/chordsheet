package com.google.code.chordsheet.entity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.code.chordsheet.utility.ActivityDataUtility;
import com.google.code.chordsheet.utility.FileUtility;
import com.google.code.chordsheet.utility.KeyUtility;
import com.google.code.chordsheet.utility.StringUtility;

public class ChordSheet {
	private static final String CHORD_SHEET_EXTENSION = ".chopro";

	private static final String[] TITLE = { "(?i).*\\{title:(.*)\\}.*", "(?i).*\\{t:(.*)\\}.*" };
	private static final String[] SUBTITLE = { "(?i).*\\{subtitle:(.*)\\}.*", "(?i).*\\{st:(.*)\\}.*" };
	private static final String[] COMMENT = { "(?i).*\\{comment:(.*)\\}.*", "(?i).*\\{c:(.*)\\}.*" };
	private static final String[] COMMENT_ITALIC = { "(?i).*\\{comment_italic:(.*)\\}.*", "(?i).*\\{ci:(.*)\\}.*" };
	private static final String[] START_OF_CHORUS = { "(?i).*\\{start_of_chorus\\}.*", "(?i).*\\{soc\\}.*" };
	private static final String[] END_OF_CHORUS = { "(?i).*\\{end_of_chorus\\}.*", "(?i).*\\{eoc\\}.*" };
	private static final String[] START_OF_TAB = { "(?i).*\\{start_of_tab\\}.*", "(?i).*\\{sot\\}.*" };
	private static final String[] END_OF_TAB = { "(?i).*\\{end_of_tab\\}.*", "(?i).*\\{eot\\}.*" };
	private static final String CHORD_BEFORE_FRAGMENT = "(?=\\[[^\\[]+\\])";

	private String title;
	private String html;
	private File file;

	public ChordSheet(String title, File folder) {
		this.title = title;

		// TODO move to ChordSheetUtility?
		String filename = FileUtility.cleanFilename(title) + CHORD_SHEET_EXTENSION;
		this.file = new File(FileUtility.getBaseFolder(), filename);
	}

	public ChordSheet(File file) {
		this.file = file;

		try {
			LineIterator lineIterator = FileUtils.lineIterator(file, StringUtility.FILE_ENCODING);

			while (lineIterator.hasNext()) {
				String line = lineIterator.nextLine();
				String clean = StringUtility.clean(line);

				if (isDirective(TITLE, clean)) {
					this.title = parseDirectiveText(clean);
					break;
				}
			}
		} catch (IOException e) {
			// ignore
		}

		if (StringUtils.isBlank(title)) {
			this.title = file.getName();
		}
	}

	/**
	 * Get the HTML representation for this {@link ChordSheet}.
	 * 
	 * @param internal
	 *            boolean true if internal assets should be used, false if they
	 *            should be embedded in HTML
	 * @return String HTML
	 */
	public String getHtml(boolean internal) {
		if (StringUtils.isBlank(html)) {
			try {
				this.html = parseChordPro(internal);
			} catch (Exception e) {
				this.html = "<html><body><p>Unable to load song</p><pre>" + ExceptionUtils.getStackTrace(e)
						+ "</pre></body></html>";
			}
		}

		return html;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Return true if the given line matches any of the given regex patterns.
	 * 
	 * @param patterns
	 *            String array of patterns to match
	 * @param line
	 *            String line to compare
	 * @return boolean
	 */
	private boolean isDirective(String[] patterns, String line) {
		boolean matches = false;

		for (int i = 0; i < patterns.length && !matches; i++) {
			matches = Pattern.matches(patterns[i], line);
		}

		return matches;
	}

	/**
	 * Read the ChordPro file and convert it to HTML.
	 * 
	 * @param internal
	 *            boolean true if internal assets should be used, false if they
	 *            should be embedded in HTML
	 * @return String HTML
	 */
	private String parseChordPro(boolean internal) throws IOException {
		StringBuilder html = new StringBuilder();
		html.append("<html>");
		html.append(StringUtility.LINE_SEPARATOR);
		html.append("<head>");
		html.append(StringUtility.LINE_SEPARATOR);
		html.append("<title>");
		html.append(getTitle());
		html.append("</title>");
		html.append(StringUtility.LINE_SEPARATOR);

		if (internal) {
			html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/song.css\"></link>");
			html.append(StringUtility.LINE_SEPARATOR);
			html.append("<script src=\"js/jquery-1.9.1.min.js\" type=\"text/javascript\"></script>");
			html.append(StringUtility.LINE_SEPARATOR);
			html.append("<script src=\"js/song.js\" type=\"text/javascript\"></script>");
		} else {
			html.append("<style type=\"text/css\">");
			html.append(StringUtility.LINE_SEPARATOR);
			InputStream css = ActivityDataUtility.getInstance().getContext().getAssets().open("css/song.css");
			LineIterator cssIterator = IOUtils.lineIterator(css, StringUtility.FILE_ENCODING);
			while (cssIterator.hasNext()) {
				html.append(cssIterator.next());
				html.append(StringUtility.LINE_SEPARATOR);
			}
			html.append("</style>");
		}

		html.append(StringUtility.LINE_SEPARATOR);
		html.append("</head>");
		html.append(StringUtility.LINE_SEPARATOR);
		html.append("<body>");
		html.append(StringUtility.LINE_SEPARATOR);

		LineIterator lineIterator = FileUtils.lineIterator(file, StringUtility.FILE_ENCODING);

		while (lineIterator.hasNext()) {
			String line = lineIterator.nextLine();
			String clean = StringUtility.clean(line);

			if (isDirective(TITLE, clean)) {
				this.title = parseDirectiveText(clean);
				html.append("<div class=\"title\">");
				html.append(title);
				html.append("</div>");
			} else if (isDirective(SUBTITLE, clean)) {
				html.append("<div class=\"subtitle\">");
				html.append(parseDirectiveText(clean));
				html.append("</div>");
			} else if (isDirective(COMMENT, clean)) {
				html.append("<div class=\"comment\">");
				html.append(parseDirectiveText(clean));
				html.append("</div>");
			} else if (isDirective(COMMENT_ITALIC, clean)) {
				html.append("<div class=\"comment-italic\">");
				html.append(parseDirectiveText(clean));
				html.append("</div>");
			} else if (isDirective(START_OF_CHORUS, clean)) {
				html.append("<div class=\"chorus\">");
			} else if (isDirective(END_OF_CHORUS, clean)) {
				html.append("</div>");
			} else if (isDirective(START_OF_TAB, clean)) {
				html.append("<div class=\"tab\">");
			} else if (isDirective(END_OF_TAB, clean)) {
				html.append("</div>");
			} else {
				html.append(parseLyric(clean));
			}

			html.append(StringUtility.LINE_SEPARATOR);
		}

		html.append("</body>");
		html.append(StringUtility.LINE_SEPARATOR);
		html.append("</html>");

		return html.toString();
	}

	/**
	 * Find any text after the directive ":" character.
	 * 
	 * @param line
	 *            String to parse
	 * @return directive text (trimmed)
	 */
	private String parseDirectiveText(String line) {
		String text = line;
		int index = StringUtils.indexOf(line, ":");

		if (index >= 0) {
			text = line.substring(index + 1, line.length() - 1);
		}

		return StringEscapeUtils.escapeHtml4(StringUtility.clean(text));
	}

	/**
	 * Split the line by whitespace and then parse each word for chords.
	 * 
	 * @param lyric
	 *            String one line of the lyrics
	 * @return String HTML for the line.
	 */
	private String parseLyric(String lyric) {
		StringBuilder html = new StringBuilder();

		html.append("<div class=\"line\">");
		html.append("<span class=\"textWidth\">");

		String[] words = StringUtils.splitPreserveAllTokens(lyric);

		if (words.length == 0) {
			html.append("<span class=\"word\"><span class=\"word-fragment\"><span class=\"lyric\"> </span></span></span>");
		} else {
			boolean lineHasChords = StringUtils.contains(lyric, '[');

			for (String word : words) {
				html.append(parseWord(word, lineHasChords));
			}
		}

		html.append("</span>");
		html.append("</div>");

		return html.toString();
	}

	/**
	 * Find chords in word and produce HTML to display chord over proper place
	 * in word.
	 * 
	 * @param word
	 *            String single word (may be empty)
	 * @return String HTML for word
	 */
	private String parseWord(String word, boolean lineHasChords) {
		StringBuilder html = new StringBuilder();

		html.append("<span class=\"word\">");

		String[] fragments = word.split(CHORD_BEFORE_FRAGMENT);

		if (fragments.length == 0) {
			html.append("<span class=\"word-fragment\">");

			if (lineHasChords) {
				html.append("<br/>");
			}

			html.append("<span class=\"lyric\"> </span></span>");
		} else {
			for (int i = 0; i < fragments.length; i++) {
				String fragment = fragments[i];
				html.append("<span class=\"word-fragment\">");
				int index = 0;

				if (fragment.startsWith("[")) {
					int end = fragment.indexOf(']');
					html.append("<span class=\"chord\">");
					html.append(StringEscapeUtils.escapeHtml4(fragment.substring(1, end)));
					html.append(" </span>");
					index = end + 1;
				}

				if (lineHasChords) {
					html.append("<br/>");
				}

				html.append("<span class=\"lyric\">");
				html.append(StringEscapeUtils.escapeHtml4(fragment.substring(index)));

				if (i == fragments.length - 1) {
					html.append(" ");
				}

				html.append("</span></span>");
			}
		}

		html.append("</span>");

		return html.toString();
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		return getTitle();
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Read the ChordPro text from file as a String.
	 * 
	 * @return String ChordPro text
	 */
	public String getChordProText() {
		String text;

		try {
			text = FileUtils.readFileToString(getFile(), StringUtility.FILE_ENCODING);
		} catch (IOException e) {
			text = StringUtils.EMPTY;
		}

		return text;
	}

	/**
	 * Save the given ChordPro content String to this ChordSheet's file.
	 * 
	 * @param content
	 *            ChordPro content to save
	 */
	public void save(String content) {
		try {
			this.html = null; // reset the HTML to force it to reload
			FileUtils.write(getFile(), content, StringUtility.FILE_ENCODING);
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * Guess the key by looking at the chords in the song.
	 * 
	 * @return
	 */
	public String findKey() {
		return KeyUtility.guessKey(getChordProText());
	}
}
