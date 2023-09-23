package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.QslCallsignRule;
import egd.fmre.qslbureau.capture.dto.QslSlotTraslade;
import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.repo.CallsignruleRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.QslService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CallsignRuleServiceImpl implements CallsignRuleService {

    @Autowired CallsignruleRepository callsignruleRepository;
    @Autowired QslService             qslService;
    @Autowired SlotLogicService             slotLogicService;
    
    //filter that happends in time
    public static BiPredicate<CallsignRule, Date> isOntime = (c, d) -> {
        if (c.getEnd() == null) {
            return c.getStart().before(d);
        }
        return c.getStart().before(d) && c.getEnd().after(d);
    };
    
    @Override
    public String applyCallsignRule(String callsignTo) {
        // get applicable rules for callsignTo
        List<CallsignRule> callsignRules = callsignruleRepository.getActiveRulesForCallsign(DateTimeUtil.getDateTime(),
                callsignTo);
        
        if(callsignRules.size() <= 0) {
            return callsignTo;
        }
        
        //filter an apply only last rule
        CallsignRule aplicableRule = callsignRules.stream()
                .filter(c -> isOntime.test(c, DateTimeUtil.getDateTime()))
                .sorted(Comparator.comparingInt(CallsignRule::getId))
                .reduce((f, s) -> s)
                .orElse(null);
        
        return aplicableRule == null ? callsignTo : aplicableRule.getCallsignRedirect();
    }
    
    @Override
    public List<QslCallsignRule> getApplicableRules(List<Status> createdAndOpenSlotStatuses, List<Slot> allSlots) {
        List<CallsignRule> activeCallsignRules = callsignruleRepository.getActiveRules(DateTimeUtil.getDateTime());

        List<QslCallsignRule> qslCallsignRuleList = new ArrayList<>();

        List<Slot> activeSlots = allSlots.stream().filter(s -> {
            List<Integer> createdAndOpenSlotStatusesIds = createdAndOpenSlotStatuses.stream().map(Status::getId)
                    .collect(Collectors.toList());
            return createdAndOpenSlotStatusesIds.contains(s.getStatus().getId());
        }).collect(Collectors.toList());
        for (Slot activeSlot : activeSlots) {
            List<Qsl> activeQsls = qslService.getActiveQslsForLocal(activeSlot);
            for (Qsl activeQsl : activeQsls) {
                String effectiveCallsign = activeQsl.getVia() != null ? activeQsl.getVia() : activeQsl.getTo();
                CallsignRule activeCallsignRule = activeCallsignRules.stream()
                        .filter(c -> c.getCallsignTo().equals(effectiveCallsign)).findFirst().orElse(null);
                if (activeCallsignRule != null) {
                    qslCallsignRuleList.add(new QslCallsignRule(activeQsl, activeCallsignRule));
                }
            }
        }
        return qslCallsignRuleList;
    }
    
    @Override
    public List<QslSlotTraslade> aplyRules(List<QslCallsignRule> aplicableRulesForLocal, boolean isSimulated) {
        List<QslSlotTraslade> trasladosList = new ArrayList<>();
        for (QslCallsignRule aplicableRule : aplicableRulesForLocal) {
            String callsignRedirect = aplicableRule.getCallsignRule().getCallsignRedirect();
            Qsl qsl = aplicableRule.getQsl();

            QslSlotTraslade qslSlotTraslade = new QslSlotTraslade();

            Slot oldSlot = slotLogicService.findById(qsl.getSlot().getId());

            qslSlotTraslade.setQsl(qsl);
            qslSlotTraslade.setOldSlot(oldSlot);

            Local local = qsl.getSlot().getLocal();
            Slot newSlot = null;
            try {
                newSlot = slotLogicService.getSlotForQsl(callsignRedirect, local);
            } catch (MaximumSlotNumberReachedException e) {
                log.error(e.getMessage());
            }
            if (newSlot == null) {
                log.warn("newSlot is null");
                continue;
            }
            if (newSlot.getSlotNumber() == oldSlot.getSlotNumber()) {
                log.warn("newSlot and oldSlot are same");
                continue;
            }
            if(!isSimulated) {
                qsl.setSlot(newSlot);
                qsl = qslService.save(qsl);
                qslSlotTraslade.setNewSlot(qsl.getSlot());
                slotLogicService.runCloseCloseableLocals(local);
            } else {
                qslSlotTraslade.setNewSlot(newSlot);
            }
            trasladosList.add(qslSlotTraslade);

        }
        return trasladosList;
    }
}
