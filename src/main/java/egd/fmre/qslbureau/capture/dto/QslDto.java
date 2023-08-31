package egd.fmre.qslbureau.capture.dto;

import java.util.Date;

import lombok.Data;

@Data
public class QslDto {
    private int idCapturer;
    private int qslId;
    private String to;
    private String via;
    private int slotNumber;
    private int qslsInSlot;
    private Date dateTimeCapture;
    private int status;
    private int localId;
}
