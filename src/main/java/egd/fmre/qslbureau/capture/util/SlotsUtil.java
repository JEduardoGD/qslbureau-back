package egd.fmre.qslbureau.capture.util;

import java.util.Date;

import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;

public class SlotsUtil {
    protected Slot generateSlot(String callsignTo, Date open, Local local, int slotNumber) {
        Slot newSlot = new Slot();
        newSlot.setCallsignto(callsignTo);
        newSlot.setOpen(open);
        newSlot.setLocal(local);
        newSlot.setSlotNumber(slotNumber);
        return newSlot;
    }
}
