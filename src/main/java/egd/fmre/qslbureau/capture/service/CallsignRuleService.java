package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.dto.QslCallsignRule;
import egd.fmre.qslbureau.capture.dto.QslRuleDto;
import egd.fmre.qslbureau.capture.dto.QslSlotTraslade;
import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Status;

public interface CallsignRuleService {

    String applyCallsignRule(String callsignTo);

    List<QslSlotTraslade> aplyRules(List<QslCallsignRule> aplicableRulesForLocal, boolean isSimulated);

	List<QslCallsignRule> getApplicableRules(List<Status> createdAndOpenSlotStatuses, Local local);

	List<QslRuleDto> getQslsRules(int idlocal);

	List<CallsignRule> findActiveByCallsignRedirect(String callsignRedirect);
}
