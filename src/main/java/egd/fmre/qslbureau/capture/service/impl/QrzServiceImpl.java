package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.qrz.QRZDatabaseDAO;
import egd.fmre.qslbureau.capture.dto.qrz.QrzSessionDTO;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.exception.QrzException;
import egd.fmre.qslbureau.capture.repo.QrzsessionRepository;
import egd.fmre.qslbureau.capture.service.QrzService;
import egd.fmre.qslbureau.capture.util.QrzUtil;

@Service
public class QrzServiceImpl implements QrzService {
	@Value("${QRZ.SESSIONTTL}")
	private int qrzsessionttlindays;

	@Value("${QRZ.USERNAME}")
	private String username;

	@Value("${QRZ.PASSWORD}")
	private String password;

	@Autowired
	QrzsessionRepository qrzsessionRepository;

	@Override
	public Qrzsession getSession() throws QrzException {
		Qrzsession qrzsession = qrzsessionRepository.getLastSession();
		if (qrzsession == null) {
			QrzSessionDTO qrzSessionDTO = QrzUtil.getNewQrzSession(username, password);
			qrzsession = QrzUtil.parse(qrzSessionDTO);
			qrzsessionRepository.save(qrzsession);
		}
		return qrzsession;
	}
	
	@Override
	public Boolean checkCallsignOnQrz(Qrzsession qrzsession, String callsign) throws QrzException {
		@SuppressWarnings("unused")
		QRZDatabaseDAO res = QrzUtil.callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzsession.getKey(), callsign));
		return true;
	}
}
