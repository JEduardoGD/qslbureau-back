package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class CallsignRuleComparerDto implements Serializable {
    private static final long serialVersionUID = 122150419786529446L;
    private String qslTo;
    private String qslVia;
    private int originSlotId;
    private int destinationSlotId;
}
