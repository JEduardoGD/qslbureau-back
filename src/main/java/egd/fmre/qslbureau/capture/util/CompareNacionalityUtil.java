package egd.fmre.qslbureau.capture.util;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.jsonburo.BuroDto;

public final class CompareNacionalityUtil {
    public static boolean isMexican(String callsing, List<BuroDto> buroDtoList) {
	BuroDto buroMexico = buroDtoList.stream().filter(buroDto -> buroDto.getPais().equalsIgnoreCase("MEXICO"))
		.findFirst().orElse(null);
	return buroMexico != null;
    }
}
