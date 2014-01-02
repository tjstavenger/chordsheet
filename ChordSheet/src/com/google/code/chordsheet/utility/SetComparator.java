package com.google.code.chordsheet.utility;

import java.util.Comparator;

import com.google.code.chordsheet.entity.Set;

public class SetComparator implements Comparator<Set> {

	@Override
	public int compare(Set lhs, Set rhs) {
		return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
	}

}
