package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ShippingMethodDto implements Serializable {
    private static final long serialVersionUID = -5191829359443912691L;
    private Integer id;
    private String key;
    private String name;
    private String description;
    private boolean haveTracking;
    private String localPartner;
}
