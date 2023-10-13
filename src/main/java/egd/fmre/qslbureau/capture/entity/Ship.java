package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "T_SHIP")
public class Ship implements Serializable {

    private static final long serialVersionUID = -2394359175484220680L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSHIP")
    private Integer id;

    @Column(name = "DATETIME")
    private Date datetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDSLOT")
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDSHIPPINGMETHOD")
    private ShippingMethod shippingMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDCAPTURER")
    private Capturer capturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDZONE")
    private Zone zone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "TRACKING_CODE")
    private String trackingCode;
}
