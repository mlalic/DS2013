package common.metadata;

/**
 * The key range is represented by a half-open interval, namely
 * [start, end). 
 *
 */
public class KeyRange {
	
	private String start;
	private String end;
	
	public KeyRange(String start, String end) {
		if (start.length() != end.length()) {
			throw new IllegalArgumentException(
					"The strings representing the ranges need to be of the same length");
		}
		this.start = start;
		this.end = end;
	}
	
	public boolean isInRange(String key) {
		// If two hex numbers are represented and strings,
		// doing a lexicographic comparison is equivalent to
		// a number magnitude comparison iff the two strings
		// have an equal number of digits.
		if (key.length() != start.length()) {
			throw new IllegalArgumentException(
					"The length of the key needs to be the same as the length of the start and end parameters of the range");
		}

		// start is included
		final boolean greaterThanStart = start.compareTo(key) <= 0;
		// end is excluded
		final boolean lessThanEnd = key.compareTo(end) < 0;
		
		if (end.compareTo(start) <= 0) {
			// Special case when the range "wraps-around" the ring.
			// It means that the start of the range is found at a position
			// with a higher magnitude than the end of the range.
			
			// The key is found in this range when it is any of the numbers higher than the
			// start (since the highest possible one is in the range, taking the wrap-around
			// assumption).
			// The key is also found in the range when it is any of the numbers smaller
			// than the end, since the smallest one is also in the range given the assumption.
			return greaterThanStart || lessThanEnd;
		}		

		return greaterThanStart && lessThanEnd;
	}
	
	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}


}
