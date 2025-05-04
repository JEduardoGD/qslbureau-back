package egd.fmre.qslbureau.capture.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class RgbUtil {
	private static final int INT_30 = 30;
	private static final String[] pasosColores = { "#00FF00", "#91EB4D", "#CDCD1F", "#FFFF00", "#FF9933", "#FF5E00", "#FF0000" };

	public static String getRgbFor(long diasTranscurridos) {
		diasTranscurridos = diasTranscurridos < 0 ? 0 : diasTranscurridos;
		BigDecimal diasTranscurridosBd = BigDecimal.valueOf(diasTranscurridos);
		BigDecimal equivalencia = diasTranscurridosBd.multiply(BigDecimal.valueOf(pasosColores.length));
		equivalencia = equivalencia.divide(BigDecimal.valueOf(INT_30), RoundingMode.HALF_UP);
		int equivalenciaInt = equivalencia.intValue();
		if (equivalenciaInt >= pasosColores.length) {
			return pasosColores[pasosColores.length - 1];
		}
		return pasosColores[equivalenciaInt];
	}
}
