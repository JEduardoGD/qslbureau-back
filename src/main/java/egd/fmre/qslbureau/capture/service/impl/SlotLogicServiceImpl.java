package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.SlotstatusEnum;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.SlotRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.DateTimeUtil;
import egd.fmre.qslbureau.capture.util.SlotsUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlotLogicServiceImpl extends SlotsUtil implements SlotLogicService {
    
    @Autowired CallsignRuleService callsignRuleService;
    @Autowired SlotRepository      slotRepository;
    @Autowired LocalRepository     localRepository;
    
    private static String MAX_NUMBER_SLOTS_REACHED = "Se ha alcanzado el número máximo de slots para este local";
    
    private Status slotstatusCreated;
    private Status slotstatusOpen;
    private Status slotstatusClosed;
    
    @PostConstruct
    private void Init(){
        slotstatusCreated = new Status(SlotstatusEnum.SLOT_CREATED.getIdstatus());
        slotstatusOpen    = new Status(SlotstatusEnum.SLOT_OPEN.getIdstatus());
        slotstatusClosed  = new Status(SlotstatusEnum.SLOT_CLOSED.getIdstatus());
    }
    
    @Override
    public List<Slot> getOpenedOrCreatedSlotsInLocal(Local local) {
        List<Status> slotStatuses = Arrays.asList(slotstatusCreated, slotstatusOpen);
        return slotRepository.findByStatusesForLocal(slotStatuses, local);
    }
    
    @Override
    public List<Slot> getOpenedOrCreatedSlotsForCallsignInLocal(String callsign, Local local) {
        List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        return openedOrCreatedSlotsInLocal.stream().filter(s -> callsign.equals(s.getCallsignto())).collect(Collectors.toList());
    }
    
    @Override
    public List<SlotCountqslDTO> getQslsBySlot(List<Integer> SlotsInLocalIds) {
        return slotRepository.getQslsBySlot(SlotsInLocalIds);
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
    public Slot getSlotForQsl(String callsignTo, Local local) throws MaximumSlotNumberReachedException {
        //apply rules of redirect
        String newCallsignTo = callsignRuleService.applyCallsignRule(callsignTo);
        
        // find some slot open or created that is used by newCallsignTo
        List<Slot> openedOrCreatedSlotsForCallsignInLocal =
                getOpenedOrCreatedSlotsForCallsignInLocal(newCallsignTo, local);
        if (openedOrCreatedSlotsForCallsignInLocal != null && !openedOrCreatedSlotsForCallsignInLocal.isEmpty()) {
            return openedOrCreatedSlotsForCallsignInLocal.get(0);
        }

        
        List<Slot> openedOrCreatedSlotsInLocal = getOpenedOrCreatedSlotsInLocal(local);
        List<Slot> closeableList = new ArrayList<>();
        closeableList.addAll(openedOrCreatedSlotsInLocal);

        
        //checking if some slot could be closed
        List<Integer> openedOrCreatedSlotsInLocalIdsInLocal = openedOrCreatedSlotsInLocal.stream().map(Slot::getId).collect(Collectors.toList());
        List<SlotCountqslDTO> slotCountList = getQslsBySlot(openedOrCreatedSlotsInLocalIdsInLocal);
        List<Slot> slotsInUse = slotCountList.stream().map(SlotCountqslDTO::getSlot).collect(Collectors.toList());
        closeableList.removeAll(slotsInUse);
        closeableList.forEach(s->{
            s.setClosedAt(DateTimeUtil.getDateTime());
            s.setStatus(slotstatusClosed);
            slotRepository.save(s);
        });
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
}

