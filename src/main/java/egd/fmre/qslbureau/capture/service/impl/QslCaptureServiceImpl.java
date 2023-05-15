package egd.fmre.qslbureau.capture.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
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
    public StandardResponse captureQsl(QslDto qsl) {
        
        StandardResponse sr;
        
        ResponseEntity<Qsl> entity;

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
            
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = new HashMap<>();
            map.put("date", new Date());
            map.put("localDateTime", LocalDateTime.now());
            map.put("localDate", LocalDate.now());
            
            return new StandardResponse(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map));
        } catch (SlotLogicServiceException e) {
            return sr;
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        Set<Qsl> qsls = qslRepository.findByLocal(local);
        List<QslDto> qslsDto = SlotsUtil.parse(qsls).stream().collect(Collectors.toList());

        Collections.sort(qslsDto, new Comparator<QslDto>() {
            @Override
            public int compare(QslDto qslDto1, QslDto qslDto2) {
                return Integer.compare(qslDto2.getQslId(), qslDto1.getQslId());
            }
        });
        return qslsDto;
    }
}
