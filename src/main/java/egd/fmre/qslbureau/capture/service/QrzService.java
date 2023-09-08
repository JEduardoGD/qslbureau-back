package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.exception.QrzException;

public interface QrzService {

	Qrzsession getSession() throws QrzException;

	Boolean checkCallsignOnQrz(Qrzsession qrzsession, String callsign) throws QrzException;

    List<Qrzreg> getQrzregOf(List<Qsl> qsls);

}
