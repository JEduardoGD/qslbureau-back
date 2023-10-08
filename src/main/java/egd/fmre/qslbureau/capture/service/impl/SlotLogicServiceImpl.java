package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.BiPredicate;
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
    @Autowired SlotRepository      slotRepository;
    @Autowired LocalRepository     localRepository;
    @Autowired QrzService          qrzService;
    
    private static String MAX_NUMBER_SLOTS_REACHED = "Se ha alcanzado el número máximo de slots para este local";
    
    private Status slotstatusCreated;
    private Status slotstatusOpen;
    private Status slotstatusClosed;
    private Status slotstatusClosedForSend;
    private Status slotstatusSent;
    
    @PostConstruct
    private void Init(){
        slotstatusCreated       = new Status(SlotstatusEnum.CREATED.getIdstatus());
        slotstatusOpen          = new Status(SlotstatusEnum.OPEN.getIdstatus());
        slotstatusClosed        = new Status(SlotstatusEnum.CLOSED.getIdstatus());
        slotstatusClosedForSend = new Status(SlotstatusEnum.CLOSED_FOR_SEND.getIdstatus());
        slotstatusSent          = new Status(SlotstatusEnum.SENT.getIdstatus());
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
            return null;
        }
        if (slotStatus.getId().equals(slotstatusClosed.getId())) {
            log.warn("The status on slot id {} already is closed", slot.getId());
            return null;
        }

        slot.setClosedAt(DateTimeUtil.getDateTime());
        slot.setStatus(slotstatusClosed);
        if (createConfirmCode) {
            slot.setConfirmCode(RandomStringUtils.randomAlphabetic(6).toUpperCase());
            slot.setStatus(slotstatusClosedForSend);
        }
        return slotRepository.save(slot);
    }
    
    private int getNextSlotNumber(List<Status> slotStatuses, Local local) {
        Integer slotNumber = slotRepository.getLastSlotNumberByLocal(slotStatuses, local);
        if (slotNumber != null) {
            return slotNumber.intValue() + 1;
        }
        return 1;
    }
    
	@Override
	public Slot getSlotByCountry(String callsign, Local local) throws MaximumSlotNumberReachedException {
		String country = qrzService.getCountryOfCallsign(callsign);
		country= TextUtil.sanitize(country);
		List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
		List<Slot> slots = slotRepository.findByLocalAndCountryAndStatuses(country, slotStatuses, local);

		if (slots != null && !slots.isEmpty()) {
			return slots.stream().sorted(Comparator.comparingInt(Slot::getId).reversed()).findFirst().get();
		}

		int slotNumber = this.getNextSlotNumber(slotStatuses, local);
		Slot newSlot = generateSlotCountry(country, DateTimeUtil.getDateTime(), local, slotNumber);
		return slotRepository.save(newSlot);
	}
	
	@Override
	public void runCloseCloseableLocals(Local local) {
        //checking if some slot could be closed
	    List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        List<Slot> closeableList = new ArrayList<>();
        closeableList.addAll(openedOrCreatedSlotsInLocal);
        List<Integer> openedOrCreatedSlotsInLocalIdsInLocal = openedOrCreatedSlotsInLocal.stream().map(Slot::getId).collect(Collectors.toList());
        List<SlotCountqslDTO> slotCountList = getQslsBySlotIdList(openedOrCreatedSlotsInLocalIdsInLocal);
        List<Slot> slotsInUse = slotCountList.stream().map(SlotCountqslDTO::getSlot).collect(Collectors.toList());
        closeableList.removeAll(slotsInUse);
        closeableList.forEach(slot -> this.changeSlotstatusToClosed(slot, false));
	}
    
    //filter that happends in time
    public static BiPredicate<CallsignRule, Date> isOntime = (c, d) -> {
        if (c.getEnd() == null) {
            return c.getStart().before(d);
        }
        return c.getStart().before(d) && c.getEnd().after(d);
    };
	
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
        System.out.println(callsignTo);
        String newCallsignTo = this.applyCallsignRule(callsignTo);
        
        // find some slot open or created that is used by newCallsignTo
        List<Slot> openedOrCreatedSlotsForCallsignInLocal =
                getOpenedOrCreatedSlotsForCallsignInLocal(newCallsignTo, local);
        if (openedOrCreatedSlotsForCallsignInLocal != null && !openedOrCreatedSlotsForCallsignInLocal.isEmpty()) {
            return openedOrCreatedSlotsForCallsignInLocal.get(0);
        }
        
        this.runCloseCloseableLocals(local);
        
        List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        
        openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        
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
        return orderAndFilter(slots, slotstatusClosedForSend);
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
}

