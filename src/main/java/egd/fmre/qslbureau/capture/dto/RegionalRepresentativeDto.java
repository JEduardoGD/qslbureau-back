package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class RegionalRepresentativeDto implements Serializable {
    private static final long serialVersionUID = -7940713732991747452L;
    private Integer id;
    private String name;
    private String lastname;
    private String username;
}
