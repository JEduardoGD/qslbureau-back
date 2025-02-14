package egd.fmre.qslbureau.capture.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.entity.Status;
import egd.fmre.qslbureau.capture.enums.SlotstatusEnum;

public class SlotsUtil {
    protected Slot generateSlot(String callsignTo, Date createdAt, Local local, int slotNumber) {
        Slot newSlot = new Slot();
        newSlot.setLocal(local);
        newSlot.setCallsignto(callsignTo);
        newSlot.setSlotNumber(slotNumber);
        newSlot.setCreatedAt(createdAt);
        newSlot.setStatus(new Status(SlotstatusEnum.CREATED.getIdstatus()));
        return newSlot;
    }

    protected Slot generateSlotCountry(String country, Date createdAt, Local local, int slotNumber) {
        Slot newSlot = new Slot();
        newSlot.setLocal(local);
        newSlot.setCountry(country);
        newSlot.setSlotNumber(slotNumber);
        newSlot.setCreatedAt(createdAt);
        newSlot.setStatus(new Status(SlotstatusEnum.CREATED.getIdstatus()));
        return newSlot;
    }

    protected List<Slot> orderAndFilter(List<Slot> slots) {
        Map<Integer, Slot> map = new HashMap<>();
        slots = slots.stream().sorted(Comparator.comparingInt(Slot::getSlotNumber)).collect(Collectors.toList());
        for (Slot slot : slots) {
            map.put(slot.getSlotNumber(), slot);
        }
        return new ArrayList<Slot>(map.values());
    }

    protected List<Slot> orderAndFilter(List<Slot> slots, List<Status> statuses) {
        return slots.stream().sorted(Comparator.comparingInt(Slot::getSlotNumber))
                .filter(s -> statuses.contains(s.getStatus()))
                .collect(Collectors.toList());
    }
    
    //filter that happends in time
    protected static BiPredicate<CallsignRule, Date> isOntime = (c, d) -> {
        if (c.getEnd() == null) {
            return c.getStart().before(d);
        }
        return c.getStart().before(d) && c.getEnd().after(d);
    };
}
