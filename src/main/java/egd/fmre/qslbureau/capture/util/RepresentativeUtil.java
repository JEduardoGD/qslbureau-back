package egd.fmre.qslbureau.capture.util;

import java.util.List;
import java.util.stream.Collectors;

import egd.fmre.qslbureau.capture.dto.ContactDataDto;
import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.enums.SlotstatusEnum;
import egd.fmre.qslbureau.capture.exception.ContactServiceException;
import egd.fmre.qslbureau.capture.service.ContactService;
import egd.fmre.qslbureau.capture.service.RepresentativeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RepresentativeUtil {
	public static void setListOf(RepresentativeService representativeService, ContactService contactService, SlotDto slotDto) {
		List<Representative> representativeList = representativeService.getRepresentativesForCallsign(slotDto.getCallsignto());
		String listOf = null;
		if (representativeList != null && !representativeList.isEmpty()) {
			listOf = String.join(", ", representativeList.stream().map(r -> r.getUsername())
					.collect(Collectors.toList()));
		}
		if (slotDto != null) {
			slotDto.setListOf(listOf);
		}
		
		ContactDataDto contactData = null;
		try {
			contactData = contactService.findActiveForCallsign(slotDto.getCallsignto());
		} catch (ContactServiceException e) {
			log.error(e.getMessage());
		}
		if (contactData == null) {
			return;
		}
		slotDto.setIdContact(contactData.getIdContact() != null ? contactData.getIdContact() : null);
		if (contactData.getEmail() != null) {
			String email = contactData.getEmail();
			if (contactData.getWantemail() != null && contactData.getWantemail().equals(Boolean.FALSE)) {
				email = null;
			}
			if (slotDto.getQslsInSlot() <= 0) {
				email = null;
			}
			if (SlotstatusEnum.CREATED.getIdstatus() != slotDto.getStatusId()
					&& SlotstatusEnum.OPEN.getIdstatus() != slotDto.getStatusId()) {
				email = null;
			}
			slotDto.setEmail(email);
		}
	}
}
