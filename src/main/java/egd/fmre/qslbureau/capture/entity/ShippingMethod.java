package egd.fmre.qslbureau.capture.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "C_SHIPPINGMETHOD")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShippingMethod implements Serializable {

    private static final long serialVersionUID = -4091878249570461242L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSHIPPINGMETHOD")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "KEY")
    private String key;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "REQUIRE_ADDRESS")
    private boolean requireAddress;

    @Column(name = "HAVE_TRACKING")
    private boolean tracking;
}
