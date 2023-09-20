package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import egd.fmre.qslbureau.capture.entity.Qsl;
import egd.fmre.qslbureau.capture.entity.Slot;
import lombok.Data;

@Data
public class QslSlotTraslade implements Serializable {
    private static final long serialVersionUID = 8104808187002874131L;
    private Qsl qsl;
    private Slot oldSlot;
    private Slot newSlot;
}
