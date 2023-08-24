package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import egd.fmre.qslbureau.capture.entity.Slot;
import lombok.Data;

@Data
public class SlotCountqslDTO implements Serializable {
    private static final long serialVersionUID = 4451965407810986166L;
    private Slot slot;
    private long c;

    public SlotCountqslDTO(Slot slot, long c) {
        super();
        this.slot = slot;
        this.c = c;
    }

}
