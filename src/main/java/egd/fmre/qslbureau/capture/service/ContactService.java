package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.dto.ContactDataDto;
import egd.fmre.qslbureau.capture.enums.ContactEmailEnum;
import egd.fmre.qslbureau.capture.exception.ContactServiceException;

public interface ContactService {

	ContactDataDto findActiveForCallsign(String callsign) throws ContactServiceException;

	ContactDataDto findActiveById(Integer contactId);

	ContactEmailEnum callForUpdateEamilForCallsign(String callsign);

	ContactDataDto updateEamilForCallsign(String callsign) throws ContactServiceException;
}
