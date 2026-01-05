package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class EmailRepresentativeDTO implements Serializable {/**
     * 
     */
    private static final long serialVersionUID = -2702745308665758718L;
    
    private String zoneName;
    private String emailAddress;
    private Integer representativeId;
    private String representativeName;
    private String representativeLastName;
}
