package egd.fmre.qslbureau.capture.service.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.repo.CallsignruleRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;

@Service
public class CallsignRuleServiceImpl implements CallsignRuleService {

    @Autowired
    CallsignruleRepository callsignruleRepository;
    
    //filter that happends in time
    public static BiPredicate<CallsignRule, Date> isOntime = (c, d) -> {
        if (c.getEnd() == null) {
            return c.getStart().before(d);
        }
        return c.getStart().before(d) && c.getEnd().after(d);
    };

    @Override
    public String applyCallsignRule(String callsignTo) {
        
        Date nowDate = new Date();

        // get applicable rules for callsignTo
        List<CallsignRule> callsignRules = callsignruleRepository.findByCallsignTo(callsignTo);
        
        if(callsignRules.size() <= 0) {
            return callsignTo;
        }
        
        //filter an apply only last rule
        CallsignRule aplicableRule = callsignRules.stream()
                .filter(c -> isOntime.test(c, nowDate))
                .sorted(Comparator.comparingInt(CallsignRule::getId))
                .reduce((f, s) -> s)
                .orElse(null);
        
        return aplicableRule == null ? callsignTo : aplicableRule.getCallsignRedirect();
    }
}
