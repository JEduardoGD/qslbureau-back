package egd.fmre.qslbureau.capture.util;

import org.apache.commons.text.RandomStringGenerator;

public abstract class RandomUtils {

	public static String randomAlphabetic(final int firstNameLength) {
		return randomAlphabetic(firstNameLength, 'a', 'z');
	}

	// If you want to use the range
	public static String randomAlphabetic(final int firstNameLength, final char low, final char high) {
		return new RandomStringGenerator.Builder().withinRange(low, high).build().generate(firstNameLength);
	}
}
