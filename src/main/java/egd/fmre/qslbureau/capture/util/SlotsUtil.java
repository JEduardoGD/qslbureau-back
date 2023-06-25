package egd.fmre.qslbureau.capture.util;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import egd.fmre.qslbureau.capture.dto.QslDto;
import egd.fmre.qslbureau.capture.entity.Local;
import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;

public abstract class SlotsUtil {
    protected Slot generateSlot(String callsignTo, Date open, Local local, int slotNumber) {
        Slot newSlot = new Slot();
        newSlot.setCallsignto(callsignTo);
        newSlot.setOpen(open);
        newSlot.setLocal(local);
        newSlot.setSlotNumber(slotNumber);
        return newSlot;
    }
    
    public static Set<QslDto> parse(List<Qsl> qsls) {
        return qsls.stream().map(q -> {
            QslDto qslDto = new QslDto();
            qslDto.setQslId(q.getId());
            qslDto.setToCallsign(q.getCallsignTo());
            qslDto.setSlotNumber(q.getSlot().getSlotNumber());
            qslDto.setDateTimeCapture(q.getDatetimecapture());
            return qslDto;
        }).collect(Collectors.toSet());
    }
    
    public static Set<QslDto> parse(Set<Qsl> qsls) {
        return qsls.stream().map(q -> {
            QslDto qslDto = new QslDto();
            qslDto.setQslId(q.getId());
            qslDto.setToCallsign(q.getCallsignTo());
            qslDto.setSlotNumber(q.getSlot().getSlotNumber());
            qslDto.setDateTimeCapture(q.getDatetimecapture());
            return qslDto;
        }).collect(Collectors.toSet());
    }
}
