package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.exception.QrzException;

public interface QrzService {

	Qrzsession getSession() throws QrzException;

	Boolean checkCallsignOnQrz(Qrzsession qrzsession, String callsign) throws QrzException;

}
