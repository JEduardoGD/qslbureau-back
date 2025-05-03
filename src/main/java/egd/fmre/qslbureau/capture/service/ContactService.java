package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.ContactDataDto;

public interface ContactService {

	ContactDataDto findActiveForCallsign(String callsign);

	ContactDataDto findActiveById(Integer contactId);

}
