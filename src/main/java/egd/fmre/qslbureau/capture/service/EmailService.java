package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.dto.EmailDataDto;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.SendMailException;

public interface EmailService {

	public boolean sendMail(EmailDataDto emailData) throws SendMailException;
}
