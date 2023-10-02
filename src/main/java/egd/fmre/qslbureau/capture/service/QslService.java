package egd.fmre.qslbureau.capture.service;

import java.util.List;

import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import egd.fmre.qslbureau.capture.enums.QslstatusEnum;

public interface QslService {

    List<Qsl> getActiveQslsForSlot(Slot slot);
    
    Qsl save(Qsl qsl);

    List<Qsl> getBySlotAndStatus(Slot slot, List<QslstatusEnum> qslstatusEnumList);

}
