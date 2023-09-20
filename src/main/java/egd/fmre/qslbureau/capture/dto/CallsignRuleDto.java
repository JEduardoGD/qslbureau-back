package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CallsignRuleDto implements Serializable {
    private static final long serialVersionUID = 3949228604338019192L;
    public CallsignRuleDto(QslDto qslDto, SlotDto newSlotDto, SlotDto oldSlotDto) {
        super();
        this.qslDto = qslDto;
        this.newSlotDto = newSlotDto;
        this.oldSlotDto = oldSlotDto;
    }
    private QslDto qslDto;
    private SlotDto newSlotDto;
    private SlotDto oldSlotDto;
}
