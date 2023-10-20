package egd.fmre.qslbureau.capture.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;
import egd.fmre.qslbureau.capture.repo.QslRepository;
import egd.fmre.qslbureau.capture.service.QslService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;

@Service
public class QslServiceImpl implements QslService {
    
    @Autowired QslRepository qslRepository;
    @Autowired SlotLogicService slotLogicService;
    
    private Status qslStatusVigente;
    
    @PostConstruct
    private void Init(){
    	qslStatusVigente = new Status(QslstatusEnum.QSL_VIGENTE.getIdstatus());
    }
    
    @Override
    public List<Qsl> getActiveQslsForSlot(Slot slot) {
        Set<Qsl> qsls = qslRepository.findBySlot(slot);
        return qsls.stream().filter(q -> qslStatusVigente.equals(q.getStatus())).collect(Collectors.toList());
        
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

    
	@Override
	public List<Qsl> listOfActiveQslsWithCallsign(List<String> callsigns, Local local) {
		List<Status> createdOrOppenedSlotStatus = slotLogicService.getCreatedAndOpenStatuses();
		return qslRepository.findQslsInSystem(callsigns, qslStatusVigente, createdOrOppenedSlotStatus, local);
	}

	@Override
	public Qsl getById(Integer id) {
        return qslRepository.findById(id).orElse(null);
	}
}
