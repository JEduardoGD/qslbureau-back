package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import egd.fmre.qslbureau.capture.entity.CallsignRule;
import egd.fmre.qslbureau.capture.entity.Qsl;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QslCallsignRule implements Serializable {
    private static final long serialVersionUID = 7146851653467933318L;
    private Qsl qsl;
    private CallsignRule callsignRule;
}
