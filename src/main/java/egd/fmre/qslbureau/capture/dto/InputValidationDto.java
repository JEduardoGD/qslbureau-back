package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class InputValidationDto implements Serializable {
    private static final long serialVersionUID = 7563870497186665763L;
    private Integer shipId;
    private Integer idSlot;
    private Integer shippingMethodId;
    private Integer capturerId;
    private String address;
    private Integer regionalRepresentativeId;
    private String trackingCode;
    private Boolean valid;
    private String error;
}
