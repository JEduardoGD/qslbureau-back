package egd.fmre.qslbureau.capture.enums;

import egd.fmre.qslbureau.capture.exception.StatusNotFoundException;

public enum SlotstatusEnum {
    SLOT_CREATED(2001), SLOT_OPEN(2002), SLOT_CLOSED(2003), SLOT_SENT(2004), SLOT_CONFIRMED(2005);

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
