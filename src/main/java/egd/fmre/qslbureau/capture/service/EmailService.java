package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.dto.EmailDataDto;
import egd.fmre.qslbureau.capture.dto.EmailRepresentativeDTO;
import egd.fmre.qslbureau.capture.exception.SendMailException;

public interface EmailService {

    boolean sendMail(EmailDataDto emailData) throws SendMailException;

    boolean sendMailRepresentative(EmailRepresentativeDTO emailRepresentative) throws SendMailException;
}
