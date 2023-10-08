package egd.fmre.qslbureau.capture.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ShipDto implements Serializable {
    private static final long serialVersionUID = 6948543984998931186L;
    private Integer id;
    private Date datetime;
    private Integer slotId;
    private Integer shippingMethodId;
    private Integer zoneId;
    private String address;
    private String trackingCode;
}
