package egd.fmre.qslbureau.capture.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.dto.SummaryQslDto;
import egd.fmre.qslbureau.capture.entity.Capturer;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qrzreg;
import egd.fmre.qslbureau.capture.entity.Qrzsession;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;
import egd.fmre.qslbureau.capture.exception.QrzException;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.exception.SlotLogicServiceException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.CapturerService;
import egd.fmre.qslbureau.capture.service.QrzService;
import egd.fmre.qslbureau.capture.service.QslCaptureService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;
import egd.fmre.qslbureau.capture.util.QsldtoTransformer;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QslCaptureServiceImpl implements QslCaptureService {
    
    @Autowired CallsignRuleService callsignRuleService;
    @Autowired SlotLogicService    slotLogicService;
    @Autowired QslRepository       qslRepository;
    @Autowired CapturerService     capturerService;
    @Autowired LocalRepository     localRepository;
    @Autowired QrzService          qrzService;
    
    private Status statusQslVigente;
    private Status statusQslEliminada;
    
    @PostConstruct
    private void Init(){
        statusQslVigente = new Status(QslstatusEnum.QSL_VIGENTE.getIdstatus());
        statusQslEliminada = new Status(QslstatusEnum.QSL_ELIMINADA.getIdstatus());
    }
    
    
    @Override
    public QslDto captureQsl(QslDto qslDto) throws QslcaptureException {
        Slot slot;
        
        Capturer capturer = capturerService.findById(qslDto.getIdCapturer());
        Local local = localRepository.findById(qslDto.getLocalId());
        Boolean qslToRecordFound = null;
        Boolean qslViaRecordFound = null;
        
        try {
            Qrzsession qrzsession = qrzService.getSession();
            qslToRecordFound = qrzService.checkCallsignOnQrz(qrzsession, qslDto.getTo());
            if (qslDto.getVia() != null && !StaticValuesHelper.EMPTY_STRING.equals(qslDto.getVia())) {
                qslViaRecordFound = qrzService.checkCallsignOnQrz(qrzsession, qslDto.getVia());
            }
        } catch (QrzException e) {
            log.error(e.getMessage());
        }
        

        try {
            String effectiveCallsign = qslDto.getVia() != null
                    && !StaticValuesHelper.EMPTY_STRING.equals(qslDto.getVia()) ? qslDto.getVia() : qslDto.getTo();
            slot = slotLogicService.getSlotForQsl(effectiveCallsign, local);
            slotLogicService.changeSlotstatusToOpen(slot);
            Qsl qsl = QsldtoTransformer.map(qslDto, capturer, slot, statusQslVigente);
            qsl = qslRepository.save(qsl);
            Set<Qsl> qsls = qslRepository.findBySlot(slot);
            QslDto qslDtoRet = QsldtoTransformer.map(qsl);
            qslDtoRet.setDateTimeCapture(qsl.getDatetimecapture());
            qslDtoRet.setQslsInSlot(qsls.size());
            qslDtoRet.setQslToRecordFound(qslToRecordFound);
            qslDtoRet.setQslViaRecordFound(qslViaRecordFound);
            return qslDtoRet;
        } catch (SlotLogicServiceException e) {
            throw new QslcaptureException(e);
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
        return QsldtoTransformer.map(qsls);

    }

    @Override
    public List<QslDto> qslsByLocal(int localId) throws QslcaptureException {
        Local local = localRepository.findById(localId);

        Pageable sortedByPriceDesc = PageRequest.of(0, 20, Sort.by("id").descending());
        List<Qsl> qsls = qslRepository.findByPaggeable(local, sortedByPriceDesc);
        
        List<Qrzreg> qrzregs = qrzService.getQrzregOf(qsls);

        List<QslDto> qslsDtoList = QsldtoTransformer.map(qsls, qrzregs).stream().collect(Collectors.toList());

        Collections.sort(qslsDtoList, new Comparator<QslDto>() {
            @Override
            public int compare(QslDto qslDto1, QslDto qslDto2) {
                return Integer.compare(qslDto2.getQslId(), qslDto1.getQslId());
            }
        });
        return qslsDtoList;
    }

    @Override
    public StandardResponse deleteById(int qslid) throws QslcaptureException {
        Qsl qsl = qslRepository.findById(qslid).orElse(null);

        if (qsl == null) {
            throw new QslcaptureException("No se encuentra la QSL solicitada");
        }

        if (statusQslVigente.equals(qsl.getStatus())) {
            qsl.setStatus(statusQslEliminada);
            qsl = qslRepository.save(qsl);
            QslDto qslDtoRet = QsldtoTransformer.map(qsl);
            return new StandardResponse(JsonParserUtil.parse(qslDtoRet));
        } else {
            log.warn(String.format("La QSL con ID %s ya tiene estatus eliminada", qslid));
            QslDto qslDtoRet = QsldtoTransformer.map(qsl);
            return new StandardResponse(JsonParserUtil.parse(qslDtoRet));
        }
    }
    
    @Override
    public SummaryQslDto getActiveQslsForCallsign(String callsign) {
        Set<Qsl> qslsForCallsign = qslRepository.findQslsInSystem(callsign, callsign, statusQslVigente,
                slotLogicService.getCreatedAndOpenStatuses());
        
        SummaryQslDto summaryQslDto = new SummaryQslDto();
        summaryQslDto.setCallsign(callsign);
        
        if(qslsForCallsign == null || qslsForCallsign.isEmpty()) {
            summaryQslDto.setCount(StaticValuesHelper.ZERO);
            return summaryQslDto;
        }
        
        Date oldest = qslsForCallsign.stream().map(Qsl::getDatetimecapture).min(Date::compareTo).get();
        Date newest = qslsForCallsign.stream().map(Qsl::getDatetimecapture).max(Date::compareTo).get();
        int count = qslsForCallsign.size();
        summaryQslDto.setOldest(oldest);
        summaryQslDto.setNewest(newest);
        summaryQslDto.setCount(count);
        
        return summaryQslDto;
    }
}































