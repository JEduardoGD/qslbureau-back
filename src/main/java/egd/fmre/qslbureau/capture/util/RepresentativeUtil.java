package egd.fmre.qslbureau.capture.util;

import java.util.List;
import java.util.stream.Collectors;

import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.service.RepresentativeService;

public abstract class RepresentativeUtil {
	public static void setListOf(RepresentativeService representativeService, SlotDto slotDto) {
		List<Representative> representativeList = representativeService.getRepresentativesForCallsign(slotDto.getCallsignto());
		String listOf = null;
		if (representativeList != null && !representativeList.isEmpty()) {
			listOf = String.join(", ", representativeList.stream().map(r -> r.getName() + " " + r.getLastName())
					.collect(Collectors.toList()));
		}
		if (slotDto != null) {
			slotDto.setListOf(listOf);
		}
	}
}
