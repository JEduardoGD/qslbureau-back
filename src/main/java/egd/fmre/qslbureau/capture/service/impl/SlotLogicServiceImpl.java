package egd.fmre.qslbureau.capture.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.SlotstatusEnum;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.exception.QrzException;
import egd.fmre.qslbureau.capture.helper.StaticValuesHelper;
import egd.fmre.qslbureau.capture.repo.CallsignruleRepository;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.SlotRepository;
import egd.fmre.qslbureau.capture.service.QrzService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import egd.fmre.qslbureau.capture.util.SlotsUtil;
import egd.fmre.qslbureau.capture.util.TextUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlotLogicServiceImpl extends SlotsUtil implements SlotLogicService {

    @Autowired CallsignruleRepository callsignruleRepository;
    @Autowired SlotRepository         slotRepository;
    @Autowired LocalRepository        localRepository;
    @Autowired QrzService             qrzService;
    
    private static String MAX_NUMBER_SLOTS_REACHED = "Se ha alcanzado el número máximo de slots para este local";
    
    private Status slotstatusCreated;
    private Status slotstatusOpen;
    private Status slotstatusClosed;
    private Status slotstatusClosedForSend;
    private Status slotstatusSent;
    private Status slotstatusMovedToIntl;
    private Status slotstatusUnconfirmable;
    
    @PostConstruct
    private void Init(){
        slotstatusCreated       = new Status(SlotstatusEnum.CREATED.getIdstatus());
        slotstatusOpen          = new Status(SlotstatusEnum.OPEN.getIdstatus());
        slotstatusClosed        = new Status(SlotstatusEnum.CLOSED.getIdstatus());
        slotstatusClosedForSend = new Status(SlotstatusEnum.CLOSED_FOR_SEND.getIdstatus());
        slotstatusSent          = new Status(SlotstatusEnum.SENT.getIdstatus());
        slotstatusMovedToIntl   = new Status(SlotstatusEnum.MOVED_TO_INTERNATIONAL.getIdstatus());
        slotstatusUnconfirmable = new Status(SlotstatusEnum.UNCONFIRMABLE.getIdstatus());
    }
    
    @Override
    public List<Slot> getOpenedOrCreatedSlotsInLocal(Local local) {
        List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
        return slotRepository.findByStatusesForLocal(slotStatuses, local);
    }
    
    @Override
    public List<Slot> getOpenedOrCreatedSlotsForCallsignInLocal(String callsign, Local local) {
        List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        return openedOrCreatedSlotsInLocal.stream().filter(s -> callsign.equals(s.getCallsignto()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SlotCountqslDTO> getQslsBySlotIdList(List<Integer> slotIdList) {
        return slotRepository.getQslsBySlotIdList(slotIdList);
    }
    
    @Override
    public void changeSlotstatusToOpen(Slot slot) {
        Status slotStatus = slot.getStatus();
        if(!slotStatus.getId().equals(slotstatusCreated.getId())) {
            log.warn("The status on slot id {} is not created", slot.getId());
        }
        if(slotStatus.getId().equals(slotstatusOpen.getId())) {
            log.warn("The status on slot id {} already is open", slot.getId());
            return;
        }
        slot.setStatus(slotstatusOpen);
        slotRepository.save(slot);
    }
    
    @Override
    public Slot changeSlotstatusToClosed(Slot slot, boolean createConfirmCode) {
        Status slotStatus = slot.getStatus();

        if (!slotStatus.getId().equals(slotstatusOpen.getId())) {
            log.warn("The status on slot id {} is not open", slot.getId());
            return slot;
        }
        if (slotStatus.getId().equals(slotstatusClosed.getId())) {
            log.warn("The status on slot id {} already is closed", slot.getId());
            return slot;
        }
        
        slot.setStatus(slotstatusClosed);
        slot.setClosedAt(DateTimeUtil.getDateTime());

        if (createConfirmCode) {
            slot.setConfirmCode(RandomStringUtils.randomAlphabetic(6).toUpperCase());
            slot.setStatus(slotstatusClosedForSend);
        }
        return slotRepository.save(slot);
    }

    @Override
    public Slot changeSlotstatusToIntl(Slot slot) {
        Status slotStatus = slot.getStatus();

        if (!slotStatus.getId().equals(slotstatusClosedForSend.getId())) {
            log.warn("The status on slot id {} is not closed for send", slot.getId());
            return slot;
        }
        if (slotStatus.getId().equals(slotstatusMovedToIntl.getId())) {
            log.warn("The status on slot id {} already is moved to international", slot.getId());
            return slot;
        }
        
        slot.setStatus(slotstatusMovedToIntl);
        slot.setClosedAt(DateTimeUtil.getDateTime());

        return slotRepository.save(slot);
    }

    @Override
    public Slot changeSlotstatusToUnconfirmable(Slot slot) {
        Status slotStatus = slot.getStatus();

        if (!slotStatus.getId().equals(slotstatusClosedForSend.getId())) {
            log.warn("The status on slot id {} is not closed for send", slot.getId());
            return slot;
        }
        if (slotStatus.getId().equals(slotstatusUnconfirmable.getId())) {
            log.warn("The status on slot id {} already marked as unconfirmable", slot.getId());
            return slot;
        }
        
        slot.setStatus(slotstatusUnconfirmable);
        slot.setClosedAt(DateTimeUtil.getDateTime());

        return slotRepository.save(slot);
    }
    
	@Override
	public Slot getSlotByCountry(String callsign, Local local) throws MaximumSlotNumberReachedException {
		String country = null;
		try {
			country = qrzService.getCountryOfCallsign(callsign);
		} catch (QrzException e) {
			log.warn(e.getMessage());
		}
		country = TextUtil.sanitize(country);

		if (country == null) {
			return null;
		}
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slots = slotRepository.findByLocalAndCountryAndStatuses(country, slotStatuses, local);

		if (slots != null && !slots.isEmpty()) {
			return slots.stream().sorted(Comparator.comparingInt(Slot::getId).reversed()).findFirst().get();
		}
		
		List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        
        if (openedOrCreatedSlotsInLocal.size() <= 0 && local.getMaxSlots() > 0) {
            Slot newSlot = generateSlotCountry(country, DateTimeUtil.getDateTime(), local, 1);
            return slotRepository.save(newSlot);
        }
        openedOrCreatedSlotsInLocal.sort(Comparator.comparing(Slot::getSlotNumber));
        
        if (openedOrCreatedSlotsInLocal.size() >= local.getMaxSlots()) {
            throw new MaximumSlotNumberReachedException(MAX_NUMBER_SLOTS_REACHED);
        }
        
        int i = 1;
        for (Slot s : openedOrCreatedSlotsInLocal) {
            if (i == s.getSlotNumber()) {
                i++;
                continue;
            } else {
                Slot newSlot = generateSlot(null, DateTimeUtil.getDateTime(), local, i);
                return slotRepository.save(newSlot);
            }
        }
        Slot newSlot = generateSlot(null, DateTimeUtil.getDateTime(), local, i);
        return slotRepository.save(newSlot);
	}
	
    @Override
    public void runCloseCloseableSlots(Local local) {
        List<Integer> closeableSlotIds = slotRepository.gettingCloseableSlotIds(local.getId());
        List<Slot> closeableList = closeableSlotIds.stream().map(slotId -> slotRepository.findById(slotId).get())
                .collect(Collectors.toList());
        closeableList.forEach(slot -> this.changeSlotstatusToClosed(slot, false));
    }
    
    @Override
    public void runOpenOpenableSlots(Local local) {
        List<Integer> opneableSlotIds = slotRepository.gettingOpenableSlotIds(local.getId());
        List<Slot> opneableSlots = opneableSlotIds.stream().map(slotId -> slotRepository.findById(slotId).get())
                .collect(Collectors.toList());
        opneableSlots.forEach(slot -> this.changeSlotstatusToOpen(slot));
    }
	
    private String applyCallsignRule(String callsignTo) {
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
    public Slot getSlotForQsl(String callsignTo, Local local) throws MaximumSlotNumberReachedException {
        //apply rules of redirect
        String newCallsignTo = this.applyCallsignRule(callsignTo);
        
        // find some slot open or created that is used by newCallsignTo
        List<Slot> openedOrCreatedSlotsForCallsignInLocal =
                getOpenedOrCreatedSlotsForCallsignInLocal(newCallsignTo, local);
        if (openedOrCreatedSlotsForCallsignInLocal != null && !openedOrCreatedSlotsForCallsignInLocal.isEmpty()) {
            return openedOrCreatedSlotsForCallsignInLocal.get(0);
        }
        
        List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        
        if (openedOrCreatedSlotsInLocal.size() <= 0 && local.getMaxSlots() > 0) {
            Slot newSlot = generateSlot(newCallsignTo, DateTimeUtil.getDateTime(), local, 1);
            return slotRepository.save(newSlot);
        }
        openedOrCreatedSlotsInLocal.sort(Comparator.comparing(Slot::getSlotNumber));
        
        if (openedOrCreatedSlotsInLocal.size() >= local.getMaxSlots()) {
            throw new MaximumSlotNumberReachedException(MAX_NUMBER_SLOTS_REACHED);
        }
        
        int i = 1;
        for (Slot s : openedOrCreatedSlotsInLocal) {
            if (i == s.getSlotNumber()) {
                i++;
                continue;
            } else {
                Slot newSlot = generateSlot(newCallsignTo, DateTimeUtil.getDateTime(), local, i);
                return slotRepository.save(newSlot);
            }
        }
        Slot newSlot = generateSlot(newCallsignTo, DateTimeUtil.getDateTime(), local, i);
        return slotRepository.save(newSlot);
    }

    @Override
    public Slot findById(int slotId) {
        return slotRepository.findById(slotId).orElse(null);
    }
    
    @Override
    public List<Status> getCreatedAndOpenStatuses() {
        return Arrays.asList(slotstatusCreated, slotstatusOpen);
    }
  
	@Override
	public List<Slot> getOpenedOrCreatedSlots() {
        List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
        return slotRepository.findByStatusesForLocal(slotStatuses);
	}
  
  
    @Override
    public List<Slot> getSlotsOfLocal(Local local) {
        return slotRepository.findByLocal(local);
    }
  
    @Override
    public List<Slot> orderAndFilterForFront(List<Slot> slots) {
        return orderAndFilter(slots);
    }
  
  
    @Override
    public List<Slot> orderAndFilterReadyForSend(List<Slot> slots) {
        return orderAndFilter(slots, Arrays.asList(slotstatusClosedForSend, slotstatusSent));
    }
    
    @Override
    public Slot changeSlotstatusToSend(Slot slot) {
        Status slotStatus = slot.getStatus();
        if (!slotStatus.getId().equals(slotstatusClosedForSend.getId())) {
            log.warn("The status on slot id {} is not closed for send", slot.getId());
            return null;
        }
        if (slotStatus.getId().equals(slotstatusSent.getId())) {
            log.warn("The status on slot id {} already is on sent status", slot.getId());
            return null;
        }

        slot.setClosedAt(DateTimeUtil.getDateTime());
        slot.setStatus(slotstatusSent);
        return slotRepository.save(slot);
    }
    
	@Override
	public Slot getNullSlot() {
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slots = slotRepository.getNullSlots(slotStatuses);
		if (slots != null && !slots.isEmpty()) {
			return slots.get(StaticValuesHelper.ZERO);
		}
		return null;
	}
}

