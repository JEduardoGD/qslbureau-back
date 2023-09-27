package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SlotDto implements Serializable {
    private static final long serialVersionUID = 4409299532677686878L;
    private int id;
    private int localId;
    private String callsignto;
    private int slotNumber;
    private String country;
    private Date createdAt;
    private Date closedAt;
    private int statusId;
    private int qslsInSlot;
}
