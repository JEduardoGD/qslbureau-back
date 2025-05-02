package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.ContactDataDto;
import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.repo.ContactRepository;
import egd.fmre.qslbureau.capture.service.ContactService;

@Service
public class ContactServiceImpl implements ContactService {
	@Autowired
	private ContactRepository contactRepository;

	@Override
	public ContactDataDto findActiveForCallsign(String callsign) {
		Contact contact = contactRepository.findActiveForCallsign(callsign);
		ContactDataDto contactDataDto = null;
		if (contact != null) {
			contactDataDto = new ContactDataDto();
			contactDataDto.setIdContact(contact.getId());
			contactDataDto.setName(contact.getName());
			contactDataDto.setSurename(contact.getSurename());
			contactDataDto.setCallsign(contact.getCallsign());
			contactDataDto.setAddress(contact.getAddress());
			contactDataDto.setEmail(contact.getEmail());
			contactDataDto.setWhatsapp(contact.getWhatsapp());
			contactDataDto.setWantemail(contact.getWantemail());
			contactDataDto.setStart(contact.getStart());
			contactDataDto.setEnd(contact.getEnd());
		}
		return contactDataDto;
	}
}
