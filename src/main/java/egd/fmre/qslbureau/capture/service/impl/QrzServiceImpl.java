package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.qrz.QRZDatabaseDto;
import egd.fmre.qslbureau.capture.dto.qrz.QrzSessionDto;
import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.exception.QrzException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.repo.QrzregRepository;
import egd.fmre.qslbureau.capture.repo.QrzsessionRepository;
import egd.fmre.qslbureau.capture.service.QrzService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import egd.fmre.qslbureau.capture.util.QrzUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QrzServiceImpl implements QrzService {
    @Value("${QRZ.SESSIONTTL}")
    private int qrzsessionttlindays;

    @Value("${QRZ.USERNAME}")
    private String username;

    @Value("${QRZ.PASSWORD}")
    private String password;

    @Value("${QRZ.CALLSIGN.EXP}")
    private int qrzCallsignExp;

    @Autowired
    QrzsessionRepository qrzsessionRepository;

    @Autowired
    QrzregRepository qrzregRepository;

    @Override
    public Qrzsession getSession() throws QrzException {
        Qrzsession qrzsession = qrzsessionRepository.getLastSession();
        if (qrzsession == null) {
            QrzSessionDto qrzSessionDTO = QrzUtil.getNewQrzSession(username, password);
            qrzsession = QrzUtil.parse(qrzSessionDTO);
            qrzsessionRepository.save(qrzsession);
        }
        return qrzsession;
    }
    
    private List<Qrzreg> getActiveQrzregList(String callsign) {
        Calendar calendar = DateTimeUtil.getNowCalendar();
        calendar.add(Calendar.DATE, -qrzCallsignExp);
    	return qrzregRepository.findByCallsignInPeriod(callsign, calendar.getTime());
    }
    
    private boolean aksQrzAndRegister(Qrzsession qrzsession, String callsign) throws QrzException {
        Qrzreg qrzreg;
        QRZDatabaseDto qrzDtabaseDto = QrzUtil
                .callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzsession.getKey(), callsign));
        String error = qrzDtabaseDto.getSession().getError();
        if (error != null && !StaticValuesHelper.QRZ_ERROR_INVALID_SESSION_KEY.equals(error)) {
            log.warn(StaticValuesHelper.QRZ_NEW_SESSION_MESSAGE, error);
            qrzsession.setError(error);
            qrzsessionRepository.save(qrzsession);
            qrzsession = this.getSession();
            qrzDtabaseDto = QrzUtil.callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzsession.getKey(), callsign));
        }

        if (qrzDtabaseDto != null && qrzDtabaseDto.getCallsign() != null) {
            qrzreg = QrzUtil.parse(qrzDtabaseDto.getCallsign());
            qrzreg.setUpdatedAt(DateTimeUtil.getDateTime());
            qrzregRepository.save(qrzreg);
            return true;
        }
        return false;
    }

	@Override
	public String getCountryOfCallsign(String callsign) {
    	Qrzsession qrzsession;
		try {
			qrzsession = getSession();
		} catch (QrzException e) {
			log.error(e.getMessage());
			return null;
		}
		List<Qrzreg> qrzregList = this.getActiveQrzregList(callsign);

		try {
			if (qrzregList == null || qrzregList.isEmpty()) {
				aksQrzAndRegister(qrzsession, callsign);
				qrzregList = this.getActiveQrzregList(callsign);
			}
		} catch (QrzException e) {
			log.error(e.getMessage());
		}

		if (qrzregList != null && !qrzregList.isEmpty()) {
			Qrzreg res = qrzregList.stream().sorted(Comparator.comparingInt(Qrzreg::getId).reversed())
					.findFirst().get();
			return res.getCountry();
		}
		return null;
	}

    @Override
    public Boolean checkCallsignOnQrz(String callsign) throws QrzException {
    	Qrzsession qrzsession = getSession();
        List<Qrzreg> qrzregList = this.getActiveQrzregList(callsign);

        if (qrzregList == null || qrzregList.isEmpty()) {
        	return aksQrzAndRegister(qrzsession, callsign);
        }

        if (qrzregList != null && !qrzregList.isEmpty()) {
            return true;
        }
        return false;
    }
    
    @Override
    public List<Qrzreg> getQrzregOf(List<Qsl> qsls) {
        Calendar calendar = DateTimeUtil.getNowCalendar();
        calendar.add(Calendar.DATE, -qrzCallsignExp);
        List<String> finalList = new ArrayList<>();
        finalList.addAll(qsls.stream().map(Qsl::getTo).collect(Collectors.toList()));
        finalList.addAll(qsls.stream().map(Qsl::getVia).collect(Collectors.toList()));
        return qrzregRepository.findByCallsignsInPeriod(finalList, calendar.getTime());
    }
}
