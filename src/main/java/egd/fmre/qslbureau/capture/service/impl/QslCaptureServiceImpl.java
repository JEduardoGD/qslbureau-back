package egd.fmre.qslbureau.capture.service.impl;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.exception.SlotLogicServiceException;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
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
    @Autowired LocalRepository     localRepository;
    
    @Value("${ID_CAPTURER}")
    private int idCapturer;
    
    @Override
    public QslDto captureQsl(QslDto qsl) {

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
            qsl.setSlotNumber(slot.getSlotNumber());
            Set<Qsl> qsls = qslRepository.findBySlot(slot);
            qsl.setQslsInSlot(qsls.size());
        } catch (SlotLogicServiceException e) {
            return null;
        }

        return qsl;
    }

    @Override
    public Integer countQslsBySlot(int slotId) throws QslcaptureException {
        Slot slot = slotLogicService.findById(slotId);
        if(slot==null) {
            throw new QslcaptureException("Slot es nulo");
        }
        Set<Qsl> qsls = qslRepository.findBySlot(slot);
        return qsls.size();
    }

    @Override
    public Set<QslDto> qslsBySlot(int slotId) throws QslcaptureException {
        Slot slot = slotLogicService.findById(slotId);
        if(slot==null) {
            throw new QslcaptureException("Slot es nulo");
        }
        Set<Qsl> qsls = qslRepository.findBySlot(slot);
        return qsls.stream().map(q -> {
            QslDto qslDto = new QslDto();
            qslDto.setQslId(q.getId());
            qslDto.setToCallsign(q.getCallsignTo());
            qslDto.setSlotNumber(slot.getSlotNumber());
            return qslDto;
        }).collect(Collectors.toSet());
        
    }
    
    @Override
    public Set<QslDto> qslsByLocal(int localId) throws QslcaptureException {
        Local local = localRepository.findById(localId);
        return null;
    }
}
