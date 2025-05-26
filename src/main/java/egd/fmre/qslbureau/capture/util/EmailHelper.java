package egd.fmre.qslbureau.capture.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import egd.fmre.qslbureau.capture.dto.ContactDataDto;
import egd.fmre.qslbureau.capture.entity.Contact;
import egd.fmre.qslbureau.capture.exception.SendMailException;
import egd.fmre.qslbureau.capture.service.impl.EmailServiceImpl;

public abstract class EmailHelper {

	protected String loadEmailFile() throws SendMailException {
		Class<EmailServiceImpl> clazz = EmailServiceImpl.class;
		InputStream inputStream = clazz.getResourceAsStream("/email.html");
		try {
			return readFromInputStream(inputStream);
		} catch (IOException e) {
			throw new SendMailException(e);
		}
	}

	protected String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();
	}

	protected ContactDataDto map(Contact contact) {
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
