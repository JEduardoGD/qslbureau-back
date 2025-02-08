package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ZoneruleDto implements Serializable {
    private static final long serialVersionUID = -413777445796175481L;
    private Integer id;
    private Integer zoneId;
    private String callsign;
    private Date start;
    private Date end;
}
