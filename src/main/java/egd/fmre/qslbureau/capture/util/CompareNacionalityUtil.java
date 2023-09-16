package egd.fmre.qslbureau.capture.util;

public final class CompareNacionalityUtil {
	public static boolean isMexican(String callsing, String mxPrefixes) {
		String[] mxPrefixesArray = mxPrefixes.split("\\,");
		for (String mxPrefix : mxPrefixesArray) {
			if (callsing.toUpperCase().startsWith(mxPrefix.toUpperCase())) {
				return true;
			}
		}
		return false;
	}
}
