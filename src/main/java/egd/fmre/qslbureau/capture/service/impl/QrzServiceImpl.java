package egd.fmre.qslbureau.capture.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.repo.QrzsessionRepository;
import egd.fmre.qslbureau.capture.service.QrzService;

@Service
public class QrzServiceImpl implements QrzService {
    @Value("${QRZSESSIONTTL}")
    private int qrzsessionttlindays;

    @Autowired
    QrzsessionRepository qrzsessionRepository;

    @Override
    public void getSession() {
        Qrzsession lastQrzsession = qrzsessionRepository.getLastSession();
        if (lastQrzsession == null) {
            Qrzsession qrzsession = new Qrzsession();
            
        }
    }
}
