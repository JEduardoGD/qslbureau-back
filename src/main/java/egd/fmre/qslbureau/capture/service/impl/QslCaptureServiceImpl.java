package egd.fmre.qslbureau.capture.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dao.QslDao;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.SlotLogicServiceException;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;

@Service
public class QslCaptureServiceImpl implements QslCaptureService {
    
    @Autowired CallsignRuleService callsignRuleService;
    @Autowired SlotLogicService    slotLogicService;
    @Autowired QslRepository       qslRepository;
    @Autowired CapturerService     capturerService;
    
    @Value("${ID_CAPTURER}")
    private int idCapturer;
    
    @Override
    public QslDao captureQsl(QslDao qsl) {

        Slot slot;
        
        Capturer capturer = capturerService.findById(idCapturer);

        try {
            slot = slotLogicService.getSlotForQsl(qsl.getToCallsign());
            Qsl qslEntity = new Qsl();
            qslEntity.setCapturer(capturer);
            qslEntity.setCallsignTo(qsl.getToCallsign());
            qslEntity.setDatetimecapture(new Date());
            qslEntity.setSlot(slot);
            qslEntity = qslRepository.save(qslEntity);
        } catch (SlotLogicServiceException e) {
            return null;
        }

        return qsl;
    }
}
