package egd.fmre.qslbureau.capture.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.service.QslService;

@Service
public class QslServiceImpl implements QslService {
    
    @Autowired QslRepository qslRepository;
    
    private Status statusQslVigente;
    
    @PostConstruct
    private void Init(){
        statusQslVigente = new Status(QslstatusEnum.QSL_VIGENTE.getIdstatus());
    }
    
    @Override
    public List<Qsl> getActiveQslsForSlot(Slot slot) {
        Set<Qsl> qsls = qslRepository.findBySlot(slot);
        return qsls.stream().filter(q -> statusQslVigente.equals(q.getStatus())).collect(Collectors.toList());
        
    }

    @Override
    public Qsl save(Qsl qsl) {
        return qslRepository.save(qsl);
    }

    @Override
    public List<Qsl> getBySlotAndStatus(Slot slot, List<QslstatusEnum> qslstatusEnumList) {
        List<Status> statuseList = qslstatusEnumList.stream().map(s -> new Status(s.getIdstatus()))
                .collect(Collectors.toList());
        return qslRepository.findBySlotAndStatuses(slot, statuseList);
    }
}
