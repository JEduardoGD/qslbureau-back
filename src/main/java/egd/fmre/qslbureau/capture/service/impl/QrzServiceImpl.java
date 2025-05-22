package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.ContactDataDto;
import egd.fmre.qslbureau.capture.dto.qrz.QRZDatabaseDto;
import egd.fmre.qslbureau.capture.dto.qrz.QrzCallsignDto;
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

@Service
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
    
    
	private void manageQrzError(Qrzsession qrzSession, String qrzDtabaseDtoError) throws QrzException {
		if (qrzDtabaseDtoError == null || StaticValuesHelper.EMPTY_STRING.equals(qrzDtabaseDtoError)) {
			return;
		}
		if (StaticValuesHelper.QRZ_ERROR_INVALID_SESSION_KEY.equals(qrzDtabaseDtoError)
				|| StaticValuesHelper.QRZ_ERROR_SESSION_TIMEOUT.equals(qrzDtabaseDtoError)) {
			qrzSession.setError(qrzDtabaseDtoError);
			qrzsessionRepository.save(qrzSession);
		}
	}
    
	private boolean aksQrzAndRegister(String callsign) throws QrzException {
		Qrzsession qrzSession = this.getSession();
		QRZDatabaseDto qrzDtabaseDto = QrzUtil
				.callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzSession.getKey(), callsign));

		String error = qrzDtabaseDto.getSession().getError();

		if (StaticValuesHelper.QRZ_ERROR_INVALID_SESSION_KEY.equals(error)
				|| StaticValuesHelper.QRZ_ERROR_SESSION_TIMEOUT.equals(error)) {
			manageQrzError(qrzSession, error);
			qrzSession = this.getSession();
			qrzDtabaseDto = QrzUtil.callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzSession.getKey(), callsign));
		}

		Qrzreg qrzreg;
		if (qrzDtabaseDto != null && qrzDtabaseDto.getCallsign() != null) {
			qrzreg = QrzUtil.parse(qrzDtabaseDto.getCallsign());
			qrzreg.setUpdatedAt(DateTimeUtil.getDateTime());
			qrzregRepository.save(qrzreg);
			return true;
		}
		return false;
	}

	@Override
	public String getCountryOfCallsign(String callsign) throws QrzException {
		List<Qrzreg> qrzregList = this.getActiveQrzregList(callsign);

		if (qrzregList == null || qrzregList.isEmpty()) {
			aksQrzAndRegister(callsign);
			qrzregList = this.getActiveQrzregList(callsign);
		}

		if (qrzregList != null && !qrzregList.isEmpty()) {
			Qrzreg qrzreg = qrzregList.stream()
					.sorted(Comparator.comparingInt(Qrzreg::getId).reversed())
					.findFirst()
					.get();
			return qrzreg.getCountry();
		}
		return null;
	}

    @Override
    public Boolean checkCallsignOnQrz(String callsign) throws QrzException {
        List<Qrzreg> qrzregList = this.getActiveQrzregList(callsign);

        if (qrzregList == null || qrzregList.isEmpty()) {
        	return aksQrzAndRegister(callsign);
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
    
	@Override
	public ContactDataDto getEmailFromQrz(String callsign) throws QrzException {
		Qrzsession qrzSession = this.getSession();
		QRZDatabaseDto qrzDtabaseDto = QrzUtil
				.callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzSession.getKey(), callsign));

		String error = qrzDtabaseDto.getSession().getError();

		if (StaticValuesHelper.QRZ_ERROR_INVALID_SESSION_KEY.equals(error)
				|| StaticValuesHelper.QRZ_ERROR_SESSION_TIMEOUT.equals(error)) {
			manageQrzError(qrzSession, error);
			qrzSession = this.getSession();
			qrzDtabaseDto = QrzUtil.callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzSession.getKey(), callsign));
		}

		QrzCallsignDto qrzCallsignDto = qrzDtabaseDto.getCallsign();
		ContactDataDto contactDataDto = null;
		if (qrzCallsignDto != null) {
			contactDataDto = new ContactDataDto();
			contactDataDto.setIdContact(null);
			contactDataDto.setName(qrzCallsignDto.getFname());
			contactDataDto.setSurename(qrzCallsignDto.getName());
			contactDataDto.setCallsign(qrzCallsignDto.getCall());
			contactDataDto.setAddress(qrzCallsignDto.getAddr1()+"\n"+qrzCallsignDto.getAddr2());
			contactDataDto.setEmail(qrzCallsignDto.getEmail());
			contactDataDto.setWhatsapp(null);
			contactDataDto.setWantemail(null);
			contactDataDto.setStart(DateTimeUtil.getDateTime());
			contactDataDto.setEnd(null);
			//contactDataDto.setListOf(null);
		}
		return contactDataDto;
	}
}
