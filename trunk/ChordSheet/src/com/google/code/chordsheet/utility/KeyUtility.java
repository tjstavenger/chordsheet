package com.google.code.chordsheet.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Support guessing a key.
 */
public class KeyUtility {
	private static final Map<String, String[]> KEYS = new HashMap<String, String[]>(26);

	// Weights must add up to 1
	private static final double FIRST_CHORD_WEIGHT = 0.2;
	private static final double LAST_CHORD_WEIGHT = 0.2;
	private static final double[] CHORD_WEIGHTS = new double[] { 0.15, 0.05, 0.075, 0.1, 0.1, 0.075, 0.05 };

	private static final String TRANSPOSE_START_PATTERN = "(\\[[^\\[]*)(";
	private static final String TRANSPOSE_END_PATTERN = ")([^\\[]*\\])";
	private static final Pattern CHORD_PATTERN = Pattern.compile("\\[([^\\[]+)\\]");
	private static final Pattern ROOT_CHORD = Pattern.compile("[A-G][#b]?m?(?:dim)?");
	private static final String MINOR = "m";
	private static final String DIM = "dim";
	private static final String[] TRANSPOSE_TEMP_CHORDS = { "T", "U", "V", "W", "X", "Y", "Z" };

	static {
		KEYS.put("C", new String[] { "C", "Dm", "Em", "F", "G", "Am", "Bdim" });
		KEYS.put("G", new String[] { "G", "Am", "Bm", "C", "D", "Em", "F#dim" });
		KEYS.put("D", new String[] { "D", "Em", "F#m", "G", "A", "Bm", "C#dim" });
		KEYS.put("A", new String[] { "A", "Bm", "C#m", "D", "E", "F#m", "G#dim" });
		KEYS.put("E", new String[] { "E", "F#m", "G#m", "A", "B", "C#m", "D#dim" });
		KEYS.put("B", new String[] { "B", "C#m", "D#m", "E", "F#", "G#m", "A#dim" });
		KEYS.put("F#", new String[] { "F#", "G#m", "A#m", "B", "C#", "D#m", "E#dim" });
		KEYS.put("Gb", new String[] { "Gb", "Abm", "Bbm", "Cb", "Db", "Ebm", "Fdim" });
		KEYS.put("Db", new String[] { "Db", "Ebm", "Fm", "Gb", "Ab", "Bbm", "Cdim" });
		KEYS.put("Ab", new String[] { "Ab", "Bbm", "Cm", "Db", "Eb", "Fm", "Gdim" });
		KEYS.put("Eb", new String[] { "Eb", "Fm", "Gm", "Ab", "Bb", "Cm", "Ddim" });
		KEYS.put("Bb", new String[] { "Bb", "Cm", "Dm", "Eb", "F", "Gm", "Adim" });
		KEYS.put("F", new String[] { "F", "Gm", "Am", "Bb", "C", "Dm", "Edim" });

		KEYS.put("Cm", new String[] { "Cm", "Ddim", "Eb", "Fm", "Gm", "Ab", "Bb" });
		KEYS.put("Gm", new String[] { "Gm", "Adim", "Bb", "Cm", "Dm", "Eb", "F" });
		KEYS.put("Dm", new String[] { "Dm", "Edim", "F", "Gm", "Am", "Bb", "C" });
		KEYS.put("Am", new String[] { "Am", "Bdim", "C", "Dm", "Em", "F", "G" });
		KEYS.put("Em", new String[] { "Em", "F#dim", "G", "Am", "Bm", "C", "D" });
		KEYS.put("Bm", new String[] { "Bm", "C#dim", "D", "Em", "F#m", "G", "A" });
		KEYS.put("F#m", new String[] { "F#m", "G#dim", "A", "Bm", "C#m", "D", "E" });
		KEYS.put("C#m", new String[] { "C#m", "D#dim", "E", "F#m", "G#m", "A", "B" });
		KEYS.put("G#m", new String[] { "G#m", "A#dim", "B", "C#m", "D#m", "E", "F#" });
		KEYS.put("Ebm", new String[] { "Ebm", "Fdim", "Gb", "Abm", "Bbm", "Cb", "Db" });
		KEYS.put("D#m", new String[] { "D#m", "E#dim", "F#", "G#m", "A#m", "B", "C#" });
		KEYS.put("Bbm", new String[] { "Bbm", "Cdim", "Db", "Ebm", "Fm", "Gb", "Ab" });
		KEYS.put("Fm", new String[] { "Fm", "Gdim", "Ab", "Bbm", "Cm", "Db", "Eb" });
	}

	/**
	 * Count the number of times the given value exists in the given list.
	 * 
	 * @param list
	 *            List of String to count instances in
	 * @param value
	 *            String value to search for
	 * @return int count
	 */
	private static int count(List<String> list, String value) {
		int count = 0;

		for (String string : list) {
			if (StringUtils.equals(string, value)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Get all the chords used in the song.
	 * 
	 * @return List of String chords
	 */
	public static List<String> getChords(String chordPro) {
		List<String> chords = new ArrayList<String>();
		Matcher matcher = CHORD_PATTERN.matcher(chordPro);

		while (matcher.find()) {
			chords.add(StringUtility.clean(matcher.group(1)));
		}

		return chords;
	}

	/**
	 * Guess the key of the song based on the chords. Weight the chords and take
	 * the highest score as the key.
	 * 
	 * @param chords
	 *            List of String chords
	 * @return String guessed key
	 */
	public static String guessKey(List<String> chords) {
		String guess = StringUtils.EMPTY;

		if (chords != null && !chords.isEmpty()) {
			// derive the "root" chord by removing all sus, 2, 4, 9, 11, etc...
			List<String> roots = new ArrayList<String>(chords.size());

			for (String chord : chords) {
				Matcher matcher = ROOT_CHORD.matcher(chord);
				if (matcher.find()) {
					roots.add(matcher.group());
				}
			}

			// Weight the chords to get the best guess key signature
			String first = roots.get(0);
			String last = roots.get(chords.size() - 1);

			Map<String, Double> keyWeights = new HashMap<String, Double>(KEYS.size());

			for (Map.Entry<String, String[]> key : KEYS.entrySet()) {
				String[] keyChords = key.getValue();
				double weight = 0;

				if (StringUtils.equals(keyChords[0], first)) {
					weight += FIRST_CHORD_WEIGHT;
				}

				if (StringUtils.equals(keyChords[0], last)) {
					weight += LAST_CHORD_WEIGHT;
				}

				for (int i = 0; i < keyChords.length; i++) {
					int count = count(roots, keyChords[i]);

					if (count > 0) {
						weight += (count * CHORD_WEIGHTS[i]);
					}
				}

				keyWeights.put(key.getKey(), weight);
			}

			List<Map.Entry<String, Double>> weights = new ArrayList<Map.Entry<String, Double>>(keyWeights.entrySet());
			Collections.sort(weights, new MapEntryComparator());
			guess = weights.get(0).getKey();
		}

		return guess;
	}

	/**
	 * Guess the key for the given ChordProd text.
	 * 
	 * @param chordPro
	 *            String ChordPro text
	 * @return String key
	 * 
	 * @see #guessKey(List)
	 */
	public static String guessKey(String chordPro) {
		List<String> chords = getChords(chordPro);

		return guessKey(chords);
	}

	/**
	 * Return true if the given key is major.
	 * 
	 * @param key
	 *            String key signature (root/I chord)
	 * @return boolean
	 */
	public static boolean isMajorKey(String key) {
		return !isMinorKey(key);
	}

	/**
	 * Return true if the given key is minor.
	 * 
	 * @param key
	 *            String key signature (root/I chord)
	 * @return boolean
	 */
	public static boolean isMinorKey(String key) {
		return StringUtils.endsWith(key, MINOR);
	}

	/**
	 * Transpose the given ChordPro text from the given original key into the
	 * given transposed key.
	 * 
	 * @param chordPro
	 *            String text in ChordPro format
	 * @param originalKey
	 *            String key signature (root/I chord) of original song
	 * @param transposedKey
	 *            String key signature (root/I chord) to transpose to
	 */
	public static String transpose(String chordPro, String originalKey, String transposedKey) {
		String[] originalChords = KEYS.get(originalKey);
		String[] transposedChords = KEYS.get(transposedKey);
		String transposed = chordPro;

		for (int i = 0; i < originalChords.length; i++) {
			Pattern pattern = Pattern.compile(TRANSPOSE_START_PATTERN + cleanChord(originalChords[i])
					+ TRANSPOSE_END_PATTERN);
			Matcher matcher = pattern.matcher(transposed);

			if (matcher.find()) {
				// prevent recursively transposing chord with temporary
				transposed = matcher.replaceAll("$1" + TRANSPOSE_TEMP_CHORDS[i] + "$3");
			}
		}

		for (int i = 0; i < originalChords.length; i++) {
			Pattern pattern = Pattern.compile(TRANSPOSE_START_PATTERN + TRANSPOSE_TEMP_CHORDS[i] + TRANSPOSE_END_PATTERN);
			Matcher matcher = pattern.matcher(transposed);

			if (matcher.find()) {
				transposed = matcher.replaceAll("$1" + cleanChord(transposedChords[i]) + "$3");
			}
		}

		return transposed;
	}

	/**
	 * Remove the {@link #DIM} and {@link #MINOR} from the end of the chord.
	 * 
	 * @param chord
	 *            String chord to clean
	 * @return String cleaned chord
	 * 
	 * @see #transpose(String, String, String)
	 */
	private static String cleanChord(String chord) {
		String clean = chord;
		clean = StringUtils.removeEnd(clean, DIM);
		clean = StringUtils.removeEnd(clean, MINOR);

		return clean;
	}

	/**
	 * Sort the {@link Entry} of key chord weights, where the weights are sorted
	 * in descending order.
	 */
	private static class MapEntryComparator implements Comparator<Map.Entry<String, Double>> {

		@Override
		public int compare(Entry<String, Double> lhs, Entry<String, Double> rhs) {
			return lhs.getValue().compareTo(rhs.getValue()) * -1;
		}

	}
}
