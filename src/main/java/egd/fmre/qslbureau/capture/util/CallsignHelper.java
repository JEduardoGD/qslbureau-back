package egd.fmre.qslbureau.capture.util;

import egd.fmre.qslbureau.capture.dto.QslDto;

public abstract class CallsignHelper {
	private static final String EMPTY_STRING = "";

	public static void sanitize(QslDto qslDto) {
		if (qslDto != null) {
			qslDto.setTo(cleanCallsign(qslDto.getTo()));
			qslDto.setVia(cleanCallsign(qslDto.getVia()));
		}
	}

	private static String cleanCallsign(String callsign) {
		if (callsign == null) {
			return null;
		}
		callsign = callsign.trim();
		if (EMPTY_STRING.equals(callsign)) {
			return null;
		}
		return callsign.toUpperCase();
	}
}
