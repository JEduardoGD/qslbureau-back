package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.CallsignDatecontactDto;
import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Slot;

public interface ContactBitacoreService {

	ContactBitacore saveContact(Contact contact, Slot slot, Representative representative);

	List<CallsignDatecontactDto> getContactOnCallsignList(List<String> callsigns);

	List<CallsignDatecontactDto> findBySlot(Slot slot);

	List<ContactBitacore> findEntityBySlot(Slot slot);

	List<ContactBitacore> migrateContactBitacore(List<ContactBitacore> contactBitacoreList, Slot newSlot);
}
