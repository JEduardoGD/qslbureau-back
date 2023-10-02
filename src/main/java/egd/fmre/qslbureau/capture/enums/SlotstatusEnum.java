package egd.fmre.qslbureau.capture.enums;

import egd.fmre.qslbureau.capture.exception.StatusNotFoundException;

public enum SlotstatusEnum {
    CREATED(2001), OPEN(2002), CLOSED(2003), CLOSED_FOR_SEND(2004), SLOT_SENT(2005), SLOT_CONFIRMED(2006);

    int idstatus;

    SlotstatusEnum(int idstatus) {
        this.idstatus = idstatus;
    }

    public int getIdstatus() {
        return idstatus;
    }

    public SlotstatusEnum getByIdstatus(int idstatus) throws StatusNotFoundException {
        for (SlotstatusEnum s : SlotstatusEnum.values()) {
            if (s.getIdstatus() == idstatus) {
                return s;
            }
        }
        throw new StatusNotFoundException(String.format("Status not found for integer %s", idstatus));
    }

}
