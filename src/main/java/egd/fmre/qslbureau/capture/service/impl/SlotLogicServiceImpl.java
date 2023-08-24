package egd.fmre.qslbureau.capture.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import egd.fmre.qslbureau.capture.dto.SlotCountqslDTO;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;
import egd.fmre.qslbureau.capture.repo.LocalRepository;
import egd.fmre.qslbureau.capture.repo.SlotRepository;
import egd.fmre.qslbureau.capture.service.CallsignRuleService;
import egd.fmre.qslbureau.capture.service.SlotLogicService;
import egd.fmre.qslbureau.capture.util.SlotsUtil;

@Service
public class SlotLogicServiceImpl extends SlotsUtil implements SlotLogicService {
    
    @Autowired CallsignRuleService callsignRuleService;
    @Autowired SlotRepository      slotRepository;
    @Autowired LocalRepository     localRepository;
    
    private static String MAX_NUMBER_SLOTS_REACHED = "Se ha alcanzado el número máximo de slots para este local";
    
    @Value("${LOCAL_ID}")
    private int localId;
    
    @Override
    public Slot getSlotForQsl(String callsignTo) throws MaximumSlotNumberReachedException {
        
        Local local = localRepository.findById(localId);
        
        //apply rules of redirect
        String newCallsignTo = callsignRuleService.applyCallsignRule(callsignTo);
        
        //find some slot open that is used by newCallsignTo
        Slot slot = slotRepository.getOpenedSlotForCallsign(newCallsignTo, local);
        
        if (slot != null) {
            return slot;
        }

        List<Slot> openedSlotsInLocal = slotRepository.getOpenedSlotsInLocal(local);
        List<Slot> closeableList = new ArrayList<>();
        closeableList.addAll(openedSlotsInLocal);

        
        //checking if some slot could be closed
        List<Integer> openedSlotsIdsInLocal = openedSlotsInLocal.stream().map(Slot::getId).collect(Collectors.toList());
        List<SlotCountqslDTO> slotCountList = slotRepository.getQslsBySlot(openedSlotsIdsInLocal);
        List<Slot> slotsInUse = slotCountList.stream().map(SlotCountqslDTO::getSlot).collect(Collectors.toList());
        closeableList.removeAll(slotsInUse);
        closeableList.forEach(s->{
            s.setClosed(new Date());
            slotRepository.save(s);
        });
        openedSlotsInLocal = slotRepository.getOpenedSlotsInLocal(local);
        
        if (openedSlotsInLocal.size() <= 0 && local.getMaxSlots() > 0) {
            Slot newSlot = generateSlot(newCallsignTo, new Date(), local, 1);
            return slotRepository.save(newSlot);
        }
        openedSlotsInLocal.sort(Comparator.comparing(Slot::getSlotNumber));
        
        if (openedSlotsInLocal.size() >= local.getMaxSlots()) {
            throw new MaximumSlotNumberReachedException(MAX_NUMBER_SLOTS_REACHED);
        }
        
        int i = 1;
        for (Slot s : openedSlotsInLocal) {
            if (i == s.getSlotNumber()) {
                i++;
                continue;
            } else {
                Slot newSlot = generateSlot(newCallsignTo, new Date(), local, i);
                return slotRepository.save(newSlot);
            }
        }
        Slot newSlot = generateSlot(newCallsignTo, new Date(), local, i);
        return slotRepository.save(newSlot);
    }

    @Override
    public Slot findById(int slotId) {
        return slotRepository.findById(slotId).orElse(null);
    }
}
