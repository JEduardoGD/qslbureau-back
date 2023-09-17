package egd.fmre.qslbureau.capture.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egd.fmre.qslbureau.capture.dto.CallsignRuleDto;
import egd.fmre.qslbureau.capture.dto.QslCallsignRule;
import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.dto.QslSlotTraslade;
import egd.fmre.qslbureau.capture.dto.SlotDto;
import egd.fmre.qslbureau.capture.dto.StandardResponse;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.exception.QslcaptureException;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.LocalService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.JsonParserUtil;
import egd.fmre.qslbureau.capture.util.QsldtoTransformer;

@RestController
@RequestMapping("aplicablerules")
public class RulesController {
    
    @Autowired CallsignRuleService callsignRuleService;
    @Autowired SlotLogicService slotLogicService;
    @Autowired LocalService localService;


    @GetMapping("/{localid}")
    public List<QslCallsignRule> getApplicableRules(@PathVariable int localid) throws QslcaptureException {
        List<Status> slotStatus = slotLogicService.getCreatedAndOpenStatuses();
        Local local = localService.getById(localid);
        List<Slot> allSlots = slotLogicService.getSlotsOfLocal(local);
        return callsignRuleService.getApplicableRules(slotStatus, allSlots);
    }


    @GetMapping("/applyforlocal/{localid}")
    public ResponseEntity<StandardResponse> applyForLocal(@PathVariable int localid) throws QslcaptureException {
        List<Status> slotStatus = slotLogicService.getCreatedAndOpenStatuses();
        Local local = localService.getById(localid);
        List<Slot> allSlots = slotLogicService.getSlotsOfLocal(local);
        List<QslCallsignRule> aplicableRulesForLocal = callsignRuleService.getApplicableRules(slotStatus, allSlots);
        List<QslSlotTraslade> qslSlotTrasladeList = callsignRuleService.aplyRules(aplicableRulesForLocal);
        
        List<CallsignRuleDto> callsignRuleDtoList = qslSlotTrasladeList.stream().map(q -> {
            QslDto qslDto = QsldtoTransformer.map(q.getQsl());
            SlotDto newSlotDto = QsldtoTransformer.map(q.getNewSlot());
            SlotDto oldSlotDto = QsldtoTransformer.map(q.getOldSlot());
           return new CallsignRuleDto(qslDto, newSlotDto, oldSlotDto);
        }).collect(Collectors.toList());
        

        StandardResponse standardResponse;
        try {
            standardResponse = new StandardResponse(JsonParserUtil.parse(callsignRuleDtoList));
        } catch (QslcaptureException e) {
            standardResponse = new StandardResponse(true, e.getMessage());
        }
        return new ResponseEntity<StandardResponse>(standardResponse, new HttpHeaders(), HttpStatus.CREATED);
    }
}
