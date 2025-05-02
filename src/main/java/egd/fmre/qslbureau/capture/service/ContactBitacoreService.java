package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;
import egd.fmre.qslbureau.capture.entity.Representative;

public interface ContactBitacoreService {

	ContactBitacore saveContact(Contact contact, Representative representative);
}
