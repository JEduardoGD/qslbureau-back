package egd.fmre.qslbureau.capture.dto.qrz;

import java.io.Serializable;

import lombok.Data;

@Data
public class CallsignDAO implements Serializable {

    private static final long serialVersionUID = -930142369143069808L;
    private String call;
    private Long dxcc;
    private String state;
    private Integer ituzone;
    private Integer cqzone;
    private String country;
    private String cont;
}
