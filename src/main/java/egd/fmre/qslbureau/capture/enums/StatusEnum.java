package egd.fmre.qslbureau.capture.enums;

import egd.fmre.qslbureau.capture.exception.StatusNotFoundException;

public enum StatusEnum {
    QSL_VIGENTE(1001), QSL_ELIMINADA(1002);

    int idstatus;

    StatusEnum(int idstatus) {
        this.idstatus = idstatus;
    }

    public int getIdstatus() {
        return idstatus;
    }

    public StatusEnum getByIdstatus(int idstatus) throws StatusNotFoundException {
        for (StatusEnum s : StatusEnum.values()) {
            if (s.getIdstatus() == idstatus) {
                return s;
            }
        }
        throw new StatusNotFoundException(String.format("Status not found for integer %s", idstatus));
    }
}
