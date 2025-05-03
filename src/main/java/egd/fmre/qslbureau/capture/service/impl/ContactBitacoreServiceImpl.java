package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.repo.ContactBitacoreRepository;
import egd.fmre.qslbureau.capture.service.ContactBitacoreService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;

@Service
public class ContactBitacoreServiceImpl implements ContactBitacoreService {
	
	@Autowired ContactBitacoreRepository contactBitacoreRepository;
	
	@Override
	public ContactBitacore saveContact(Contact contact, Representative representative) {
		ContactBitacore contactBitacore = new ContactBitacore();
		contactBitacore.setContact(contact);
		contactBitacore.setRepresentative(representative);
		contactBitacore.setDatetime(DateTimeUtil.getDateTime());
		return contactBitacoreRepository.save(contactBitacore);
	}
}
