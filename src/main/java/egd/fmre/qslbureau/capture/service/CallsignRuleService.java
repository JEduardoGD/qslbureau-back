package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.QslCallsignRule;
import egd.fmre.qslbureau.capture.dto.QslSlotTraslade;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;

public interface CallsignRuleService {

    String applyCallsignRule(String callsignTo);

    List<QslCallsignRule> getApplicableRules(List<Status> createdAndOpenSlotStatuses, List<Slot> allSlots);

    List<QslSlotTraslade> aplyRules(List<QslCallsignRule> aplicableRulesForLocal, boolean isSimulated);
}
