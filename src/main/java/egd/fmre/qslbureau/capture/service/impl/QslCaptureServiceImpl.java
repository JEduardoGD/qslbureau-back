package egd.fmre.qslbureau.capture.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.JsonParserException;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.exception.SlotLogicServiceException;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;
import egd.fmre.qslbureau.capture.util.SlotsUtil;

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
    public StandardResponse captureQsl(QslDto qslDto) {
        Slot slot;
        
        Capturer capturer = capturerService.findById(idCapturer);

        try {
            slot = slotLogicService.getSlotForQsl(qslDto.getToCallsign());
            Qsl qsl = new Qsl();
            qsl.setCapturer(capturer);
            qsl.setCallsignTo(qslDto.getToCallsign());
            qsl.setDatetimecapture(new Date());
            qsl.setSlot(slot);
            qsl = qslRepository.save(qsl);
            qslDto.setSlotNumber(slot.getSlotNumber());
            Set<Qsl> qsls = qslRepository.findBySlot(slot);
            qslDto.setQslsInSlot(qsls.size());
            return new StandardResponse(JsonParserUtil.parse(qsl.toDto()));
        } catch (SlotLogicServiceException |JsonParserException e) {
            return new StandardResponse(true, e.getMessage());
        }
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
        if (slot == null) {
            throw new QslcaptureException("Slot es nulo");
        }
        Set<Qsl> qsls = qslRepository.findBySlot(slot);
        return SlotsUtil.parse(qsls);

    }

    @Override
    public List<QslDto> qslsByLocal(int localId) throws QslcaptureException {
        Local local = localRepository.findById(localId);

        Pageable sortedByPriceDesc = PageRequest.of(0, 20, Sort.by("id").descending());
        List<Qsl> qsls = qslRepository.findByPaggeable(local, sortedByPriceDesc);

        List<QslDto> qslsDtoList = SlotsUtil.parse(qsls).stream().collect(Collectors.toList());

        Collections.sort(qslsDtoList, new Comparator<QslDto>() {
            @Override
            public int compare(QslDto qslDto1, QslDto qslDto2) {
                return Integer.compare(qslDto2.getQslId(), qslDto1.getQslId());
            }
        });
        return qslsDtoList;
    }
}
