package egd.fmre.qslbureau.capture.service;

import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.exception.MaximumSlotNumberReachedException;

public interface SlotLogicService {

    public Slot getSlotForQsl(String callsignTo) throws MaximumSlotNumberReachedException;
    
    public Slot findById(int slotId);

}
