package egd.fmre.qslbureau.capture.dto;

import java.util.Date;

import lombok.Data;

@Data
public class QslDto {
    private int idCapturer;
    private int qslId;
    private String toCallsign;
    private int slotNumber;
    private int qslsInSlot;
    private Date dateTimeCapture;
    private int status;
}
