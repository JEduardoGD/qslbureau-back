package egd.fmre.qslbureau.capture.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.qrz.QRZDatabaseDto;
import egd.fmre.qslbureau.capture.dto.qrz.QrzSessionDto;
import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.exception.QrzException;
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

    @Override
    public Boolean checkCallsignOnQrz(Qrzsession qrzsession, String callsign) throws QrzException {
        Qrzreg qrzreg;

        Calendar calendar = DateTimeUtil.getNowCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        log.info(sdf.format(calendar.getTime()));

        calendar.add(Calendar.DATE, -qrzCallsignExp);
        log.info(sdf.format(calendar.getTime()));

        List<Qrzreg> qrzregList = qrzregRepository.findByCallsignInPeriod(callsign, calendar.getTime());

        if (qrzregList == null || qrzregList.isEmpty()) {
            QRZDatabaseDto qrzDtabaseDto = QrzUtil
                    .callToQrz(String.format(QrzUtil.QRZ_ASK_FOR_CALL, qrzsession.getKey(), callsign));
            if (qrzDtabaseDto != null && qrzDtabaseDto.getCallsign() != null) {
                qrzreg = QrzUtil.parse(qrzDtabaseDto.getCallsign());
                qrzreg.setUpdatedAt(DateTimeUtil.getDateTime());
                qrzregRepository.save(qrzreg);
                return true;
            }
        }

        if (qrzregList != null && !qrzregList.isEmpty()) {
            qrzreg = qrzregList.get(qrzregList.size() - 1);
            return true;
        }
        return false;
    }
}
