package egd.fmre.qslbureau.capture.util;

import java.util.Date;

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
        newSlot.setStatus(new Status(SlotstatusEnum.SLOT_CREATED.getIdstatus()));
        return newSlot;
    }
}
