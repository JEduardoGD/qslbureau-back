package egd.fmre.qslbureau.capture.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.CallsignDatecontactDto;
import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.entity.ContactBitacore;
import egd.fmre.qslbureau.capture.entity.Representative;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.repo.ContactBitacoreRepository;
import egd.fmre.qslbureau.capture.service.ContactBitacoreService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;

@Service
public class ContactBitacoreServiceImpl implements ContactBitacoreService {
	
	@Autowired ContactBitacoreRepository contactBitacoreRepository;
	
	@Override
	public ContactBitacore saveContact(Contact contact, Slot slot, Representative representative) {
		ContactBitacore contactBitacore = new ContactBitacore();
		contactBitacore.setContact(contact);
		contactBitacore.setRepresentative(representative);
		contactBitacore.setSlot(slot);
		contactBitacore.setDatetime(DateTimeUtil.getDateTime());
		return contactBitacoreRepository.save(contactBitacore);
	}
	
	@Override
	public List<CallsignDatecontactDto> getContactOnCallsignList(List<String> callsigns) {
		return contactBitacoreRepository.getContactOnCallsignList(callsigns);
	}
	
	@Override
	public List<CallsignDatecontactDto> findBySlot(Slot slot) {
		List<ContactBitacore> contactBitacoreList = contactBitacoreRepository.findBySlot(slot);
		return contactBitacoreList.stream().map(contactBitacore -> {
			return new CallsignDatecontactDto(slot.getCallsignto(), slot.getId(), slot.getSlotNumber(), contactBitacore.getDatetime());
		}).sorted(Comparator.comparing(CallsignDatecontactDto::getDatetime).reversed()).collect(Collectors.toList());
	}
	
	@Override
	public List<ContactBitacore> findEntityBySlot(Slot slot) {
		return contactBitacoreRepository.findBySlot(slot);
	}
	
	@Override
	public List<ContactBitacore> migrateContactBitacore(List<ContactBitacore> contactBitacoreList, Slot newSlot) {
		List<ContactBitacore> contactBitacoreListNew = contactBitacoreList.stream().map(c -> {
			c.setSlot(newSlot);
			return c;
		}).collect(Collectors.toList());
		return contactBitacoreRepository.saveAll(contactBitacoreListNew);
	}
}
