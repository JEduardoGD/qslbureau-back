package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.ContactDataDto;
import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.exception.QrzException;

public interface QrzService {

	Qrzsession getSession() throws QrzException;

    List<Qrzreg> getQrzregOf(List<Qsl> qsls);

	Boolean checkCallsignOnQrz(String callsign) throws QrzException;

	String getCountryOfCallsign(String callsign) throws QrzException;

	public ContactDataDto getEmailFromQrz(String callsign) throws QrzException;
}
