package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;

public interface SlotLogicService {

    public Slot getSlotForQsl(String callsignTo, Local local) throws MaximumSlotNumberReachedException;
    
    public Slot findById(int slotId);

}
