package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.ContactDataDto;
import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.enums.ContactEmailEnum;
import egd.fmre.qslbureau.capture.exception.QrzException;
import egd.fmre.qslbureau.capture.repo.ContactRepository;
import egd.fmre.qslbureau.capture.service.ContactService;
import egd.fmre.qslbureau.capture.service.QrzService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContactServiceImpl implements ContactService {
	
	@Autowired private ContactRepository contactRepository;
	@Autowired private QrzService        qrzService;

	@Override
	public ContactDataDto findActiveForCallsign(String callsign) {
		Contact contact = contactRepository.findActiveForCallsign(callsign);
		return map(contact);
	}
	
	@Override
	public ContactDataDto findActiveById(Integer contactId) {
		Contact contact = contactRepository.findActiveById(contactId);
		return map(contact);
	}

	private ContactDataDto map(Contact contact) {
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
	
	@Override
	public ContactEmailEnum callForUpdateEamilForCallsign(String callsign) {
		Contact contact = contactRepository.findActiveForCallsign(callsign);

		String email = null;
		if (contact != null) {
			email = contact.getEmail();
		}

		ContactDataDto contactDataDto = null;
		try {
			contactDataDto = qrzService.getEmailFromQrz(callsign);
		} catch (QrzException e) {
			log.error(e.getMessage());
		}

		if (contactDataDto == null) {
			return ContactEmailEnum.CANT_OBTAIN_EMAIL_FROM_QRZ;
		}

		if (email!=null && email.equalsIgnoreCase(contactDataDto.getEmail())) {
			return ContactEmailEnum.EMAIL_DOES_NOT_NEED_UPDATE;
		}
		
		return ContactEmailEnum.EMAIL_CAN_BE_UPDATE;
	}
	
	@Override
	@Transactional
	public ContactDataDto updateEamilForCallsign(String callsign) {
		Contact contact = contactRepository.findActiveForCallsign(callsign);

		ContactDataDto contactDataDto = null;
		try {
			contactDataDto = qrzService.getEmailFromQrz(callsign);
		} catch (QrzException e) {
			log.error(e.getMessage());
		}

		if (contactDataDto == null) {
			return null;
		}

		if (contact != null) {
			contact.setEnd(DateTimeUtil.getDateTime());
			contactRepository.save(contact);
		}
		
		Contact newContact = new Contact();
		newContact.setId(null);
		newContact.setName(contactDataDto.getName());
		newContact.setSurename(contactDataDto.getSurename());
		newContact.setCallsign(contactDataDto.getCallsign());
		newContact.setAddress(contactDataDto.getAddress());
		newContact.setEmail(contactDataDto.getEmail());
		newContact.setWhatsapp(contact != null ? contact.getWhatsapp() : null);
		newContact.setWantemail(contact != null ? contact.getWantemail() : true);
		newContact.setStart(DateTimeUtil.getDateTime());
		newContact.setEnd(null);
		contact = contactRepository.save(newContact);
		
		contactDataDto.setIdContact(contact.getId());
		contactDataDto.setEmail(contact.getEmail());
		contactDataDto.setWantemail(contact.getWantemail());
		contactDataDto.setStart(contact.getStart());
		return contactDataDto;
	}
}
