package testing;

import org.junit.Test;


import common.metadata.KeyRange;
import junit.framework.TestCase;

public class AdditionalTest extends TestCase {

	/**
	 * Tests that the {@link KeyRange#isInRange(String) isInRange} method
	 * runs correctly for "normal" ranges (where end > start).
	 */
	@Test
	public void testInRange() {
		final String start = "00";
		final String end = "AA";
		KeyRange range = new KeyRange(start, end);
		
		// A value obviously in the range
		assertTrue(range.isInRange("01"));
		// Start is included in the range
		assertTrue(range.isInRange(start));
		// End is not included in the range
		assertFalse(range.isInRange(end));
		// A value completely outside of the range
		assertFalse(range.isInRange("FF"));
		assertFalse(range.isInRange("AB"));
	}

	/**
	 * Tests that the {@link KeyRange#isInRange(String) isInRange} method
	 * runs correctly for ranges which "wrap around" the hash circle, i.e.
	 * end < start.
	 */
	@Test
	public void testInRangeBorder() {
		final String start = "CD";
		final String end = "10";
		
		KeyRange range = new KeyRange(start, end);
		
		// The start is considered in the range
		assertTrue(range.isInRange(start));
		// A value in the range
		assertTrue(range.isInRange("CE"));
		// The largest key value is also in the range
		assertTrue(range.isInRange("FF"));
		// ..as is the smallest
		assertTrue(range.isInRange("00"));
		// A value strictly less than the end is in the range
		assertTrue(range.isInRange("09"));
		// The end is considered outside of the range
		assertFalse(range.isInRange(end));
		// Values higher than the range's end are not in it.
		assertFalse(range.isInRange("11"));
		// A value strictly less than the range's beginning is not in it.
		assertFalse(range.isInRange("CC"));
	}
	
	}
}
