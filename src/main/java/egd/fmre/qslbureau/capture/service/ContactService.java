package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.ContactDataDto;
import egd.fmre.qslbureau.capture.enums.ContactEmailEnum;

public interface ContactService {

	ContactDataDto findActiveForCallsign(String callsign);

	ContactDataDto findActiveById(Integer contactId);

	ContactEmailEnum callForUpdateEamilForCallsign(String callsign);

	ContactDataDto updateEamilForCallsign(String callsign);
}
